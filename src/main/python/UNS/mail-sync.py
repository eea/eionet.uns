
import sys,os, ldap, MySQLdb

INSTANCE_HOME="/var/local/uns2/uns-python-source"
os.environ['UNS_HOME'] = '/var/local/uns2'

branch = "ou=Users,o=EIONET,l=Europe"

if __name__=="__main__":
    sys.path.append(INSTANCE_HOME)
    tobeupdated = []

    from UNS.Config import *

    lr = ldap.initialize('ldap://ldap.eionet.europa.eu')
#   lr.simple_bind_s(dn,pw)

    db = MySQLdb.connect(dbserver['host'],dbserver['username'], dbserver['password'], dbserver['database'],
       port=dbserver['port'], connect_timeout = dbserver['connect_timeout'], use_unicode = True)
    cursor = db.cursor()
    cursor.execute ("select ID,EXT_USER_ID,ADDRESS from DELIVERY_ADDRESS,EEA_USER where DELIVERY_TYPE_ID=1 AND ID=EEA_USER_ID")

    while 1:
        row = cursor.fetchone()
        if row == None:
            break
        print row[0], row[1], row[2]
        res = lr.search_s(branch, ldap.SCOPE_SUBTREE, "uid=%s" % row[1], attrlist=('mail',))
        if len(res) > 0:
            mails = res[0][1]['mail']
            if row[2] not in mails:
                tobeupdated.append((int(row[0]),mails[0]))
                print "------------------>", mails[0]
        else:
            print "NOT FOUND"

    for id,mail in tobeupdated:
        affected = cursor.execute("update DELIVERY_ADDRESS set ADDRESS=%s where EEA_USER_ID=%s and DELIVERY_TYPE_ID=1" , (mail, id))
        print affected, mail, id
        cursor.execute("select EEA_USER_ID, ADDRESS, DELIVERY_TYPE_ID from DELIVERY_ADDRESS where EEA_USER_ID=%s and DELIVERY_TYPE_ID=1" ,(id,))
        row = cursor.fetchone()
        print row
    cursor.close()
    db.commit()
    db.close()


