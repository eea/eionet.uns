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

class RDFThing:
    """ Object representation of the RDF Resource """
    
    def __init__(self, subject=None, type=None):
        self.__subject=subject
        self.__type=type
        self.__metadata  = {}
        
    def __getitem__(self, key):
        try:
            values = self.__metadata[key]
        except KeyError:
            values=None
        return values
        
    def __setitem__(self, key, value):
        try:
            self.__metadata[key].append(value)
        except KeyError:
            self.__metadata[key] = [value]
           
        
    def __len__(self):
        return len(self.__metadata)
        
    def getSubject(self):
        return self.__subject
        
    def getType(self):
        return self.__type
        
    def getMetadata(self):
        return self.__metadata
        

    