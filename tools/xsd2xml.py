#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-
"""Python module for the Xsd2XSEM generator

Functionality:
  - Given an xsd, generates the corrosponding XML document instance

To do:
  - Namespace handling 

  the element definition is guaranteed to have a name, E.g.
  
  <xsd:element name="c">                     <-- We are here
    <xsd:complexType>
      <xsd:attribute name="name1" type="xsd:string"/>

  if the element defintion has a type (that not an xsd datatype), the
  next element that is a xsd:complexType and has a name that matches
  that type is not in the instance model itself, but defines the
  hierarchy of the xml instance. E.g.:

  <xsd:element name="a" type="aType">    <-- We are here
  ...
  <xsd:complexType name="aType>
  <xsd:element name="b">

  For a given complexType, the function build_xml_instance looks up
  the name of the complexType in a hashmap to get the corrosponding
  parent (the instance element 'a')

  <xsd:element name"a" type="aType">
  ...
    <xsd:complexType name="aType">         <-- We are here
      <xsd:element name="b">

  the node itself will not be in the instance_model,
  but it defines the hierarchy, as the pair
  xsd:elements type and complexTypes name
  constitutes a hierarchy in an instance model

"""

__author__  = 'Steen Manniche <stm@dbc.dk>'
__date__    = 'Thu Oct 23 11:12:15 2008'
__version__ = '$Revision:$'


#import os
from xml.dom import minidom
from xml.dom import Node
# from pprint import pprint
import logging as log
#elementtree has a flaky import history:
try:
    import ElementTree as ET
except:
    # using python >= 2.5 without cET
     try:
          from xml.etree import ElementTree as ET
     except:
         #using python <= 2.3
         try:
             import elementtree.ElementTree as ET
         except:
             raise ImportError, "Your system needs the ElementTree or cElementTree library in order to run this package\ntry aptitude install python-elementtree"

class XMLInstantiator( object ):
    """Class that generates an XML Document instance given an XML Schema
    """

    def __init__( self, filename, flag ):
        """

        Arguments:

        - `filename`: name of the file that init tries to parse. If
          this is not an XML Schema, a ValueError is raised

        - `flag`: indicating whether either verbosity, debug-mode or
          just plain program execution should be the purpose
        
        """

        #root is set once for a document instance and holds the whole
        #instance tree after a successfull execution of build_xml_instance
        self.root              = None

        #parent holds the given current level in the document instance
        #generation
        self.parent            = None

        #the parent map is somewhat heterogenous
        #(yay python!), and holds relations
        #between parent elements and their
        #children through the directives given in
        #complexTypes name paired with elements
        #complexType-type declarations
        self.parent_map = {}

        #primary xsd datatypes
        self.xsd_datatypes =[
            'xsd:string',
            'xsd:boolean',
            'xsd:decimal',
            'xsd:float',
            'xsd:double',
            'xsd:duration',
            'xsd:dateTime',
            'xsd:time',
            'xsd:date',
            'xsd:gYearMonth',
            'xsd:gYear',
            'xsd:gMonthDay',
            'xsd:gDay',
            'xsd:gMonth',
            'xsd:hexBinary',
            'xsd:base64Binary',
            'xsd:anyURI',
            'xsd:QName',
            'xsd:NOTATION']

        #and, if it should be needed, the derived datatypes
        self.xsd_derived_datatypes = [
            'xsd:normalizedString',
            'xsd:token',
            'xsd:language',
            'xsd:NMTOKEN',
            'xsd:NMTOKENS',
            'xsd:Name',
            'xsd:NCName',
            'xsd:ID',
            'xsd:IDREF',
            'xsd:IDREFS',
            'xsd:ENTITY',
            'xsd:ENTITIES',
            'xsd:integer',
            'xsd:nonPositiveInteger',
            'xsd:negativeInteger',
            'xsd:long',
            'xsd:int',
            'xsd:short',
            'xsd:byte',
            'xsd:nonNegativeInteger',
            'xsd:unsignedLong',
            'xsd:unsignedInt',
            'xsd:unsignedShort',
            'xsd:unsignedByte',
            'xsd:positiveInteger']

        # when setting attributes, we 'help' a bit by setting default
        # values
        self.attr_defaults = {
            'booleanType': 'true',
            'xsd:string' : '',
            'xsd:integer': '0',
            'xsd:boolean': 'true',
            'xsd:decimal': '0.0',
            'xsd:float'  : '0.0',
            'xsd:double' : '0.0'
            }

        # convenience concatenation
        self.xsd_types = self.xsd_datatypes+self.xsd_derived_datatypes

        # for debugging or verbose printouts from the log
        if flag == "debug":
            import pdb
            pdb.set_trace()
        elif flag == "verbose":
            loglevel = log.DEBUG
        else:
            loglevel = log.NOTSET

        log.basicConfig(level=loglevel,
                    format='%(asctime)s %(levelname)s %(message)s',
                        filename='xsd2xml.debug',
                        filemode='w'
                            )
        log.getLogger('')

        # automatic parsing of the input file, this will throw if
        # something goes wrong or is wrong with the xsd
        self.xsd = ET.parse( filename )

        if self.xsd.getroot().tag.split('}')[1] != "schema":
            raise ValueError, "the imported files contents does not seem to be an XML Schema document"

        #get the global namespace
        self.ns = self.xsd.getroot().attrib.get( 'targetNamespace' )

        #please note that namespace prefixes are semantically
        #irrellevant. This is just for the 'human-readability' of the
        #generated output
        ET._namespace_map[ self.ns ] = 'foxml'
        
    def build_xml_instance( self, node_tree, sieve ):
        """Given an xsd, this method builds an XML Document
        Instance and saves it to the self.parent. It also
        contructs a root element from the root xsd:element
        definition found in the schema

        Arguments:
        - `self`:
        - `node_tree`:
        - `sieve`:

        @returns: an xml instance
        """
        # iterates all elements in the xsd, sadly, only one filter
        # can be applied to the getiterator function, but we have
        # a list.
        for element in node_tree.getiterator():
            # remove the namespace
            element_tag = element.tag.split('}')[1]
            log.debug( "Got element %s"%( element_tag ) )
            if element_tag in sieve:
                #get name and type from attribs note: type (and
                #name) can be null after assignment, use for
                #checking
                el_name, el_type = element.get( 'name' ), element.get( 'type' )
                #print element_tag, el_name, el_type
                if element_tag == "element":
                    log.debug( "xsd:element name=%s type=%s"%( el_name, el_type ) )
                    #local cache of the new instance element
                    #in elements, name is never None
                    log.debug( "Creating new element, name = %s"%( el_name ) )
                    element_name = "{%s}%s"% ( self.ns, el_name )
                    elem = ET.Element( element_name )
                    #this is the special case:
                    if self.parent is None:
                        #there was no root element, creating it.
                        self.parent = elem
                        self.root = self.parent
