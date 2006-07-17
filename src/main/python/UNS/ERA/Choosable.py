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

from UNS.util.ORMPY.BasePopo import BasePopo
from UNS.util.ORMPY.Association import Association

class Choosable(BasePopo) :

    """ The name of database table. """
    table='CHOOSABLE'
    
    """ Mapped classes must declare the primary key columns of the database table. 
    The pk element defines the mapping from that properties to the primary key columns.
    """
    pk=[("CHANNEL_ID",),("PROPERTY",)]
    properties=[] 
    associations=[]
    
    def __init__(self):
        BasePopo.__init__(self)