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

import string, xmlrpclib, httplib
from base64 import encodestring

class BasicAuthTransport( xmlrpclib.Transport ):
    """ 
    We must use basic authentication to connect to the XML-RPC server
    Class overrides request method and adds basic authentication parameters as header        
    """
    def __init__( self, username=None, password=None ):
        self.username=username
        self.password=password
        self.verbose=None
        
    def request( self, host, handler, request_body,verbose = None):
        h = httplib.HTTP( host )
        h.putrequest( "POST", handler )
        h.putheader( "Host", host )
        h.putheader( "User-Agent", self.user_agent )
        h.putheader( "Content-Type", "text/xml" )
        h.putheader( "Content-Length", str( len( request_body ) ) )
        if self.username is not None and self.password is not None:
            h.putheader( "AUTHORIZATION", "Basic %s" % string.replace(encodestring( "%s:%s" % ( self.username, self.password ) ), 
                    "\012", "" ) )
        h.endheaders()
        if request_body: h.send( request_body )
        errcode, errmsg, headers = h.getreply()
        if errcode != 200:
            raise xmlrpclib.ProtocolError( 
                host + handler, 
                errcode, errmsg, 
                headers
                )

        return self.parse_response( h.getfile() ) 
