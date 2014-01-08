# -*- coding: utf-8 -*-
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
import sys,os, ldap, MySQLdb

INSTANCE_HOME=os.environ.get('INSTANCE_HOME')
os.environ['UNS_HOME'] = '@UNS_HOME@'

ldapserver = 'ldap://ldap.eionet.europa.eu'
branch = "ou=Users,o=EIONET,l=Europe"

if __name__=="__main__":
    sys.path.append(INSTANCE_HOME)
    tobeupdated = []

    from UNS.Config import *

    lr = ldap.initialize(ldapserver)
#   lr.simple_bind_s(dn,pw)

    db = MySQLdb.connect(dbserver['host'],
        dbserver['username'], dbserver['password'], dbserver['database'],
        port=dbserver['port'], connect_timeout = dbserver['connect_timeout'],
        use_unicode = True)
    cursor = db.cursor()
    cursor.execute ("select ID,EXT_USER_ID,ADDRESS from DELIVERY_ADDRESS,EEA_USER where DELIVERY_TYPE_ID=1 and ID=EEA_USER_ID")

    # Loop through the table
    while 1:
        row = cursor.fetchone()
        if row == None:
            break
        res = lr.search_s(branch, ldap.SCOPE_SUBTREE, "uid=%s" % row[1], attrlist=('mail',))
        if len(res) > 0:
            mails = res[0][1]['mail']
            if row[2] not in mails:
                tobeupdated.append((int(row[0]),mails[0]))
        else:
            print "NOT FOUND: %s" % row[1]
            if row[2] != '':
                tobeupdated.append((int(row[0]),''))

    for id,mail in tobeupdated:
        affected = cursor.execute("update DELIVERY_ADDRESS set ADDRESS=%s where EEA_USER_ID=%s and DELIVERY_TYPE_ID=1" , (mail, id))
        cursor.execute("select EEA_USER_ID, ADDRESS, DELIVERY_TYPE_ID from DELIVERY_ADDRESS where EEA_USER_ID=%s and DELIVERY_TYPE_ID=1" ,(id,))
        row = cursor.fetchone()
    cursor.close()
    db.commit()
    db.close()

# vim: set expandtab sw=4 :