#                         if self.ns is not None:
#                             self.root.attrib['xmlns:foxml'] = self.ns

                        log.debug( "%s is root of the instance tree" %( elem ) )
                    else:
                        self.parent.append( elem )
                        log.debug( "Appending %s to the parent list" %( elem ) )
                        #print "element %s has children: %s"%(self.parent, self.parent._children)
                    #if type is not None, then element has
                    #children in the instance model
                    if el_type is not None:
                        log.debug( "parent map contains { %s : %s }"%( el_type, elem ) )
                        self.parent_map.update( { el_type: elem } )

                elif element_tag == "attribute":
                    log.debug( "xsd:attribute name=%s type=%s"%( el_name, el_type ) )

                    if el_type in self.attr_defaults.keys():
                        #we have a default value
                        self.parent.set( el_name, 
                                         self.attr_defaults.get( el_type ) )
                    else:
                        self.parent.set( el_name, "" )

                elif element_tag == "complexType":
                    log.debug( "xsd:complexType name=%s type=%s"%( el_name, el_type ) )
                    if el_name is not None:
                        #setting a new parent
                        log.debug( "Setting new parent = %s"%( el_name ) )
                        try:
                            self.parent = self.parent_map[ el_name ]
                        except KeyError, ke:
                            error_string = "Key %s was not in the parent_map:\n%s\nKeyError: '%s'" %( el_name, self.parent_map, ke.message )
                            sys.exit( error_string )
                elif element_tag == "complexContent":
                    pass
                elif element_tag == "extension":
                    # treating:
                    # ...
                    # <xsd:complexType>
                    #     <xsd:complexContent>
                    #         <xsd:extension base="digitalObjectType"/>
                    #     </xsd:complexContent>
                    # </xsd:complexType>
                    # ...

                    el_base = element.get( "base" )
                    log.debug( "xsd:%s 'type'=%s"%( element_tag, el_base ) )

                    # this is a special (and somewhat complex case) of
                    # creating complexTypes (oh, the irony...); by
                    # using an xsd:extension, in a xsd:complexContent,
                    # one is allowed to obfuscate the purpose of
                    # referencing an element in a complexType. This is
                    # (in the instance model) equivalent to:
                    # ...
                    # <xsd:complexType>
                    #     <xsd:sequence>
                    #         <xsd:element name="compass" type="compassType"/>
                    #     </xsd:sequence>
                    # </xsd:complexType>
                    # ...
                    # Where the element name is disreguarded for the
                    # purposes of comparisons.
                    #if type is not None, then element has
                    #children in the instance model
                    if el_base is not None:
                        log.debug( "parent map contains { %s : %s }"%( el_base, elem ) )
                        self.parent_map.update( { el_base: elem } )
                    
                    

            else:
                pass

    def get_xpath( self, node ):
        """
        from http://cvs.4suite.org/viewcvs/lib/domtools.py and
        modified a bit

        Return an XPath expression that provides a unique path to the
        given node (supports elements, attributes and root nodes)
        within a document

        """
        if node.nodeType == Node.ELEMENT_NODE:
            count = 1
            #Count previous siblings with same node name
            previous = node.previousSibling
            while previous:
                if previous.localName == node.localName: count += 1
                previous = previous.previousSibling
            step = '%s[%i]' % (node.nodeName, count)
            ancestor = node.parentNode
        elif node.nodeType == Node.ATTRIBUTE_NODE:
            step = '@%s' % (node.nodeName)
            ancestor = node.ownerElement
        elif not node.parentNode:
            #Root node
            step = ''
            ancestor = node
        else:
            raise TypeError( "Unsupported node type %s"%node.__repr__ )
        if ancestor.parentNode:
            return self.get_xpath( ancestor ) + '/' + step
        else:
            return '/' + step

    def doc_order_iter( self, node ):
        """
        Iterates over each node in document order,
        returning each in turn
        """
        #Document order returns the current node,
        #then each of its children in turn
        yield node
        for child in node.childNodes:
            #Create a generator for each child,
            #Over which to iterate
            for cn in c.doc_order_iter(child):
                yield cn
        return


