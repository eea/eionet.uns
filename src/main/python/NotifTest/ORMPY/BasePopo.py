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




class BasePopo :
    """ 
    Object/relational mappings are defined in an Python classes. 
    The mapping classes who must extend BasePopo are designed to be readable and hand-editable. 
    """
    
    __allow_access_to_unprotected_subobjects__ = 1

    def __init__(self):
        self._list  = {}
    
    def __getitem__(self, key):
        """ Returns the item given by index from the Dictionary.
             Since it is faster to to fail on the lookup into the dictionary and catch exception than it is to check for existence of the specified
             key this method is not using has_kay method.
             Speed up will be result of such handling especially if key exist.
        """
        try:
            value = self._list[key]
        except KeyError:
            value=None
        return value
        
    def __setitem__(self, key, value):
            """ Sets the value for the field """
            self._list[key] = value
        
    def __delitem__(self, key):
            del self._list[key] 
        
    def __len__(self):
        """ Returns the length of the Dictionary"""
        return len(self._list)
        
    def getAttributes(self):
        return self.__getIDAttrs()+self.__getAssociationAttrs()+self.properties
        
    def __getAssociationAttrs(self):
        result=[]
        if(self.associations is not None):
            for association in self.associations:
                    l=association.getAttributes()
                    if(l is not None): result=result+l
        return result
        
    def __getIDAttrs(self):
        result=[]
        try:
            for key in self.pk:
                result.append(key[0])
        except AttributeError: pass
        return result
        