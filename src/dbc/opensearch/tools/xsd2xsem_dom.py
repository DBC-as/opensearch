#!/usr/bin/env python
# -*- coding: utf-8 -*- 

from xml.dom import minidom
from xml.dom import Node
from xml.etree import ElementTree
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

        # the instance_model holds all the paths traversed to reach
        # the xpaths of the elements of the instance document
        self.instance_model = {}

        self.instance_model_list = []

        self.node = ""
        self.name = ""
        self.parentName = ""

        self.xml_instance_root = None
        
        # for a given path, this variable tracks if the path is a
        # necessary xpath of the instance document or an optional one.
        # or perhaps we should just be lenient and let all elements
        # have their chance. Empty field has never killed an index...
        self.optional = False

        # the mappings dict hold the data needed to build a mapping
        # from an xpath expression to a compass field
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
            logging.debug( "searching for %s, node == %s, parent == %s", search_criteria, child.nodeName, self.find_parent_element_name( child ) )
            if child.parentNode.nodeName == "xsd:schema" and \
                   self.is_element_with_name( child, search_criteria ):
                logging.debug( "name=\"%s\"", child.getAttribute( 'name' ) )

                # if child has att name, it is in the instance model:
                if child.hasAttribute( 'name' ):
                    self.instance_model = { child.getAttribute( 'name' ) : '' }
                    self.instance_model_list = [ child.getAttribute( 'name' ) ]
                    self.xml_instance_root = ElementTree.Element( "fakeroot" )
                    ElementTree.SubElement( self.xml_instance_root, child.getAttribute( 'name' ) )

                    self.parentName = child.getAttribute( 'name' )
                #if element has no type attribute, it has a nested definition
                if not child.hasAttribute( 'type' ):
                    logging.debug( "node %s has no attribute type, searching for complexType", child.nodeName )
                    self.read_xsd( child, [ 'xsd:element', 'xsd:attribute', 'xsd:complexType' ] )

            # otherwise, if the parent is not the schema, we should
            # end down here, with the rest of the nodes
            elif self.is_element_with_name( child, search_criteria ):

                if child.hasAttribute( 'type' ):
                    try:
                        datatype = child.getAttribute( 'type' ).split( ':' )[1]
                    except IndexError:
                        datatype = child.getAttribute( 'type' )

                    logging.debug( "node has attribute type == %s", datatype )
                    logging.debug( "type %s in xsd_datatypes == %s", datatype, ( datatype in self.xsd_datatypes ) )

                    if datatype in self.xsd_datatypes:
                        self.instance_model[ child.getAttribute( 'type' ) ] = datatype
                        self.instance_model_list.append( [ child.getAttribute( 'name' ), datatype ] )

                    else:
                        self.read_xsd( child, [ 'xsd:element', 'xsd:attribute' ] )

                    # if child has att name, it is in the instance model:
                    if child.hasAttribute( 'name' ):

#                         if child.nodeName == "xsd:attribute":

                        self.instance_model[ self.find_parent_type_from_type( child, self.find_parent_element_name( child ) ) ] = { child.getAttribute( 'name' ) : child.getAttribute( 'type' ) }
                        self.instance_model_list.append( [ self.find_parent_element_name( child ), [ child.getAttribute( 'name' ), child.getAttribute( 'type' )  ] ] )

                        name = self.find_parent_element_name( child )