if __name__ == '__main__':
    import sys
    from optparse import OptionParser
    parser = OptionParser("xsd2xml -f xsdfile [-d|-v|-h]",)
    
    parser.add_option("-d", "--debug", action="store_true", 
                      dest="debug", default=False,  
                      help="Turns on the python debugger (pdb)." )

    parser.add_option("-v", "--verbose", action="store_true", 
                      dest="verbose", default=False,
                      help="Turns on verbosity. Debugging info written to a file" )

    parser.add_option("-f", "--infile", type="string", dest="infile", action="store",
                      help="The xsd to process" )

    parser.add_option("-o", "--outfile", type="string", dest="outfile", action="store",
                      help="If given, the outfile will have the xml instance written to it." )

    parser.add_option("-x", "--xpath", action="store_true", 
                      dest="xpath", default=False,  
                      help="writes all possible xpaths from the xml instance that can be constructed on basis of the xsd" )

    
    (options, args) = parser.parse_args()

    if options.infile is not None:
        infile = options.infile
    else:
        parser.error( "an input file must be specified" )

    if options.debug and options.verbose:
        parser.error( "cannot combine debug mode with verbose mode" )

    flag = options.debug or options.verbose

    if options.debug:
        flag = "debug"
    elif options.verbose:
        flag = "verbose"

    c = XMLInstantiator( infile, flag )
    c.build_xml_instance( c.xsd,
                          [ "element",
                            "attribute",
                            "complexType",
                            "complexContent",
                            "extension"
                            ]
                          )
    xml_instance = ET.tostring( c.root, encoding="UTF-8")

    if options.outfile is not None:
        o = open( options.outfile, 'w' )
        o.write( xml_instance )
        o.close()

    if options.xpath:

        # get xpaths, currently only supported with the xml.dom package:
        dom_tree = minidom.parseString( xml_instance )

        nodes = dom_tree.documentElement

        for node in c.doc_order_iter( dom_tree ):
            print c.get_xpath( node )

#         for e in nodes.childNodes:
#             print e


        

    #later, I would like to make some tests, the following should cover my case

    test_xsd = """<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' elementFormDefault='qualified' attributeFormDefault='unqualified'>
  <xsd:element name='a'>

    <xsd:complexType>
      <xsd:sequence>
        <xsd:element name='b' type='bType'/>
      </xsd:sequence>
    </xsd:complexType>

  </xsd:element>

  <xsd:complexType name='bType'>

    <xsd:sequence>
      <xsd:element name='c' type='cType'/>
      <xsd:element name='a' type='xsd:string'/>
    </xsd:sequence>

  </xsd:complexType>
  <xsd:complexType name='cType'>
    <xsd:sequence>
      <xsd:element name='e' minOccurs='0'>
        <xsd:complexType>
          <xsd:attribute name='name' type='xsd:string' use='optional' default='alias'/>
          <xsd:attribute name='setting' type='xsd:string' use='optional'/>
        </xsd:complexType>
      </xsd:element>
    </xsd:sequence>
  </xsd:complexType>
</xsd:schema>
    """

    verification_xml = """<?xml version='1.0' encoding='UTF-8'?><a><b><c><e name='alias' setting=''/></c><a>test streng</a></b></a>"""
