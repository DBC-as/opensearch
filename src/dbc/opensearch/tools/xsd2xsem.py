#!/usr/bin/env python
# -*- coding: utf-8 -*- 

import os, sys
from xml.dom import minidom
from xml.dom import Node
from pprint import pprint
import logging
import pdb
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

class XSEMGenerator( object ):
    """Class for generating XSEM mapping files from an xsd
    """

    def __init__( self, file_name, debug ):
        """
        Arguments:
        """
        self.counter           = 0
        self.doc               = None
        self.root              = None
        self.parent            = None
        self.remember_type     = {}
        self.in_instance_model = []
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

        self.xsd_types = self.xsd_datatypes+self.xsd_derived_datatypes


        logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(levelname)s %(message)s',
#                     filename='xsd2xsem.debug',
#                     filemode='w'
                            )
        logging.getLogger('')

        if debug: pdb.set_trace()
        self.node = None

        self.xsd = minidom.parse( file_name )

        self.xsd2xml_instance( self.xsd,
                               [ "xsd:element",
                                 "xsd:attribute"],
                               [ "xsd:complexType",
                                 "xsd:sequence",
                                 "xsd:choice",
                                 "xsd:simpleType"] )
        
        print self.doc.toxml( encoding="UTF-8" )
        logging.debug( "Number of iterations:                %s"% self.counter )


    def xsd2xml_instance( self, child, node_list, sieve ):
        """
        node_list is one or more of:
        xsd:element, xsd:attribute, xsd:complexType, xsd:sequence, xsd:choice or xsd:simpleType
        
        Arguments:
        """

        self.counter += 1

        #this should only catch complexTypes and simpleTypes.
        if child.nodeName in sieve and child.hasAttribute( 'name' ):
            logging.debug( "%s has name %s and remember_type is %s"%( child.nodeName, child.getAttribute( 'name' ), self.remember_type['node'].tagName ) )
            if self.remember_type is not None:

                logging.debug( "%s : %s"%(child.getAttribute( 'name' ), self.remember_type['type'] ) )
                if child.getAttribute( 'name' ) == self.remember_type['type']:
                    # E.g.
                    #
                    # <xsd:element name"a" type="aType">
                    # ...
                    # <xsd:complexType name="aType">         <-- We are here
                    #   <xsd:element name="b">
                    #this node should not be in the instance_model,
                    #but it defines the hierarchy, as the pair
                    #xsd:elements type in a complexTypes name
                    #constitutes a hierarchy
                    self.parent = self.remember_type[ 'node' ]
                    logging.debug( "self.parent now set to %s"% self.remember_type['node'].tagName )
                    #self.xsd2xml_instance( child, node_list, sieve )
        if child.nodeName in node_list and child.hasAttribute( 'name' ):
            #the element definition only has a name, which in our case
            #means that we are looking at an empty element instance
            #with attributes, E.g.
            #
            # <xsd:element name="c">                     <-- We are here
            #   <xsd:complexType>
            #     <xsd:attribute name="name1" type="xsd:string"/>
            if self.root == None:
                logging.debug( "Initializing document instance..." )
                logging.debug( "root will be %s"% child.getAttribute( 'name' ) )
                self.doc         = minidom.getDOMImplementation().createDocument( None, None, None )
                root_element     = self.doc.createElement( child.getAttribute( 'name' ) )
                self.root        = self.doc.appendChild( root_element )
                self.parent      = self.root
                self.in_instance_model.append( child.getAttribute( 'name' ) )

            elif child.nodeName == "xsd:element" and child.getAttribute( 'name' ) not in self.in_instance_model:
                self.elem = self.doc.createElement( child.getAttribute( 'name' ) )
                self.parent.appendChild( self.elem )
                logging.debug( "appended new node %s to parent %s (root is %s)"% (child.getAttribute( 'name' ), self.parent.tagName, self.root.tagName ) )
                logging.debug( "xpath for element %s = %s"%( self.elem.tagName, self.get_xpath( self.elem ) ) )
                logging.debug( "element %s in instance_model: %s"%(self.elem.tagName, self.elem.tagName in self.in_instance_model ) )
                self.in_instance_model.append( child.getAttribute( 'name' ) )
                

            elif child.nodeName == "xsd:attribute":
                #I'm pretty confident that we always want to append
                #attributes to the previously found element
                val = child.getAttribute( 'default' ) if child.hasAttribute( 'default' ) else ""
                self.elem.setAttribute( child.getAttribute( 'name' ), val )
                logging.debug( "set attribute %s='%s' on element %s"%( child.getAttribute( 'name' ), val, self.elem.tagName ) )
                self.in_instance_model.append( [ child.getAttribute, val ] )


            if child.hasAttribute( 'type' ):
                # if the node has a type (that not an xsd datatype),
                # the next node that is in the sieve and has a name
                # that matches that type is not in the instance model,
                # but defines the hierarchy of the xml instance. E.g.:
                #
                # <xsd:element name="a" type="aType">    <-- We are here
                # ...
                # <xsd:complexType name="aType>
                #   <xsd:element name="b">
                if child.getAttribute( 'type' ) not in self.xsd_types:
                    self.remember_type.update( { 'node' : self.elem } )
                    self.remember_type.update( { 'type' : child.getAttribute( 'type' ) } )
                    logging.debug( "node %s with type %s has siblings in the instance doc"% ( child.getAttribute( 'name' ), child.getAttribute( 'type' ) ) )
                    # if we got our hands on a new type, we start
                    # from the root again, but we're looking for a
                    # distinct node this time. We need to find a
                    # complexType or simpleType, where the
                    # .getAttribute( 'name' ) == self.remember_type['node'].getAttribute( 'type' ).
                    self.find_node_from_name( self.xsd, child.getAttribute( 'type' ) )
                    logging.debug( "found node %s from which to search for siblings of %s"%( self.node.getAttribute( 'name' ), self.elem.tagName ) )
                    self.xsd2xml_instance( self.node, node_list, sieve )

        #filter out unneccesary node types:
        for children in [ c for c in child.childNodes if c.nodeType == 1]:
            self.xsd2xml_instance( children, node_list, sieve )

    def find_node_from_name( self, node, name ):
        """
        returns the first occurence of a node with the specified name
        in the given (sub-) tree

        """
        for node in [ c for c in node.childNodes if c.nodeType == 1]:
            if node.nodeType == Node.ELEMENT_NODE and node.hasAttribute( 'name' ):
                if node.getAttribute( 'name' ) == name:
                    self.node = node
            self.find_node_from_name( node, name )
              
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
    
    def get_leaf_elements( self, node ):
        """
        Return a list of elements that have no element children
        node - the starting point (subtree rooted at node will be searched)
        """
        return self.doc_order_iter_filter(node, lambda n: n.nodeType == Node.ELEMENT_NODE and not [ 1 for cn in n.childNodes if cn.nodeType == Node.ELEMENT_NODE ])

    def doc_order_iter_filter( self, node, filter_func ):
        """
        Iterates over each node in document order,
        applying the filter function to each in turn,
        starting with the given node, and yielding each node in
        cases where the filter function computes true
        node - the starting point
               (subtree rooted at node will be iterated over document order)
        filter_func - a callable object taking a node and returning
                      true or false
        """
        if filter_func(node):
            yield node
        for child in node.childNodes:
            for cn in self.doc_order_iter_filter(child, filter_func):
                yield cn
        return


if __name__ == '__main__':
    import sys
    if len(sys.argv) == 3:
        if sys.argv[2] == '-d':
            c = XSEMGenerator( sys.argv[1], True )
    else:
        c = XSEMGenerator( sys.argv[1], False )
#     c.read_xsd( c.xml_tree.documentElement )

       
#     tree = ET.ElementTree(c.current_element)
#     tree.write( "testtest.xml" )


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

    verification_xml = """<?xml version='1.0' encoding='UTF-8'?>
<a>
  <b>
    <c>
      <e name='alias' setting=''/>
    </c>
    <a>test streng</a>
  </b>
</a>
    """
