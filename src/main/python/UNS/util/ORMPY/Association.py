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

class Association:
    """ """
    many_to_one=1
    one_to_many=2
    many_to_many=3
    one_to_one=4
    
    def __init__(self, nature, name=None, details=None, table=None):
        self.nature=nature
        self.name=name
        self.details=details
        self.table=table
            
    def getAttributes(self):
        result=None
        if(self.nature==self.one_to_one or self.nature==self.many_to_one):
            result=list()
            for i in range(1,len(self.details)): 
                result.append(self.details[i])  
        return result    
        
            
            
            
            
        
    
    