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

import os, string, cStringIO, time
import libxml2, libxslt, urllib
import socket
socket.setdefaulttimeout(15.0)
from rdflib import *
from rdflib.constants import *
from RDFThing import RDFThing

RSS_CHANNEL=Namespace( "http://purl.org/rss/1.0/channel" )
RSS_IMAGE=Namespace( "http://purl.org/rss/1.0/image" )

if ( os.getenv( "INSTANCE_HOME" ) ):
    stylesheet = os.environ['INSTANCE_HOME'] + "/UNS/util/rss2rdf.xsl"
elif ( os.getenv( "ZOPE_HOME" ) ):
    stylesheet = os.environ['ZOPE_HOME'] + "/UNS/util/rss2rdf.xsl"
styledoc = libxml2.parseFile( stylesheet )
style = libxslt.parseStylesheetDoc( styledoc )

class UNSURLopener(urllib.FancyURLopener):

    http_error_default = urllib.URLopener.http_error_default

    def __init__(*args):
        self = args[0]
        apply(urllib.FancyURLopener.__init__, args)
        self.addheaders = [('User-agent', 'UNS'),]

    def http_error_401(self, url, fp, errcode, errmsg, headers):
        return self.http_error_default(url, fp, errcode, errmsg, headers)
        
class XmlTransformer:
        def transform( self, docContent ):
                doc = libxml2.parseDoc(docContent)
                s1=time.clock()
                result = style.applyStylesheet( doc, None )
                mmm=style.saveResultToString( result )
                doc.freeDoc()
                result.freeDoc()
                return mmm

class RDFCollector:
    """ Collect """
    
    def __init__( self, URI=None ):
        self.__harvest( URI )
            
    def __harvest( self, URI ):
        if URI is not None:
            opener = UNSURLopener()
            try:
                file=opener.open(URI)
                content=file.read()
                if not content.find( "rdf:RDF" )>0:
                    content=XmlTransformer().transform( content )
                self.__harvestC(content)
            except Exception, e: 
                raise IOError('Unable to parse feed %s' % URI)
                    
                    
    def __harvestC( self, content ):
        """ Parsing content provided as String """
        if content is not None:
            self.store = Graph()
            self.store.parse( StringInputSource( content ) )
        
    
    def getLocalName( self, predicate ):
        name=None
        toks = string.split( str( predicate ), '#' )
        if( len( toks )<2 ) : 
            toks = string.split( str( predicate ), '/' )
            name=toks[len( toks )-1]
        else:
            name=toks[1]
        return name
    
    def getTypes( self ):
        trt=[]
        for s, o in self.store.subject_objects( TYPE ):
            if o not in trt:
                trt.append( o )
        return trt
    
    def getMetadataElements( self, URI=None ):
        """ Gets all metadata elements that may appear at first RDF level """
        if URI is not None:
            self.__harvest( URI )
        metadata=[]
        cannel=None
        image=None
        for s in self.store.subjects( TYPE, RSS_CHANNEL ): cannel=s
        for s in self.store.subjects( TYPE, RSS_IMAGE ): image=s
        for s, p, o in self.store:
              if isinstance( o, Literal ) and s!=cannel and s!=image: 
                ufn=self.getLocalName( p )
                if ( p, ufn ) not in metadata:
                    metadata.append( ( p, ufn ) )
        return metadata
            
    
    def getEvents( self, URI=None ):
        """ Gather events data """
        if URI is not None:
            self.__harvest( URI )
        return self.__collectThings();

    def getEventsC( self, content ):
        """ Gather events data from RDF """
        if content is not None:
            self.__harvestC( content )
        return self.__collectThings();

    def __collectThings( self ):
        things=[]
        cannel=None
        image=None
        for s in self.store.subjects( TYPE, RSS_CHANNEL ): cannel=s
        for s in self.store.subjects( TYPE, RSS_IMAGE ): image=s
        for s, o in self.store.subject_objects( TYPE ):
            if s!=cannel and s!=image and o!=RDFS_CLASS and not isinstance( s, BNode ): things.append( RDFThing( s, o ) )
        for thing in things:
            s=thing.getSubject()
            for p, o in self.store.predicate_objects( s ):
                if isinstance( o, Literal ) and not isinstance( s, BNode ):
                    try:
                        thing[p]=o
                    except KeyError, e:
                       pass
        return things
    
    def collectRdfPayload( self, rdf_payload ):
        """ Parses straightforward triples in a list-of-lists structure and returns list of RDFThing objects """
        things=[]
        for triple in rdf_payload:  
            if triple[1]==TYPE: things.append( RDFThing( triple[0], triple[2] ) )
        for thing in things:
            s=thing.getSubject()
            for triple in rdf_payload:
                curr_s, p, o=triple[0], triple[1], triple[2]
                if s==curr_s and p != TYPE:
                    if o is None:  object = BNode()
                    elif o.startswith(RDFNS) or o==TYPE : object = URIRef(o)
                    else:  object = Literal(o)
                    if isinstance( object, Literal ):
                        try: thing[p]=object
                        except KeyError, e: pass
        return things


