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
#   Original code: Sasha Milosavljevic(ED)
#                  
import time,datetime

from UNS.UNSConstants import REMOVED_ROLE_TEMPLATE,REMOVED_CHANNEL_TEMPLATE
from UNS.util.ORMPY.Session import Session
from UNS.ERA.Channel import Channel
from UNS.ERA.Choosable import Choosable 
from UNS.ERA.EEAUser import EEAUser
from UNS.util.ORMPY.BasePopo import BasePopo
from UNS.util.ORMPY.exceptions import *
from UNS.util.ORMPY.SqlFunction import MySqlFunction
from UNS.Logging import getLogger
from UNS.exceptions import ValidationException
from UNS.queries.qchannels import *
from UNS.ERA.DeliveryType import DeliveryType
from UNS.ERA.ChannelDt import ChannelDt
from UNS.ERA.ChannelRole import ChannelRole
from UNS.ERA.Subscription import Subscription
from UNS.Notifications.DeliveryHandler import *
from UNS.Notifications import *
from UNS.ERA.Event import Event
from UNS.ERA.Notification import Notification
from UNS.ERA.NotificationTemplate import NotificationTemplate
from UNS.ERA.EEARole import EEARole



class ChannelFacade:

    """
         The Facade Class for channels related operations.
    """

    logger=getLogger("UNS.ChannelFacade")
    init_last_harvest_date = "1990-01-01 00:00:00"


    def getChannels(self,mode,orderBy = None):
        """
            Returns a list of the existing channels in selected mode.
            @param mode: Mode of the channels : PULL or PUSH.
            @param orderBy: Tuple  with two items : sorting column and the order direction.
            @type mode: C{str}
            @type orderBy: C{tuple}
            @rtype: C{list}
            @return: a C{list} of the L{Channel} objects.
        """
        session = None
        try:
            session=Session()
            return session.findCustom(BasePopo,existing_channels_q,[mode],["ID","NAME","DESCRIPTION","MODE","SUBS_COUNT","LAST_HARVEST","EXT_USER_ID","CREATION_DATE","SECONDARY_ID","CSTATUS"],orderBy)
        finally:
            if(session):session.close()


    def getChannel(self,id):
        """
            Returns a channel with primary key equal C{id}.
            @param id: ID of the channel
            @type id: long
            @return: a L{Channel} object
            @rtype: L{Channel}
        """
        session = None
        try:
            session=Session()
            channel = session.findByPK(Channel,[int(id)])   
        finally:
            if(session):session.close()        
        return channel

    
    def createPullChannel(self, channel, uid, channel_metadata, user_groups, template_id, delivery_types):
        """ 
            Creates a new pull channel in the database.
            If C{template_id} is None, automaticly assigns default template to the channel.
            @param channel: The pull channel which will be created.
            @type channel: L{Channel}
            @param uid: LDAP uid attribute of the user or user_name in the case of the Zope user.
            @type uid: C{str}
            @param channel_metadata: Metadata of the C{channel} which may be filtered.
            @type channel_metadata: C{list} of the C{str}
            @param user_groups: List of the user groups which are allowed to subscribe to the C{channel}.
            @type user_groups: C{list} of the C{str}
            @param template_id: ID of the notificaion's template assigned to the C{channel}.
            @type template_id: C{int}
            @param delivery_types: List of the delivery types allowed for users.
            @type delivery_types: C{list} of the C{int} 
        """
        session = None
        try:
            try: 
                channel['LAST_HARVEST']= self.init_last_harvest_date
                channel['CREATION_DATE']=MySqlFunction('UTC_TIMESTAMP()')
                session=Session()
                user = session.findCustom(EEAUser,user_q,[uid])[0]
                channel["CREATOR"] = user["ID"]
                channel["CSTATUS"] = 1                
                channel["NOTIFICATION_TEMPLATE_ID"] = template_id or 1
                session.create(channel)
                channel['SECONDARY_ID']=str( channel['ID']*2+1 )+str( int( time.time() ) )
                session.update( channel)
                
                if(channel_metadata != None):
                    self._updateChannelMetadata(channel ,channel_metadata , session)

                for gid in user_groups:
                    ch_role = ChannelRole()
                    ch_role["CHANNEL_ID"] = channel["ID"] 
                    ch_role["EEA_ROLE_ID"] = gid
                    session.create(ch_role)
                                            
                for dt_id in delivery_types:
                    ch_dt = ChannelDt()
                    ch_dt["DELIVERY_TYPE_ID"] = dt_id
                    ch_dt["CHANNEL_ID"] = channel["ID"] 
                    session.create(ch_dt)
                    
                session.commitTransaction()
            except ORMPyIntegrityError:       
                raise ValidationException ,'Channel with url "%s" alredy exist' % channel["FEED_URL"]
        finally:
            if(session):session.close()


    def deleteChannel(self,channel):
        """ 
            Deletes channel from database.
            @param channel: The channel that will be deleted
            @type channel: L{Channel}
        """
        session = None
        try:
            session=Session()
            subscriptions = session.findCustom(BasePopo,user_subscripions_for_channel_q,[channel['ID']],["ID","EXT_USER_ID","USER_FULL_NAME","PREFER_HTML","ADDRESS","CHANNEL_NAME"])
            template = session.findByPK(NotificationTemplate,[REMOVED_CHANNEL_TEMPLATE])
            self.sendUnsubscribeNotification(subscriptions,template)
            session.remove(channel)
            session.commitTransaction();
        finally:
            if(session):session.close()        

    def sendUnsubscribeNotification(self,subscriptions,template):
        """
            Sends notifications to users who will be unsubscribed due access rights changes or channel removal.
            @param template: Template for notifications
            @type template: L{NotificationTemplate}
            @param subscriptions: List of the subscriptions that will be removed
            @type subscriptions: C{list} of the L{Subscription} 
        """
        session = None
        try:
            em = EMAIL(None)
            em.connect()
            for subs in subscriptions:
                event = Event()
                event["CREATION_DATE"] = datetime.datetime.now()
                event['META_DATA_TUPLES'] =[]
                notification = Notification()
                notification['SUBJECT'], notification['CONTENT'],notification['HTML_CONTENT']= prepareText(template, event, subs)
                address = subs["ADDRESS"]
                em.send(address, notification)
            em.disconnect()            
        except Exception, e:
            logger.error(e)
        

    def updateChannel(self,channel,channel_metadata, user_groups, template_id, delivery_types,ldap_facade):
        """ 
            Updates channel in the database.
            @param channel: The pull channel which will be updated.
            @type channel: L{Channel}
            @param channel_metadata: Metadata of the C{channel} which may be filtered.
            @type channel_metadata: C{list} of the C{str}
            @param user_groups: List of the user groups which are allowed to subscribe to the C{channel}.
            @type user_groups: C{list} of the C{str}
            @param template_id: ID of the notificaion's template assigned to the C{channel}.
            @type template_id: C{int}
            @param delivery_types: List of the delivery types allowed for users.
            @type delivery_types: C{list} of the C{int}
            @param ldap_facade: The instance of the LDAPFacade class
            @type ldap_facade: C{LDAPFacade}            
        """
        session = None
        try:
            try:
                session=Session()
                if(channel["MODE"] == "PUSH"):
                    channel["FEED_URL"] = None
                    channel["REFRESH_DELAY"] = 1
                channel["NOTIFICATION_TEMPLATE_ID"] = template_id or 1
                session.update(channel)
                if(channel_metadata != None):
                    self._updateChannelMetadata(channel ,channel_metadata , session)


                old_groups = self.getChannelUserGroups(channel["ID"])
                old_groups_ids = [ch_role["EEA_ROLE_ID"] for ch_role in old_groups]
                remove_groups = filter(lambda group: not user_groups.count(group["EEA_ROLE_ID"]) ,old_groups)
                add_groups = filter(lambda group_id: not old_groups_ids.count(group_id) ,user_groups)
        
        
                if((remove_groups and user_groups) or (not old_groups and user_groups)):
                    subscriptions = session.findCustom(BasePopo,user_subscripions_for_channel_q,[channel['ID']],["ID","EXT_USER_ID","USER_FULL_NAME","PREFER_HTML","ADDRESS","CHANNEL_NAME"])
                    invalid_subscriptions = []
                    for subs in subscriptions:
                        user_roles = ldap_facade.getUserLdapRoles(subs["EXT_USER_ID"])
                        if (not self._hasValidRoles(user_groups,user_roles)):
                            invalid_subscriptions.append(subs)
                            sb = Subscription()
                            sb["ID"] = subs["ID"]
                            session.remove(sb)

                    template = session.findByPK(NotificationTemplate,[REMOVED_ROLE_TEMPLATE])
                    self.sendUnsubscribeNotification(invalid_subscriptions,template)


                for group in remove_groups:
                    session.remove(group)

                        
                for gid in add_groups:
                    ch_role = ChannelRole()
                    ch_role["CHANNEL_ID"] = channel["ID"]
                    ch_role["EEA_ROLE_ID"] = gid
                    session.create(ch_role)
    
                old_delivery_types = self.getChannelDeliveryTypes(channel["ID"])
                old_delivery_types_ids = [dt["ID"] for dt in old_delivery_types]
                remove_ch_dt = filter(lambda dt: not delivery_types.count(dt["ID"]) ,old_delivery_types)
                add_ch_dt = filter(lambda ch_dt_id: not old_delivery_types_ids.count(ch_dt_id) ,delivery_types)
        
                for dt in remove_ch_dt:
                    session.remove(session.findByPK(ChannelDt, [channel["ID"],dt["ID"]]))
        
                for dt_id in add_ch_dt:
                    ch_dt = ChannelDt()
                    ch_dt["DELIVERY_TYPE_ID"] = dt_id
                    ch_dt["CHANNEL_ID"] = channel["ID"]
                    session.create(ch_dt)
    
                    
                session.commitTransaction()
            except ORMPyIntegrityError:
                raise ValidationException ,'Channel with url "%s" alredy exist' % channel["FEED_URL"]
        finally:
            if(session):session.close()
        
    def _hasValidRoles(self,channel_roles,user_roles):
        session = None
        try:
            session=Session()
            for ch_role_id in channel_roles:
                role = session.findByPK(EEARole,[ch_role_id])
                ch_role_name = role["EXT_ID"]
                for user_role in user_roles:
                    if user_role == ch_role_name:
                        return True                    
        finally:
            if(session):session.close()
        return False
        

       
    def getChoosableMetadata(self, channel):  
        """ 
            Returns all channel choosable metadata, that is metadata elements on which users may create filters.
            @param channel: Channel that owns metadata elements.
            @type channel: L{Channel}
            @rtype: C{list}
            @return: a C{list} of the L{Choosable} objects.
        """
        session = None
        try:
            session=Session()
            return session.findCustom(Choosable,choosable_metadata_q,[channel['ID']])
        finally:
            if(session):session.close()        


    def _updateChannelMetadata(self,channel,channel_metadata , session):
        """ 
            Updates old channel metadata or create new one
        """
        old_metadata = self.getChoosableMetadata(channel)
        old_property_ids = [ch["PROPERTY"] for ch in old_metadata]
        remove_metadata = filter(lambda ch: not channel_metadata.count(ch["PROPERTY"]) ,old_metadata)

        for ch in remove_metadata:
            existing_filters =  session.findCustom(BasePopo,existing_filters_q,[ch["PROPERTY"],channel["ID"]],["COUNT"])[0]["COUNT"]
            if(existing_filters):
                raise ValidationException ,'Filters based on property "%s" are activated.\nThis property on the channel "%s" can not be deselected' % ( ch["PROPERTY"],channel["NAME"])
            session.remove(ch)
        
        
        add_metadata = filter(lambda property: not old_property_ids.count(property) ,channel_metadata)


        for property in add_metadata:
            ch = Choosable()
            ch["PROPERTY"] = property
            ch["CHANNEL_ID"] = channel["ID"]
            session.create(ch)
            
    def distinctProperties(self,ch_id):
        """
            Returns distinct properties (metadata) occurred in the genrated channel's events.
            @param ch_id: ID of the channel.
            @type ch_id: long
            @rtype: C{list}
            @return: a C{list} of the L{BasePopo} objects.
        """
        session = None
        try:
            session=Session()
            return  session.findCustom(BasePopo,distinct_properties_q,[ch_id],["PROPERTY"]) 
        finally:
            if(session):session.close()


    def changeStatus(self,ch_id,status):
        """ 
            Changes status of the push channel.
            @param ch_id: ID of the channel.
            @type ch_id: long
            @param status: New status of the channel : 0 - disabled or 1 - enabled.
            @type status: int
        """        
        session = None
        try:
            session=Session()
            channel = session.findByPK(Channel,[ch_id]) 
            channel["CSTATUS"] = status
            session.update(channel)
            session.commitTransaction()
            return channel
        finally:
            if(session):session.close()

                        
    def getAllDeliveryTypes(self):
        """
            Returns all delivery types available for the users.
            @rtype: C{list}
            @return: a C{list} of the L{DeliveryType} objects.      
        """
        session = None
        try:                
            session=Session()
            return session.findAll(DeliveryType)
        finally:
            if(session):session.close()


    def getChannelDeliveryTypes(self,ch_id):
        """
            Returns delivery types which are allowed by definition of the channel.
            @param ch_id: ID of the channel.
            @type ch_id: C{long}            
            @rtype: C{list}
            @return: a C{list} of the L{DeliveryType} objects.      
        """
        session = None
        try:                
            session=Session()
            return session.findCustom(DeliveryType,channel_delivery_types_q,[ch_id])
        finally:
            if(session):session.close()


    def getChannelUserGroups(self, ch_id):
        """
            Returns a LDAP user groups (roles) which members are allowed to subscribe on the channel
            @param ch_id: ID of the channel.
            @type ch_id: C{long}
            @rtype: C{list}
            @return: a C{list} of the L{ChannelRole} objects.            
        """
        session = None
        try:        
            session = Session()
            return session.findCustom(ChannelRole,channel_user_groups_q,[ch_id])
        finally:
            if(session):session.close()
        