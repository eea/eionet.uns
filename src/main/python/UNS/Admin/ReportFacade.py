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
#   Original code: Sasa Milosavljevic(ED)
#                  


from UNS.util.ORMPY.Session import Session
from UNS.util.ORMPY.BasePopo import BasePopo
from UNS.ERA.DeliveryType import DeliveryType
from UNS.ERA.Channel import Channel
from UNS.ERA.EEAUser import EEAUser
from UNS.queries.qreports import *


class ReportFacade:

    """
         The Facade Class for generation of the reports.
    """
      
    def throughputReport(self,from_date,to_date,channel,user,orderBy):
        """ 
            Generates report regarding throughput of notifications.
            @param from_date: First date of the period.
            @param to_date: Last date of the period.
            @param channel: ID of the channel for which report is generated.
            @param user: ID of the user for which report is generated.
            @param orderBy: Tuple  with two items : sorting column and the order direction.            
            @type from_date: C{string}
            @type to_date: C{string}
            @type channel: C{string}
            @type user: C{string}
            @type orderBy: C{tuple}            
            @rtype: C{list} 
            @return: C{list} of the L{BasePopo} objects, each represents troughput of the notifications per day.
        """
        session = None
        try:
            session=Session()                
            if(channel and user):
                result = session.findCustom(BasePopo,throughput_report_full_q,["%Y-%m-%d",from_date,to_date,user,channel],["DELIVERY_DATE","DELIVERY_TYPE_ID","DELIVERY_STATUS","NOTIFCOUNT"],orderBy)
            elif(channel):
                result = session.findCustom(BasePopo,throughput_report_for_channel_q,["%Y-%m-%d",from_date,to_date,channel],["DELIVERY_DATE","DELIVERY_TYPE_ID","DELIVERY_STATUS","NOTIFCOUNT"],orderBy)
            elif(user):
                result = session.findCustom(BasePopo,throughput_report_for_user_q,["%Y-%m-%d",from_date,to_date,user],["DELIVERY_DATE","DELIVERY_TYPE_ID","DELIVERY_STATUS","NOTIFCOUNT"],orderBy)
            else:
                result = session.findCustom(BasePopo,throughput_report_default_q,["%Y-%m-%d",from_date,to_date],["DELIVERY_DATE","DELIVERY_TYPE_ID","DELIVERY_STATUS","NOTIFCOUNT"],orderBy)            
            return result
            
        finally:
            if(session):session.close()

    def getFailedNotifications(self,orderBy):
        """ 
            Generates report for notifications which aren't successfuly sent.
            @param orderBy: Tuple  with two items : sorting column and the order direction.
            @type orderBy: C{tuple}            
        """
        session = None        
        try:
            session=Session()
            return session.findCustom(BasePopo,failed_notifications_report_q,[],["CH_NAME","EXT_USER_ID","DT_NAME","ADDRESS","SUBS_ID","FAILED_NR"],orderBy)
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

    def getChannels(self):
        """ 
            Returns all channels generated in database.
            @rtype: C{list}
            @return: a C{list} of the L{Channel} objects.            
        """
        session = None
        try:                
            session=Session()
            return session.findAll(Channel)
        finally:
            if(session):session.close()

    def getUsers(self):
        """ 
            Returns all EEA user registrated in database.
            @rtype: C{list}
            @return: a C{list} of the L{EEAUser} objects.            
        """
        session = None
        try:                
            session=Session()
            return session.findAll(EEAUser)
        finally:
            if(session):session.close()


    def getChannel(self,id):
        """
            Returns a channel with primary key equal C{id}.
            @param id: ID of the channel.
            @type id:C{long}
            @return: a L{Channel} object.
            @rtype: L{Channel}
        """
        session = None
        try:
            session=Session()
            channel = session.findByPK(Channel,[int(id)])   
        finally:
            if(session):session.close()        
        return channel

    
    def getUser(self,id):
        """
            Returns a EEA User with primary key equal C{id}.
            @param id: ID of the User.
            @type id: C{long}
            @return: a L{EEAUser} object.
            @rtype: L{EEAUser}
        """
        session = None
        try:
            session=Session()
            channel = session.findByPK(EEAUser,[int(id)])   
        finally:
            if(session):session.close()        
        return channel