class RDFGenerator:
    """ """

    def generate_rss1(self, things):
        """ Generates RSS 1.0 document from the things list"""
        
        heads = cStringIO.StringIO()
        heads.write("<channel rdf:about=\"http://uns.eionet.europa.eu/events\"><title>Unified Notification Service</title><items><rdf:Seq>\n")
        for thing in things: heads.write("<rdf:li rdf:resource=\""+thing.getSubject()+"\"/>\n")
        heads.write("</rdf:Seq></items></channel")
        head=heads.getvalue().replace("&","&amp;")
        
        if not things or len(things)==0: return ''.join(["<rdf:RDF xmlns='http://purl.org/rss/1.0/' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>",head,"></rdf:RDF>"])
        
        store=Graph()
        store.bind("dc", "http://purl.org/dc/elements/1.1/")
        store.bind(None, "http://purl.org/rss/1.0/")
        store.bind("content", "http://purl.org/rss/1.0/modules/content/")
        store.bind("slash", "http://purl.org/rss/1.0/modules/slash/")
        RSS1 = Namespace("http://purl.org/rss/1.0/")
        
        self.__generateRdfBody(store,things,RSS1['item'])
                
        rrr=store.serialize(format="pretty-xml").split(">",2)
        return '>'.join([rrr[0],rrr[1],head,rrr[2]])
        
    def generate_rdf_items(self, things):
        """ Generates RDF containing only RSS item elements"""
        
        if not things or len(things)==0: return ''.join(["<rdf:RDF xmlns='http://purl.org/rss/1.0/' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>",head,"></rdf:RDF>"])
        
        store=Graph()
        store.bind("dc", "http://purl.org/dc/elements/1.1/")
        store.bind(None, "http://purl.org/rss/1.0/")
        store.bind("content", "http://purl.org/rss/1.0/modules/content/")
        store.bind("slash", "http://purl.org/rss/1.0/modules/slash/")
        RSS1 = Namespace("http://purl.org/rss/1.0/")
        
        self.__generateRdfBody(store,things,RSS1['item'])
                
        return store.serialize(format="pretty-xml")
            
    def generate_rdf(self, things):
        """ Generates RDF from array of the RDFThing objects"""
        store=Graph()
        self.__generateRdfBody(store,things)
        message=store.serialize(format="pretty-xml")
        return message
        
    def __generateRdfBody(self, store, things,sub=None):
        for thing in things:
            s=URIRef(thing.getSubject())
            if not sub: store.add((s, TYPE , Literal(thing.getType())))
            else: store.add((s, TYPE , sub))
            elements=thing.getMetadata();
            for element in elements:
                property=URIRef(element)
                values=elements[element]
                for value in values:  store.add((s, property , Literal(value)))

    



    
