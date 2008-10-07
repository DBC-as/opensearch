#!/usr/bin/env python
# -*- coding: utf-8 -*- 

from xml.dom import minidom
from xml.dom import Node
from pprint import pprint
import logging

class XSEMGenerator( object ):
    """Class for generating XSEM mapping files from an xsd
    """

    def __init__( self, file_name ):
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

        # the instance_model holds all the paths traversed to reach the xpaths of the elements of the instance document
        self.instance_model = {}

        self.node = ""
        self.name = ""

        # for a given path, this variable tracks if the path is a necessary xpath of the instance document or an optional one.
        # or perhaps we should just be lenient and let all elements have their chance. Empty field has never killed an index...
        self.optional = False

        # the mappings dict hold the data needed to build a mapping from an xpath expression to a compass field
        self.mappings = {}

        # instance counter for uid'ing the mappings
        self.mappingscounter = 0

        
        logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(levelname)s %(message)s',
                    filename='xsd2xsem.debug',
                    filemode='w'
                            )
        logging.getLogger('')

    def get_xpath( self, node ):
        """
        from http://cvs.4suite.org/viewcvs/lib/domtools.py and
        modified a bit

        Return an XPath expression that provides a unique path to
        the given node (supports elements, attributes, root nodes,
        text nodes, comments and PIs) within a document

        >>> c = XSEMGenerator()


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
#         elif node.nodeType in OTHER_NODES:
#             #Text nodes, comments and PIs
#             count = 1
#             #Count previous siblings of the same node type
#             previous = node.previousSibling
#             while previous:
#                 if previous.nodeType == node.nodeType: count += 1
#                 previous = previous.previousSibling
#             test_func = OTHER_NODES[node.nodeType]
#             step = '%s()[%i]' % (test_func, count)
#             ancestor = node.parentNode
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

    def read_xsd( self, document, search_criteria=[ 'xsd:element' ] ):
        """
        given an xml.dom document containing an xsd, read each node
        and return a list of the elements encountered from their name
        and the xpath expression needed to reach them in the xml
        document instance

        Remarks
        =======

        The hierarchy of an xsd is not mirrored in the natural
        hierarchy of the xml it is formulated in. Therefore, we need
        to walk a bit up and down the DOM tree in order to find the
        families of the elements.

        
        """

        for child in document.childNodes:
            
            #there is only one top level element node, let's find it: 
            logging.debug( "searching for %s, node == %s", search_criteria, child.nodeName )
            if child.parentNode.nodeName == "xsd:schema" and self.is_element_with_name( child, search_criteria ):
                logging.debug( "name=\"%s\"", child.getAttribute( 'name' ) )
                     
                #if element has no type attribute, it has a nested definition
                if not child.hasAttribute( 'type' ):
                    logging.debug( "node %s has no attribute type, searching for complexType", child.nodeName )
                    self.read_xsd( child, [ 'xsd:element', 'xsd:attribute', 'xsd:complexType' ] )

            # otherwise, we should end down here, with the rest of the nodes
            elif self.is_element_with_name( child, search_criteria ):
                if child.hasAttribute( 'type' ):
                    try:
                        datatype = child.getAttribute( 'type' ).split( ':' )[1]
                    except IndexError:
                        datatype = child.getAttribute( 'type' )
                    logging.debug( "node has attribute type == %s", datatype )
                    logging.debug( "type %s in xsd_datatypes == %s", datatype, ( datatype in self.xsd_datatypes ) )

                    if datatype in self.xsd_datatypes:
                        print "xpath expr: %s"% self.get_xpath( child )
                    else:
                        self.read_xsd( child, [ 'xsd:element', 'xsd:attribute' ] )
                else:
                    logging.debug( "node %s has no type, going to children", child.nodeName )
                    self.read_xsd( child, [ 'xsd:element', 'xsd:attribute', 'xsd:complexType' ] )

                    print child.nodeName
                    print child.nodeValue
                    print child.attributes.item(0)

            else:
                self.read_xsd( child, search_criteria )
                
#                     logging.debug( "name=\"%s\"", child.item( c-1 ) )
                

    def find_entity(self, sub_tree,search_criteria):
        """
        
        Arguments:
        - `sub_tree`: the node-tree to search for the search_criteria
        - `search_criteria`: a list of the elements that we are searching for, eg. [ 'xsd:element', 'xsd:attribute' ]
        
        returns the first node node that matches the criteria
        """
        for child in sub_tree.childNodes:
            logging.debug( "node == %s", child.nodeName )
            if child.nodeName in search_criteria:
                return child
            else:
                return self.find_entity( child, search_criteria)
        


                
    def is_element_with_name( self, node, name ):
        """ small boolean helper function"""
#         logging.debug( "%s is element_node: %s" %(node.nodeName, ( Node.ELEMENT_NODE == node.nodeType) ) )
#         logging.debug( "%s == %s is %s"% (node.nodeName, name, (node.nodeName == name ) ) )

        return Node.ELEMENT_NODE == node.nodeType and node.nodeName in name

    def find_element( self, child ):
        for nested_defs in child.childNodes:
            # if element is a complextype, we go down that tree to find the contained elements
            if self.is_element_with_name( nested_defs, "xsd:complexType" ):
                logging.debug( "nested_defs is complextype" )
                for sublings in nested_defs.childNodes:
                    pass
#                    logging.debug( self.is_element_with_name( self.find_type( nested_defs ), "xsd:element" ) )


    #obsolete ?
    def find_type_attribute( self, node ):
        logging.debug( "name of node to find type attribute in = %s"% node.nodeName )
#         logging.debug( self.is_element_with_name( node, 'xsd:element' ) )
        if self.is_element_with_name( node, 'xsd:element' ):
            logging.debug( "found node with name %s"%node.nodeName )
            return node
        else: #go one down
            for children in node.childNodes:
                logging.debug( "iterating on ",children )
                self.find_type_attribute( children )
    
    # obsolete ?
    def find_node_name( self, node, nodename ):
        logging.debug( nodename )
        for child in node.getElementsByTagName( nodename ):
            logging.debug( child )


if __name__ == '__main__':
    import sys
    c = XSEMGenerator( sys.argv[1] )

    c.read_xsd( c.xml_tree.documentElement )
    
    # poor mans file check. Works only if the files are named after convention
    filetokens = sys.argv[1].split( '.' )

    if filetokens[ len(filetokens) - 1 ] == 'xml':
        for thing in c.get_leaf_elements( c.xml_tree.documentElement ):
            print "\n", thing.nodeName
            print c.get_xpath( thing )
    elif filetokens[ len(filetokens) - 1 ] == 'xsd':
        for mapping in c.mappings:
            print "\n", mapping
