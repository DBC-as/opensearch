#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import os, sys
import re

from optparse import OptionParser
from optparse import OptionGroup

def get_root_folder( current_wd, src="src" ):
    """
    Given the current work directory, ascend until we reach 'src' or
    some user-specified top-level from which the java packages
    descends and return the path.

    >>> get_root_folder( '/a/b/src/d/e' )
    'd/e'
    >>> get_root_folder( '/a/b/test/d/e', 'test' )
    'd/e'
    >>> get_root_folder( '/src/' )
    ''
    >>> get_root_folder( '/a/b' )
    Traceback (most recent call last):
    ...
    ValueError: /a/b does not contain src
    """

    if not src in current_wd.split( '/' ):
        raise ValueError( current_wd+' does not contain '+src )
    #path = os.path.abspath( current_wd )
    root = current_wd.split( src )
    
    pwd  = root[1][1:]

    return pwd

def get_java_files( path ):
    """
    Given a path, returns an iterator of the absolute path of the
    javafiles found within the path
    """
    for root, dirs, files in os.walk( path ):
#         print "root: %s"%(root)
#         print "dirs: %s"%(dirs)
        if '.svn' in dirs:
            dirs.remove('.svn') 

        for fil in files:
            if( '.' in fil and fil.split( '.' )[1] == 'java' ):
                yield root+os.path.sep+fil
            
def check_package_name( javafile, package ):
    """ Given a (java)file and a packagename, this method opens the
    corrosponding file and checks if the package name looks right
    """

    ofo = open( javafile, 'r' )
    java_stmt = ofo.readlines()
    ofo.close()
    new_java_stmt = []


    rxstr = r"""^(?:package) (?P<package>(?:\w+)(?:\.(?:\w+))*);$"""
    compile_obj = re.compile( rxstr )
    for line in java_stmt:
        if compile_obj.search( line ) is not None:
            found_pkg = compile_obj.match( line ).group( 'package' )
            if found_pkg == package:
                print ".",
            else:
                print "\n%s"%( javafile )
                print "%s does not look right, it should be %s"%( line, package )
                print "should i do something about it?"
                inp = raw_input( "y/n" )
                if inp == "y":
                    line = "package %s;"%( package )
                    print "line is now \n%s"%( line )
                    
        new_java_stmt.append( line )

    if new_java_stmt != java_stmt:
        print "writing new file %s"%(javafile)
        fo = open( javafile, 'w' )
        for line in new_java_stmt:
            fo.writeline( line )

        fo.close()
        
def _test( verbosity=False ):
    import doctest
    doctest.testmod( verbose=verbosity )

def main( arguments, options):
    #'/home/stm/entwicklung/dbc/OpenSearch/svn/'

    print arguments
    print options
    for i in get_java_files( options.src ):
        check_package_name( i, os.path.split( get_root_folder( i ) )[0].replace( '/', '.' ) )

if __name__ == '__main__':

    usage = """%prog -h | [-t] [-s|--source sourcefolder]"""

    parser = OptionParser( usage=usage )

    parser.add_option( "-t", "--test", dest="test", action="store_true", 
                       default=False, help="runs doctests on the program")
    parser.add_option( "-v", "--verbosetest", dest="vtest", action="store_true", 
                       default=False, help="runs doctests on the program and prints status of each test")
    parser.add_option( "-s", "--source", 
                       type="string", action="store", dest="src",
                       help="The path to the source folder of the java files to check")

    (options, args) = parser.parse_args()

    if options.vtest or options.test:
        sys.exit( _test( options.vtest ) )
      
    main( args, options )


