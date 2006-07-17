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

import ldap
import thread

from UNS.util.ORMPY.Session import Session
from UNS.ERA.EEARole import EEARole
from UNS.ERA.EEAUser import EEAUser
from UNS.ERA.DeliveryAddress import DeliveryAddress
from UNS.util.MemCache import MemCache

from UNS.Logging import getLogger

class LDAPFacade:

    """
         The Facade Class for LDAP related operations.
    """    
    
    LDAP_EXPIRE_CACHE_TIME = 7200
    
    lock = thread.allocate_lock()
    ldap_error_message = "Problems with ldap server. Please check the log file for details"
    logger=getLogger("UNS.LDAPFacade")
    roles_cache = MemCache()
    
    def __init__(self, host, port,users_base):
        self.host = host
        self.port = int(port)
        self.users_base  = users_base
        self.groups_base = "ou=Roles,o=EIONET,l=Europe"


    def __connect(self):
        return ldap.open(self.host,self.port)


    def getUserAttributes(self, user_dn , attributes = None):
        """ 
            Retruns LDAP user's attributes values. 
            If "attributes" input parameter is None all attributes are returned.
            @param user_dn: DN of the user
            @type user_dn: C{str}
            @param user_dn: List of the attributes that will be returned.
            @type user_dn: C{list}
            @rtype: C{list} 
            @return: a list of the user's LDAP attributes.
        """
        searchScope = ldap.SCOPE_BASE        
        retrieveAttributes = attributes
        searchFilter = '(objectClass=*)'
        result_data = []        
        
        try:
            ldp = self.__connect()
            ldap_result_id = ldp.search(user_dn, searchScope, searchFilter, retrieveAttributes)
            result_type, result_data = ldp.result(ldap_result_id, 0)
            if (result_type != ldap.RES_SEARCH_ENTRY):
                 raise RuntimeError        
            return result_data[0][1]            
        except Exception, e:
            self.logger.exception(e)
            raise Exception(self.ldap_error_message)
    


    def _getLdapRoles(self,refresh_time=None):
        """ """        
        roles = self.roles_cache.get("ldap_roles")        
        if roles: 
            roles = roles.getContent()        
        else:          
            searchScope = ldap.SCOPE_SUBTREE    
            retrieveAttributes = ["cn","uniqueMember"]
            searchFilter = '(objectClass=*)'
            roles = {}
            baseDNlen = len(self.groups_base)+1             
            userDNlen = len(self.users_base)+1
            uidLen = len("uid") + 1
            try:
                l = self.__connect()
                ldap_result_id = l.search(self.groups_base, searchScope, searchFilter, retrieveAttributes)
                index = 0
                while 1:
                    result_type, result_data = l.result(ldap_result_id, 0)
                    if (result_data == []):
                        break
                    elif(index != 0):
                        if result_type == ldap.RES_SEARCH_ENTRY:
                            cn  = result_data[0][1]['cn'][0]
                            uids = [(uid[:-userDNlen])[uidLen:] for uid in result_data[0][1]['uniqueMember']]
                            roles[result_data[0][0][:-baseDNlen]] = (cn,uids) 
                    index+=1
                            
                self.roles_cache.put("ldap_roles",roles,self.LDAP_EXPIRE_CACHE_TIME)
            except Exception, e:
                self.logger.exception(e)
                raise Exception(self.ldap_error_message)        
        return roles
    

    def getUserGroups(self):
        """
            Returns all groups(roles) on ldap server.
            @rtype: C{list} 
            @return: a list of the roles.
        """        
        roles = self._getLdapRoles()
        uns_roles = self._synchronizeRoles(roles)
        uns_roles.sort(lambda r1,r2: cmp(r1[2],r2[2]))            
        return uns_roles

        
    def _synchronizeRoles(self,roles):
        """
            Synchronize roles in database against ldap server 
        """
        self.lock.acquire()

        baseDNlen = len(self.groups_base)+1 
        uns_roles = []
        db_roles_dict = {}
        ID = 0
        session = Session()
        db_roles = session.findAll(EEARole)

        for db_role in db_roles:
            db_roles_dict[db_role['EXT_ID']] = db_role['ID']

        for role in roles.keys(): # 1 because first entry is ou=Roles,o=EIONET,l=Europe dn itself            
            ID = db_roles_dict.get(role)             
            if (not ID):                
                db_role = EEARole()
                db_role['EXT_ID'] = role
                session.create(db_role)
                ID = db_role["ID"]
            cn = roles[role][0]
            uns_roles.append((ID,role,cn))
    
        session.commitTransaction()            
        session.close()
    
        self.lock.release()        

        return uns_roles

        
    def getUserLdapRoles(self , uid):
        """
            Returns all roles which belongs to the user.
            @param uid: User's uid attribute.
            @type uid: C{str}
            @rtype: C{list} 
            @return: a list of the roles.
        """
        user_roles = []
        roles = self._getLdapRoles()
        for role,role_tuple in roles.items():
            if role_tuple[1].count(uid):
                user_roles.append(role)
        return user_roles
         
    
    def createNewUserDB(self,uid,cn,mail):
        """
            Checks if EEA user exist in UNS database , and if it is false create one.
            @param uid: User's uid attribute.
            @type uid: C{str}
            @param cn: User's cn attribute.
            @type cn: C{str}
            @param mail: User's mail attribute.
            @type mail: C{str}
            
        """
        session = None
        try:
            session=Session()        
            user= session.findCustom(EEAUser,"from EEA_USER where EXT_USER_ID = %s",[uid])                            
            if (not user):
                user = EEAUser()
                user["EXT_USER_ID"] = uid
                user["CN"] = cn or uid
                user["VACATION_FLAG"] = 0
                user["PREFER_HTML"] = 0
                session.create(user)
                if mail:
                    da = DeliveryAddress()
                    da["EEA_USER_ID"] = user["ID"]
                    da["DELIVERY_TYPE_ID"] = 1
                    da["ADDRESS"] = mail
                    session.create(da)
                session.commitTransaction()            
        finally:
            if(session):session.close()


    def getUser(self,uid):
        """
            Returns user object with LDAP uid attribute equal to C{uid}.
            @param uid: User's uid attribute.
            @type uid: C{str}
            @rtype: L{EEAUser} 
            @return: a L{EEAUser} object.             
        """
        session = None
        try:
            session = Session()
            return session.findCustom(EEAUser,"from EEA_USER where EXT_USER_ID =%s",[uid])[0]
        finally:
            if(session):session.close()
        