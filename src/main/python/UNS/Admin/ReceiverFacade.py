# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy of
# the License at http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# The Original Code is Reportnet Unified Notification Service
#
# The Initial Owner of the Original Code is European Environment
# Agency (EEA).  Portions created by European Dynamics (ED) company are
# Copyright (C) by European Environment Agency.  All
# Rights Reserved.
#
# Contributor(s):
#   Original code: Nedeljko Pavlovic (ED)

import os, time, string, sys

#UNS imports
from UNS.util.ORMPY.Session import Session
from UNS.util.ORMPY.SqlFunction import MySqlFunction
from UNS.util.ORMPY.BasePopo import BasePopo
from UNS.Harvester.Picker import Puller
from UNS.queries.qharvest import *
from UNS.queries.qchannels import *
from UNS.util.RDFUtil import *
from UNS.Subscription.SubscriptionFacade import SubscriptionFacade
from UNS.util.ORMPY.exceptions import ORMPyIntegrityError
from UNS.ERA.Filter import Filter
from UNS.ERA.Statement import Statement
from UNS.ERA.Channel import Channel
from UNS.ERA.ChannelRole import ChannelRole
from UNS.ERA.Choosable import Choosable
from UNS.ERA.EEAUser import EEAUser
from UNS.ERA.EEARole import EEARole
from UNS.ERA.DeliveryAddress import DeliveryAddress
from UNS.ERA.Subscription import Subscription
from UNS.exceptions import *

from UNS.Logging import getLogger

logger=getLogger( "UNS.ReceiverFacade" )


