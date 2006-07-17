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
#   Original code: Sasha Milosavljevic (ED)

""" Various constants used in UNS product """

import os

#paths
UNS_PRODUCT_HOME = os.path.dirname(__file__)
ZPT_HOME = os.path.join(UNS_PRODUCT_HOME,'www/zpt')
CSS_HOME = os.path.join(UNS_PRODUCT_HOME,'www/css')
JS_HOME = os.path.join(UNS_PRODUCT_HOME,'www/js')
HELP_HOME = os.path.join(UNS_PRODUCT_HOME,'www/help')
IMAGES_HOME = os.path.join(UNS_PRODUCT_HOME,'www/images')

#mysql config file
my_sql_cnf = os.path.join(UNS_PRODUCT_HOME,'etc/my.cnf')

#Permisions
uns_admin_permission = "UNS Administration"
uns_user_permission = "UNS Subscribing"
uns_rpc_permission = "UNS Rpc"

#Roles
uns_admin_role = "UNS_Administrator"
uns_rpc_role = "UNS_rpc"
zope_manager_role = "Manager"

#modes
PULL = 0
PUSH = 1

## Deamon states
DEAMON_DOWN = 0
DEAMON_UP = 1
DEAMON_STARTING = 2
DEAMON_STOPING = 3


REMOVED_ROLE_TEMPLATE = 2
REMOVED_CHANNEL_TEMPLATE = 3
