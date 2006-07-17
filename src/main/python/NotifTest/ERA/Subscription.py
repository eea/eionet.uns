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


from NotifTest.ORMPY.BasePopo import BasePopo
from NotifTest.ORMPY.Association import Association

class Subscription(BasePopo) :

    """ The name of database table. """
    table='SUBSCRIPTION'
    
    """ Mapped classes must declare the primary key columns of the database table. 
    The pk element defines the mapping from that properties to the primary key columns.
    """
    
    # user_id many_to_one
    # channel many_to_one
    
    pk=[('ID','AUTOINCREMENT')]
    properties=['LEAD_TIME','CREATION_DATE','SECONDARY_ID']
    associations=[  Association(Association.many_to_one, 'User',['xxx','EEA_USER_ID'],"EEA_USER"),
                    Association(Association.many_to_one, 'Channel',['xxx','CHANNEL_ID'],"CHANNEL")
                 ]
    
    def __init__(self):
        BasePopo.__init__(self)
  