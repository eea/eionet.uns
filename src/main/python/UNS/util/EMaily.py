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

import smtplib, time
import poplib
import string
from socket import *
from email.MIMEText import MIMEText
from email.Header import Header
from email.Utils import formatdate
from email.MIMEMultipart import MIMEMultipart

class EMaily:
    """ """
    def __init__(self):
        self.smtpSession = None
        
    def connect(self, host, port=None , username=None, password=None):
        try:
            self.smtpSession = smtplib.SMTP(host)
            if username is not None:
                self.smtpSession.login(username, password)
        except Exception, e:
            raise IOError("Error while establishing SMTP session !")
        
    def disconnect(self):
        """ UNS Aware disconnect from the SMTP server """
        self.smtpSession.quit()
        self.smtpSession=None
        
       
    def sendMessage(self, address, notification, admin=None):
        if address is None:
            raise Exception({ "None" : ( 550 ,"The 'None' recipient is NOT sent to the SMTP server" ) })
        try:
            RECIPIENTS = [address]
            SENDER =admin
            msg = self._createMessage(address, notification,admin)
            result=self.smtpSession.sendmail(SENDER, RECIPIENTS, str(msg))
            if result:
                self.smtpSession.rset()
                raise Exception(result)
        finally:
            pass
            
                
    def _createMessage(self, address, notification,admin):
        
        text = notification['CONTENT']
        html = notification['HTML_CONTENT']
        msg = html and MIMEMultipart() or MIMEText(text,'plain','utf-8')
        msg['From'] = '"Notification Service"' + ( admin and ('<'+admin+'>') or '')
        msg['To'] = address
        msg['Subject'] = Header(notification['SUBJECT'], 'utf-8')
        msg['Date'] = formatdate()
        msg['X-UnsId'] = str(notification['ID'])

        if html:
            msg.set_type('multipart/alternative')
            msg.preamble = ''
            msg.epilogue = ''
            msg.attach(MIMEText(text,'plain','utf-8'))
            msg.attach(MIMEText(html,'html','utf-8'))
        
        return msg         
                
class POPy:
    """ """
    def __init__(self):
        self.pop3Session = None
        self.poptimeout = 15
        
    def connect(self, host, port , username, password ):
        try:
            sck = socket(AF_INET, SOCK_STREAM)
            sck.settimeout(self.poptimeout)
            sck.connect((host,port))
            sck.close()            
            self.pop3Session = poplib.POP3(host,port)
            self.pop3Session.user(username)
            self.pop3Session.pass_(password)                    
        except timeout:
                raise Exception("POP3 server timeout exceeded ...")
        except Exception, e:
                raise IOError("Error while establishing POP3 session !")
        
    def disconnect(self):
        """ UNS Aware disconnect from the POP3 server """
        self.pop3Session.quit()
        self.pop3Session=None