class ReceiverFacade:
    """ """
    
    def __init__(self):
        self.collector=RDFCollector()
        self.subs_facade=SubscriptionFacade()
        

                
        
        
    def handleCreate( self, username, name, description ):
        """ """
        session = None
        channel=Channel()
        channel['LAST_HARVEST']= "1990-01-01 00:00:00"
        channel['CREATION_DATE']=MySqlFunction( 'UTC_TIMESTAMP()' )
        channel['MODE']='PUSH'
        channel['NAME']=name
        channel['DESCRIPTION']=description
        channel['CSTATUS']=0
        channel['NOTIFICATION_TEMPLATE_ID']=1
        
        try:
            session=Session()
            rpcUser=self.__handleUserExistence(session, username, None, None)
            channel['CREATOR']=rpcUser['ID']
            session.create( channel )
            channel['SECONDARY_ID']=str( channel['ID']*2+1 )+str( int( time.time() ) )
            session.update( channel )
            session.customUpdate(qInsertAllDT % channel['ID'] )
            session.commitTransaction()
            return channel['SECONDARY_ID']
        finally:
            if( session ):session.close()
            

    def handleUpdate( self, ch_id,description):
        """ """
        session = None        
        try:
            session=Session()
            channel = session.findByPK(Channel,[ch_id])
            channel['DESCRIPTION']=description
            session.update(channel)
            session.commitTransaction()
        finally:
            if( session ):session.close()
            
            
    def push( self, rpc_user, secondaryId, rdf_input, input_form ):
        """ """
        session = None
        try:
            session=Session()
            channel=self.__getPushChannel(session, rpc_user, secondaryId)
            channel['LAST_HARVEST']=MySqlFunction( 'UTC_TIMESTAMP()' )
            session.update( channel )
            session.commitTransaction()     
            if input_form==0: things= self.collector.collectRdfPayload(rdf_input)
            elif input_form==1: things= self.collector.getEventsC(rdf_input)
            else: pass
            Puller(channel).insertEvents(session, things)
        finally:
            if session: session.close()
            
            
    def canUserSubscribe(self, rpc_user, secondaryId, user_roles):
        """ """
        q=qUserChannelRoles
        result, session=True, None
        try:        
            session=Session()
            channel=self.__getPushChannel(session, rpc_user, secondaryId)
            channel_roles = session.findCustom(ChannelRole,channel_user_groups_q,[channel['ID']])                
            if len(channel_roles)>0:
                if len(user_roles)==0: result=False
                else:
                    roles_list_s = ""
                    for u_role in user_roles: roles_list_s+= '"%s",' % u_role      
                    q = (q % (channel['ID'], roles_list_s[:-1]))    
                    roles_check=session.findCustom(BasePopo,q,[],['RESULT'])[0]['RESULT']
                    if roles_check==0: result=False
            return result
        finally:
            if(session):session.close()
            
            
    def subscribeUser( self, rpc_user, secondaryId, ldap_user, username, filters ):
        """ 
        Creates new or updates existing subscription to the specified channel for the specified user. 
        If subscription for provided channel and user already exists it will be  updated, otherwise it will be created. 
        """
        session = None
        try:
            session=Session()
            uns_user=self.__handleUserExistence(session, username, ldap_user.getProperty("cn"), ldap_user.getProperty("mail"))
            channel=self.__getPushChannel(session, rpc_user, secondaryId)
            aaa="select * from SUBSCRIPTION S where S.CHANNEL_ID=%s and S.EEA_USER_ID in (select U.ID from EEA_USER U where U.EXT_USER_ID=%s)"
            user_subscribtion=session.findCustom(Subscription, aaa , [channel['ID'],username], ['ID','EEA_USER_ID','CHANNEL_ID','LEAD_TIME','CREATION_DATE','SECONDARY_ID'] )
            filters=self.__cleanFilterDuplicates(filters)
            for filter in filters:
                for predicate in filter.keys():
                    try:
                        choosable_pred=Choosable()
                        choosable_pred['CHANNEL_ID']=channel['ID']
                        choosable_pred['PROPERTY']=predicate
                        session.create(choosable_pred)
                        session.commitTransaction()
                    except ORMPyIntegrityError:
                        """ Nothing to do channel's choosable element already exists"""
            if user_subscribtion:
                logger.debug("Updating subscription...")
                subscribtion=user_subscribtion[0]
                # The follwing code is reserved for the future use
                #old_subdts=self.subs_facade.getSubsDeliveryTypes(subscribtion["ID"])
                #old_subdts_ids = [sdt["DELIVERY_TYPE_ID"] for sdt in old_subdts]
                #self.subs_facade.updateSubscription(subscribtion,old_subdts_ids,{},session)
            else:
                logger.debug("Creating subscription...")
                subscribtion = Subscription()
                subscribtion['CHANNEL_ID'] = channel['ID']
                self.subs_facade.createSubscription(username,subscribtion,[1],{},session)
            filters=self.__cleanExistingFilters(filters,subscribtion["ID"])
            if filters:
                for filt in filters:
                     user_filter=Filter()
                     user_filter['STATEMENTS'], user_filter['SUBSCRIPTION_ID']=[], subscribtion['ID']
                     for predicate in filt.keys():
                         stmt=Statement()
                         stmt['PROPERTY'], stmt['VALUE']=predicate, filt[predicate]
                         stmt['RELATION']=1
                         user_filter['STATEMENTS'].append(stmt)
                     if user_filter['STATEMENTS']: self.subs_facade.addFilter(user_filter, session)

            session.commitTransaction()
        finally:
            if session: session.close()
        
        
           
    def getChannels( self, user, orderBy ):
        """
        Returns list of push channels that are created by the a rpc user
        """
        session = None
        try:
            session=Session()
            return session.findCustom( BasePopo, rpc_user_channels_q, [user], ["ID", "NAME", "DESCRIPTION", "MODE", "SUBS_COUNT", "LAST_HARVEST", "CREATION_DATE", "SECONDARY_ID", "CSTATUS"], orderBy )
        finally:
            if( session ):session.close()
        
        
    def __handleUserExistence(self, session, username, cn, email_addr ):
        """ """
        uns_users = session.findCustom( EEAUser, "from EEA_USER where EXT_USER_ID=%s", [username] )  
        if uns_users: uns_user=uns_users[0]
        else:
            uns_user=EEAUser()
            uns_user['EXT_USER_ID']=username
            uns_user["CN"] = cn or username
            uns_user['VACATION_FLAG']=0
            uns_user["PREFER_HTML"] = 0
            session.create( uns_user )
            if email_addr:
                    da = DeliveryAddress()
                    da["EEA_USER_ID"] = uns_user["ID"]
                    da["DELIVERY_TYPE_ID"] = 1
                    da["ADDRESS"] = email_addr
                    session.create(da)
            session.commitTransaction()     
        return uns_user      
        
    def __getPushChannel(self, session, rpc_user, secondaryId):
        """ """
        result = session.findCustom( Channel, q_find_channel_sid, [secondaryId] )   
        if not result: raise LookupError( "Channel doesn't exist" )
        channel=result[0]
        if channel['CSTATUS']==0: raise ValueError( "Channel is disabled" )
        uns_users = session.findCustom( EEAUser, "from EEA_USER where EXT_USER_ID=%s", [rpc_user] )  
        if not(uns_users and channel['CREATOR']==uns_users[0]['ID']): raise NotChOwnerException("Not channel owner")
        return channel
        
    def __cleanFilterDuplicates(self, filters):
        f_tmp_list=[]
        for filter in filters: 
            if filter not in f_tmp_list: f_tmp_list.append(filter)
        return f_tmp_list
        
    def __cleanExistingFilters(self, filters, subs_id):
        """ """
        tmp_list, r, uns_filt=[], [], SubscriptionFacade().getSubscriptionFilters(subs_id)
        for filter in uns_filt.values():
            stmtdict={}
            for stmt in filter['STATEMENTS']:
                if stmt['PROPERTY']: stmtdict[stmt['PROPERTY']]=stmt['VALUE']
            if stmtdict and stmtdict not in tmp_list: tmp_list.append(stmtdict)
        for filter in filters:
            if filter not in tmp_list: r.append(filter)
        return r
        
    def getChannel(self,secondaryId):
        """ """
        session = None
        try:
            session=Session()
            result = session.findCustom( Channel, q_find_channel_sid, [secondaryId])
            return result and result[0] or None
        finally:
            if( session ):session.close()