#                         print "parent element name = %s"%name
#                         print "this element name   = %s"%child.getAttribute( 'name' )
#                         print "xml is              = %s"%ElementTree.tostring( self.xml_instance_root )
#                         print "find element yields = %s"%self.xml_instance_root.find( ".//"+name )
#                         print "find element yields = %s"%self.xml_instance_root.find( name )

                        hej  = self.xml_instance_root.find( ".//"+name ) if self.xml_instance_root.find( ".//"+name ) != None else self.xml_instance_root.find( name )
                        if hej is None:
                            print "xml is              = %s"%ElementTree.tostring( self.xml_instance_root )
                            print "tried to find       = %s"%child.getAttribute( 'name' )
                            print "parent element name = %s"%name
                        
                        node = ElementTree.SubElement( hej, child.getAttribute( 'name' ) )
                        ElementTree.SubElement( node , child.getAttribute( 'type' ) )
                        


                else:
                    logging.debug( "node %s has no type, going to children", child.nodeName )
                    self.read_xsd( child, [ 'xsd:element', 'xsd:attribute', 'xsd:complexType' ] )

            else: #recurse
                self.read_xsd( child, search_criteria )
                

    def find_parent_element_name( self, node ):
        """
        This method is only appliable for XSDs

        Given a node, this function returns the immediate parent that
        has a name of a complexType
        """
        #root nodes are different objects, but sometimes accidentially
        #gets down here, so we handle them
        if node.parentNode.nodeName == "xsd:schema":
            # this is a bad bad hack. please fix me
            return self.parentName
        if node.parentNode.hasAttribute( 'name' ):
            return node.parentNode.getAttribute( 'name' )
        else:
            return self.find_parent_element_name( node.parentNode )

    def find_parent_type_from_type( self, node, name ):
        #root nodes are different objects, but sometimes accidentially
        #gets down here, so we handle them
        if node.parentNode.nodeName == "xsd:schema":
            # this is a bad bad hack. please fix me
            logging.debug( "returning default %s"%( self.parentName) )
            return self.parentName
        if node.parentNode.hasAttribute( 'name' ) and node.parentNode.getAttribute( 'name' ) == name:
            logging.debug( "returning %s "% node.parentNode.getAttribute( 'name' ) )
            return node.parentNode.getAttribute( 'type' )
        else:
            logging.debug( "* recursing on %s, %s:"% (node.parentNode, name ) )
            return self.find_parent_type_from_type( node.parentNode, name )

#     def recurse_parents( self, node, name ):
#         if node.hasAttributes() and node.hasAttribute( 'name' ):
#             if node.getAttribute( 'name' ) == name:
#                 return node
#         else:
#             return self.recurse_parents( node.parentNode, name )


    #obsolete?
    def find_entity(self, sub_tree,search_criteria):
        """
        
        Arguments:
        - `sub_tree`: the node-tree to search for the search_criteria
        - `search_criteria`: a list of the elements that we are searching for,
                             eg. [ 'xsd:element', 'xsd:attribute' ]
        
        returns the first node node that matches the criteria
        """
        for child in sub_tree.childNodes:
            logging.debug( "node == %s", child.nodeName )
            if child.nodeName in search_criteria:
                return child
            else:
                return self.find_entity( child, search_criteria)
              
    def is_element_with_name( self, node, name ):
        """
        This method is only appliable for XSDs
        
        returns true iff node is xsd:element and has parameter name as
        name
        """
        return Node.ELEMENT_NODE == node.nodeType and node.nodeName in name

    def xml_from_dict( self, dict ):
        elem = ElementTree.Element("compass-core-mapping")
        return ElementTree.tostring(elem)

    # obsolete 
#     def find_element( self, child ):
#         for nested_defs in child.childNodes:
#             # if element is a complextype, we go down that tree to
#             # find the contained elements
#             if self.is_element_with_name( nested_defs, "xsd:complexType" ):
#                 logging.debug( "nested_defs is complextype" )
#                 for sublings in nested_defs.childNodes:
#                     pass

    #obsolete 
#     def find_type_attribute( self, node ):
#         logging.debug( "name of node to find type attribute in = %s"% node.nodeName )
# #         logging.debug( self.is_element_with_name( node, 'xsd:element' ) )
#         if self.is_element_with_name( node, 'xsd:element' ):
#             logging.debug( "found node with name %s"%node.nodeName )
#             return node
#         else: #go one down
#             for children in node.childNodes:
#                 logging.debug( "iterating on ",children )
#                 self.find_type_attribute( children )
    

if __name__ == '__main__':
    import sys
    c = XSEMGenerator( sys.argv[1] )
    
    # poor mans file check. Works only if the files are named after convention
    filetokens = sys.argv[1].split( '.' )

    if filetokens[ len(filetokens) - 1 ] == 'xml':
        for thing in c.get_leaf_elements( c.xml_tree.documentElement ):
            print "\n", thing.nodeName
            print c.get_xpath( thing )

            
    elif filetokens[ len(filetokens) - 1 ] == 'xsd':
        c.read_xsd( c.xml_tree.documentElement )

    #     pprint( c.instance_model_list )
        
#     print ElementTree.tostring( c.xml_instance_root )

    sys.exit(0)
    def find_abs_path( liste, navn, indent=0 ):
        for element in liste:
            if type(element) == type(list()):
                find_abs_path( element, "", indent+1 )
            else:
                print indent*" "+element
            

    find_abs_path( c.instance_model_list, "fileEntry" )
