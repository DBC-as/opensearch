#!/usr/bin/env python
# -*- coding: utf-8 -*- 

from xml.dom import minidom
from xml.dom import Node
from pprint import pprint
import logging

class XSEMGenerator( object ):
    """Class for generating XSEM mapping files from an xsd
    """

    def __init__(self, file_name):
        """
        
        Arguments:
        - `xml_string`: A string containing the xml (XML Schema) to be processed
        """
        
        self.xml_tree = minidom.parse( file_name )

#         self.xmldict = XmlDictConfig( ''.join( document.toxml().split( "\n" ) ) )

        self.xsd_datatypes =[
            'string',
            'boolean',
            'decimal',
            'float',
            'double',
            'duration',
            'dateTime',
            'time',
            'date',
            'gYearMonth',
            'gYear',
            'gMonthDay',
            'gDay',
            'gMonth',
            'hexBinary',
            'base64Binary',
            'anyURI',
            'QName',
            'NOTATION']

        self.xsd_derived_datatypes = [
            'normalizedString',
            'token',
            'language',
            'NMTOKEN',
            'NMTOKENS',
            'Name',
            'NCName',
            'ID',
            'IDREF',
            'IDREFS',
            'ENTITY',
            'ENTITIES',
            'integer',
            'nonPositiveInteger',
            'negativeInteger',
            'long',
            'int',
            'short',
            'byte',
            'nonNegativeInteger',
            'unsignedLong',
            'unsignedInt',
            'unsignedShort',
            'unsignedByte',
            'positiveInteger']

        self.xsd_types = self.xsd_datatypes+self.xsd_derived_datatypes

        logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(levelname)s %(message)s',
#                     filename='strip_tags.debug',
#                     filemode='w'
                            )
        logging.getLogger('')

    def get_xpath( self, node ):
        """
        from http://cvs.4suite.org/viewcvs/lib/domtools.py and
        modified a bit

        Return an XPath expression that provides a unique path to
        the given node (supports elements, attributes, root nodes,
        text nodes, comments and PIs) within a document

        """
        # extension to the base Node.{dom element defintions}
        OTHER_NODES = {
            Node.TEXT_NODE: 'text',
            Node.COMMENT_NODE: 'comment',
            Node.PROCESSING_INSTRUCTION_NODE: 'processing-instruction'
            }
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
        elif node.nodeType in OTHER_NODES:
            #Text nodes, comments and PIs
            count = 1
            #Count previous siblings of the same node type
            previous = node.previousSibling
            while previous:
                if previous.nodeType == node.nodeType: count += 1
                previous = previous.previousSibling
            test_func = OTHER_NODES[node.nodeType]
            step = '%s()[%i]' % (test_func, count)
            ancestor = node.parentNode
        elif not node.parentNode:
            #Root node
            step = ''
            ancestor = node
        else:
            raise TypeError( "Unsupported node type %s"%node.__repr__ )
        if ancestor.parentNode:
            return self.test( ancestor ) + '/' + step
        else:
            return '/' + step
        
    

    def read_xsd( self, document ):
        """
        given an xml.dom document containing an xsd, read each node
        and return a list of the elements encountered from their name
        and the xpath expression needed to reach them in the xml
        document instance
        """

        """
        algorithm:
        ==========

        The hierarchy of an xsd is not mirrored in the natural
        hierarchy of the xml it is formulated in. Therefore, we need
        to walk a bit up and down the DOM tree in order to find the
        families of the elements.
        """

        for child in document.childNodes:
            #there is only one top level element node 
            if self.is_element_with_name( child, "xsd:element" ):
                #logging.debug( "element node has fields", pprint(dir(child) ) )
                logging.debug( "name=\"%s\""% child.getAttribute( 'name' ) )


                #if element has no type attribute, it has a nested definition
                if not child.hasAttribute( 'type' ):
                    for nested_defs in child.childNodes:
                        if self.is_element_with_name( nested_defs, "xsd:complexType" ):
                            print "nested_defs is complextype"
                            pass
#                             logging.debug( self.is_element_with_name( self.find_type( nested_defs ), "xsd:element" ) )
                        

                sys.exit(0)
                self.find_node_name( child, child.getAttribute( 'name' ))
#                 for sublings in document.

                
    def is_element_with_name( self, node, name ):
        """ small boolean helper function"""
        logging.debug( "%s is element_node: %s" %(node.nodeName, ( Node.ELEMENT_NODE == node.nodeType) ) )
        logging.debug( "%s == %s is %s"% (node.nodeName, name, (node.nodeName == name ) ) )

        return Node.ELEMENT_NODE == node.nodeType and node.nodeName == name

    def find_type( self, node ):
        logging.debug( "name of node to find type attribute in = %s"% node.nodeName )
#         logging.debug( self.is_element_with_name( node, 'xsd:element' ) )
        if self.is_element_with_name( node, 'xsd:element' ):
            logging.debug( "found node with name %s"%node.nodeName )
            return node
        else: #go one down
            for children in node.childNodes:
                logging.debug( "iterating on ",children )
                self.find_type( children )
    
    def find_node_name( self, node, nodename ):
        logging.debug( nodename )
        for child in node.getElementsByTagName( nodename ):
            logging.debug( child )


if __name__ == '__main__':
    import sys
    c = XSEMGenerator( sys.argv[1] )

    c.read_xsd( c.xml_tree.documentElement )


