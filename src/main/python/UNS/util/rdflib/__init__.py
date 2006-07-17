# RDF Library

__version__ = "2.1.0"

import sys
# TODO: what version of python does rdflib require??
assert sys.version_info >= (2,2,1), "rdflib requires python 2.2.1 or higher"
del sys

from os import environ
import sys

instance_home = environ.get('INSTANCE_HOME') or environ.get('ZOPE_HOME')    
sys.path.append(instance_home + "/UNS/util")

from rdflib.URIRef import URIRef
from rdflib.BNode import BNode
from rdflib.Literal import Literal

from rdflib.Namespace import Namespace

from rdflib.Graph import Graph

from rdflib import RDF
from rdflib import RDFS

from rdflib.FileInputSource import FileInputSource
from rdflib.URLInputSource import URLInputSource
from rdflib.StringInputSource import StringInputSource


