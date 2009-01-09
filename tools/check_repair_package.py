#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import os, sys
import re

from optparse import OptionParser
from optparse import OptionGroup

_import_list   = []
_files_checked = 0

def get_root_folder( current_wd, src="src" ):
    """
    Given the current work directory, ascend until we reach 'src' or
    some user-specified top-level from which the java packages
    descends and return the path.

    >>> get_root_folder( '/a/b/src/d/e' )
    'd/e'
    >>> get_root_folder( '/a/b/test/d/e', 'test' )
    'd/e'
    >>> get_root_folder( '/src/', 'src' )
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
        if '.svn' in dirs:
            dirs.remove( '.svn' ) 
        elif 'CVS' in dirs:
            dirs.remove( 'CVS' )
        for fil in files:
            if( '.' in fil and fil.split( '.' )[1] == 'java' ):
                yield root+os.path.sep+fil
            
def check_package_name( javafile, package, reallydoit=False, do_backups=True ):
    """ Given a (java)file and a packagename, this method opens the
    corresponding file and checks if the package name looks right
    """
    global _files_checked
    ofo = open( javafile, 'r' )
    java_stmt = ofo.readlines()
    ofo.close()
    new_java_stmt = []

    rxstr = r"""^(?:package) (?P<package>(?:\w+)(?:\.(?:\w+))*);"""
    compile_obj = re.compile( rxstr )
    for line in java_stmt:
        if compile_obj.search( line ) is not None:
            found_pkg = compile_obj.match( line ).group( 'package' )
            if found_pkg == package:
                _files_checked = _files_checked + 1
            else:
                print "\n%s"%( javafile )
                print "%s does not look right, it should be %s"%( line, package )
                print "should i do something about it?"
                if reallydoit:
                    print "just doing it"
                    line = "package %s;"%( package )
                    print "line is now \n%s"%( line )
                else:
                    inp = raw_input( "y/n" )
                    if inp == "y" :
                        line = "package %s;"%( package )
                        print "line is now \n%s"%( line )
                    
        new_java_stmt.append( line )

    if new_java_stmt != java_stmt:
        print "writing new file %s\nbacking up the old one in %s"%( javafile, os.path.abspath( os.path.curdir )+os.path.sep+javafile+'_bak')

        fo = open( javafile, 'w' )
        fo.writelines( new_java_stmt )
        fo.close()

        if do_backups:
            fo = open( os.path.abspath( os.path.curdir )+os.path.sep+javafile+"_bak", 'w' )
            fo.writelines( java_stmt )
            fo.close()
        
def _test( verbosity=False ):
    import doctest
    doctest.testmod( verbose=verbosity )

def main( arguments, options):
    for i in get_java_files( options.src ):
        _import_list.append( os.path.split( get_root_folder( i ) )[0].replace( '/', '.' )+'.'+os.path.split( os.path.abspath(i))[1].split( '.' )[0] )
        check_package_name( i, os.path.split( get_root_folder( i ) )[0].replace( '/', '.' ), options.reallydoit, options.backup )

    #print _import_list
    print "Files checked = %s"% ( _files_checked )

if __name__ == '__main__':

    usage = """%prog -h | [-t] [-s|--source sourcefolder]"""

    parser = OptionParser( usage=usage )

    parser.add_option( "-t", "--test", dest="test", action="store_true", 
                       default=False, help="runs doctests on the program")
    parser.add_option( "-v", "--verbosetest", dest="vtest", action="store_true", 
                       default=False, help="runs doctests on the program and prints status of each test")
    parser.add_option( "--yestoall", dest="reallydoit", action="store_true", 
                       default=False, help="only use this option if you're absolute sure you know what I do. I try to backup everything. But given my authors mental capabilities, there every reason to expect fuckups")
    parser.add_option( "--dont_do_backups", dest="backup", action="store_true", 
                       default=False, help="If this is set, don't backup of changes")

    parser.add_option( "-s", "--source", 
                       type="string", action="store", dest="src",
                       help="The path to the source folder of the java files to check")

    (options, args) = parser.parse_args()

    if options.vtest or options.test:
        sys.exit( _test( options.vtest ) )

    if options.src is not None:
        main( args, options )

    else:
        sys.exit( parser.print_help() )
