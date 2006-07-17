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

import os, sys, md5, string
hexStr = string.hexdigits

def hexify(str):
        r = ''
        for ch in str:
            i = ord(ch)
            r = r + hexStr[(i >> 4) & 0xF] + hexStr[i & 0xF]
        return r
        
def calculate_md5(what):
    m = md5.new()
    m.update(what)
    return hexify(m.digest())
    
def getLocalName( predicate ):
        name=None
        toks = string.split( str( predicate ), '#' )
        if( len( toks )<2 ) : 
            toks = string.split( str( predicate ), '/' )
            name=toks[len( toks )-1]
        else:
            name=toks[1]
        return name
