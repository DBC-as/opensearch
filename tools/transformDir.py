#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-


# This file is part of opensearch.
# Copyright © 2009, Dansk Bibliotekscenter a/s, 
# Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
#
# opensearch is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# opensearch is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with opensearch.  If not, see <http://www.gnu.org/licenses/>.


import libxml2
import libxslt
import logging
import os
import os.path
import string
import datetime
import sys


def transform( xmlFile, destFile, xsltFile ):
    """
    Transforming document
    """

    print "parsing "+xmlFile
    
    styledoc = libxml2.parseFile( xsltFile )
    style = libxslt.parseStylesheetDoc( styledoc )
    doc = libxml2.parseFile( xmlFile )
    
    result = style.applyStylesheet( doc, None )
    style.saveResultToFilename( destFile, result, 0 )
    style.freeStylesheet()
    doc.freeDoc()
    result.freeDoc()


def main( src, dest, xslt, overwrite ):

    print ""
    print("-"*70)
    now = datetime.datetime.now()
    print "Starting Transformation ", now.ctime() ;
    print("-"*70)
    
    if os.path.isfile( dest ):
        os.remove( dest )
    if not os.path.isdir( dest ):
        os.mkdir( dest )

    files_transformed = 0
    for root, dirs, files in os.walk(src):
        
        for f in files:
            src_file = os.path.abspath( os.path.join( root, f )  )
            dest_file = os.path.join( dest, f  )
            
            if os.path.exists( dest_file ) and not overwrite:
                print( "cannot write file='%s' already exists"%dest_file )
                # log no go
            else:
                print( "\n--> Starting Transformation on file '%s' to '%s'"%( src_file, dest_file ) )
                try:
                    transform( src_file, dest_file, xslt )
                except libxml2.parserError: 
                    print( "Cannot parse file '%s'"%src_file )

if __name__ == '__main__':
    
    from optparse import OptionParser
    
    import sys

    parser = OptionParser( usage="%prog [options] -x xslt src dest" )

    parser.add_option( "-x", "--xslt",type="string", action="store", dest="xslt",
                       help="the xslt sheet.")

    parser.add_option( "-o", "--overwrite", action="store_true", dest="overwrite", default="False",
                       help="if destination file or folder exists it will be overwritten if this flag is set.")

    (options, args) = parser.parse_args()

    if not options.xslt:
        sys.exit( "a xslt sheet must be specified." )
    if not os.path.isfile( options.xslt ):
        sys.exit( "file '%s' does not exist."%options.xslt )

    if len( args ) > 2:
        print "more than 2 arguments. ignoring the rest"
    if len( args ) < 2:
        sys.exit( "both sourcec and destination folder must be specified" )
    if not os.path.isdir(args[0]):
        sys.exit( "'%s' is not a directory."%options.xslt )
    if os.path.exists(args[1]) and not options.overwrite:
        sys.exit( "folder or file '%s' exists. use --overwrite if you mean it."%args[0] )

    main( os.path.abspath( args[0] ), os.path.abspath( args[1] ), os.path.abspath( options.xslt ), options.overwrite )

