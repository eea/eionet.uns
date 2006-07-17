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
#                  Ivan Brkovic(ED)

from UNS.util.ORMPY.Session import Session
from UNS.ERA.NotificationTemplate import NotificationTemplate
from UNS.util.ORMPY.exceptions import ORMPyOperationalError
from UNS.exceptions import ValidationException

class TemplateFacade:

    """
         The Facade Class for notification's templates operations.
    """
    
    def getTemplates(self,orderBy = None):
        """
            Returns all templates generated in database.
            @param orderBy: Tuple  with two items : sorting column and the order direction.
            @type orderBy: C{tuple}
            @rtype: C{list}            
            @return: a C{list} of the L{NotificationTemplate} objects.            
        """
        session = None
        try:
            session=Session()
            return session.findAll(NotificationTemplate,None,orderBy)
        finally:
            if(session):session.close()


    def getTemplate(self,id):
        """
            Returns a template with primary key equal C{id}.
            @param id: ID of the template.
            @type id: C{long}
            @return: a L{NotificationTemplate} object.
            @rtype: L{NotificationTemplate}
        """
        session = None
        try:
            session=Session()
            return session.findByPK(NotificationTemplate,[int(id)])
        finally:
            if(session):session.close()

	 
    def deleteTemplate(self,template):
        """ 
            Deletes template from database.
            @param template: The channel that will be deleted.
            @type template: L{NotificationTemplate}
            @raise ValidationException: if referential integrity is broken or is in EDIT_ONLY mode.
        """
        session = None
        try:
            try:                
                session=Session()
                if template["EDIT_ONLY"]:
                    raise ValidationException , 'Can not delete template "%s" because it is in EDIT_ONLY mode' % template["NAME"]                
                session.remove(template)
                session.commitTransaction();
                return template
            except ORMPyOperationalError,error:
                if(error.code == 1217):
                    raise ValidationException , 'Can not delete template "%s" because it is assigned to a channel(s)' % template["NAME"] 
                else:raise error                
        finally:
            if(session):session.close()
        
        
    def updateTemplate(self,template):
        """ 
            Updates template from database.
            @param template: The channel that will be updated.
            @type template: L{NotificationTemplate}
        """
        session = None
        try:
            session=Session()
            session.update(template)
            session.commitTransaction()
        finally:
            if(session):session.close()


    def addTemplate(self, template):
        """ 
            Creates a new template in the database.
            By default template is not in EDIT_ONLY mode.
            @param template: The channel that will be created.
            @type template: L{NotificationTemplate}
        """
        session = None
        try:
            session=Session()
            template["EDIT_ONLY"] = 0
            tmpl=session.create(template)
            session.commitTransaction()
        finally:
            if(session):session.close()