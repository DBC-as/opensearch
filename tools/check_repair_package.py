#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import os, sys
import re

from optparse import OptionParser
from optparse import OptionGroup

import logging as log

log.basicConfig( level = log.DEBUG,
                format = '%(asctime)s %(levelname)s %(message)s' )
log.getLogger( '' )


"""
The aim of this module is to check and repair the package names and
imports of files that have been moved from their original
placement. The program expects files to be located correpondingly to
their package names. E.g. a java file with the package declaration

package com.example.component.db;

should reside in (a relative path ending with):

src/com/example/component/db/NameOfJavaFile.java

Furthermore, the program tries to fix broken imports. It does this by
building a list of types and then checking the imports against this
list. The program tries a depth (packagelevels-1) for qualifying the
types. To illustrate:
given a typelist
{ 'TypeA' : [ 'com.examples.components', 'com.example', 'com.other'],
  'TypeB' : ...
}

and the file being checked has a package space of

com.example.components

with an import of

com.example.components.types.TypeA;

the user should recive an interactive query on:

1: com.example.components.TypeA
2: com.example.TypeA
3: com.other.TypeA

However, when the choice is not unequivocal, the program ask the user
interactively.

"""

_files_checked = 0

class ImportDict( dict ):
    """ ImportDict extends the builtin dict with the method `insert`,
    with the special feature, that the values are stored in a list.
    `insert` takes a key/value pair and does one of the following:
    
    if the key is not in the dict:
        inserts the key/value, with value being a list with one item

    if the key is in the dict and the value is already associated with the key
        the insertion is dropped silently

    if the key is in the dict but the value is not associated with it
        the value is appended to the list of values associated with the key

    """

    def insert( self, key, value ):
        if not self.has_key( key ):
            super( ImportDict, self ).__setitem__( key, [ value ] )
        elif self.has_key( key ) and ( value in self.get( key ) ):
            pass
        else:
            values = self.get( key )
            values.append( value )
            super( ImportDict, self ).__setitem__( key, values )

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
            
def check_and_fix_package_name( java_stmt, package, reallydoit=False ):
    """ Given a (java)file and a packagename, this method opens the
    corresponding file and checks if the package name looks right.

    If the package has changed, the method returns the changed java
    file as a list ready to be written to a file using
    file.writelines()
    """
    global _files_checked
    new_java_stmt = []
    
    _files_checked = _files_checked + 1

    rxstr = r"""^(?:package) (?P<package>(?:\w+)(?:\.(?:\w+))*);"""
    compile_obj = re.compile( rxstr )
    for line in java_stmt:
        if compile_obj.search( line ) is not None:
            found_pkg = compile_obj.match( line ).group( 'package' )

            if found_pkg != package:
                print "%s does not look right, it should be %s"%( line, package )
                print "should i do something about it?"
                if reallydoit:
                    line = "package %s;"%( package )
                else:
                    inp = raw_input( "y/n\n" )
                    if inp == "y" :
                        if package == '.':
                            #this is the default package and implies no package declaration:
                            line = ''
                        else:
                            line = "package %s;\n"%( package )
                    
        new_java_stmt.append( line )

    if new_java_stmt != java_stmt:
        return new_java_stmt
    else:
        return None

def check_and_fix_imports( java_stmt, imports_dict ):
    """given a list of strings (aka lines in a java file) containing a
    java statement and an imports dict, this function checks the
    imports used in the statement and tries to remedy any imports the
    looks conspicuous.

    This could probably be much faster just using one string and regex
    substitution, but it would also be a lot less readable and speed
    is not an issue here.
    
    """
    global _files_checked
    _files_checked = _files_checked + 1

    new_java_stmt = []

    # The following regular expression does not handle star imports (
    # java.io.* ), which we regard as an aborration anyway. It also
    # doesn't handle imports from the default namespace, and neither
    # should you.
    rxstr = r"""(?:import (?P<import>(?:\w+)(?:\.(?P<type>(?:\w+)))+);)"""
    compile_obj = re.compile( rxstr )
    
    for line in java_stmt:
        if compile_obj.match( line ) is not None:
            found_imp = compile_obj.match( line ).group( 'import' )

            log.debug( "found imports: %s"%(found_imp) )
            package, dot, jtype = found_imp.rpartition( '.' )
            log.debug( "package=%s, jtype=%s"%( package, jtype ) )
            #print "type is %s"%(jtype)
            log.debug( "found possible namespaces: %s"%( imports_dict.get( jtype ) ) )
            if imports_dict.get( jtype ) is None:
                log.debug( "There are no package namespaces for %s"%( jtype ) )
                pass

            elif jtype in imports_dict and len( imports_dict.get( jtype ) ) == 1:

                packages = imports_dict.get( jtype )
                if len( packages ) == 1:
                    #there is only one type to consider, and this is it
                    log.debug( "%s matches import from the namespace %s"%( found_imp, packages[0] ) )
                if package in packages:
                    log.debug( "%s matches import from the namespace %s"%( found_imp, package ) )
                else:
                    #there is more than one namespace containing the type name
                    for namespace in packages:
                        log.debug( "namespace: %s"%( namespace ) )

            elif package in imports_dict.get( jtype ):
                log.debug( "import is %s"%( package+dot+jtype ) )


            else:
                print "%s could not be directly matched."%(jtype)
                qns = raw_input( "Please give me a namespace to check against\n" )
                implst = get_import_candidates( found_imp, imports_dict, qns )

                print "found import candidates:"

                if implst is None:
                    pass
                else:

                    for no, imp in enumerate( implst ):
                        print "%s: %s"%(no, imp)

                    choice = raw_input( "please enter choice or q for none: ")
                    if choice == "q":
                        pass
                    else:
                        new_imp = ""
                        try:
                            new_imp = implst[int(choice)]
                        except TypeError:
                            log.error( "satans, TypeError" )
                        except IndexError:
                            log.error( "satans, IndexError" )

                        print "you choosed %s"%( new_imp )
                        line = "import %s;\n"%( new_imp )

        new_java_stmt.append( line )

    if new_java_stmt != java_stmt:
        return new_java_stmt
    else:
        return None

def get_import_candidates( import_statement, imports_dict, qualification_ns="" ):
    """
    `import_statement` is the name of the qualified type from the java
    import (eg. 'org.types.TypeA') 

    `imports_dict` is the ImportDict instance containing the
    recognised types given with options.src from the commandline
    options
    
    `qualification_ns` defaults to the empty namespace, ie. the
    default packages. It specifies the granularity of the
    search. E.g. if the q_ns is 'org', then only types whose
    package-namespace starts with 'org' are considered as candidates.

    >>> get_import_candidates( 'org.types.TypeA', { 'TypeA': [ 'org.types' ] } )
    >>> get_import_candidates( 'org.types.TypeA', { 'TypeA': [ 'org.types' ] }, 'org' )
    ['org.types.TypeA']
    >>> get_import_candidates( 'org.types.TypeA', { 'TypeA': [ 'org.types' ] }, 'com' )
    >>>
    >>> get_import_candidates( 'org.TypeB', { 'TypeB': [ 'org' ] }, 'org' )
    ['org.TypeB']
    >>> get_import_candidates( 'org.components.types.TypeA', { 'TypeA': [ 'org.components', 'org.types' ] }, 'org' )
    ['org.components.TypeA', 'org.types.TypeA']
    """
    if qualification_ns == "":
        return None

    package, dot, jtype = import_statement.rpartition( '.' )
    
    hierarchy = package.split( '.' )
    ns_hierar = qualification_ns.split( '.' )
    pack_list = imports_dict.get( jtype )

    log.debug( "hierarchy %s (%s)"%( hierarchy, len(hierarchy) ) )
    log.debug( "ns_hierar %s (%s)"%( ns_hierar, len(ns_hierar) ) )
    log.debug( "pack_list %s"%( pack_list) )

    # if there's not a toplevel match, there will never be one
    if ns_hierar[0] != hierarchy[0]:
        return None

    if len( pack_list ) == 1:
        #just return the type
        log.debug( "returning pack_list[0]+dot+jtype=%s"%( [ pack_list[0]+dot+jtype ] ) )
        return [ pack_list[0]+dot+jtype ]
    else:
        # return the types that match the level in ns_hierarchy
        return_list = []

        for ns in pack_list:
            if ns.startswith( qualification_ns ):
                return_list.append( ns+dot+jtype )

        if len(return_list) == 1:
            log.debug( "returning return_list[0]=%s"%( [ return_list[0] ] ) ) 
            return [ return_list[0] ]
        else:
            log.debug( "returning return_list=%s"%( return_list ) )
            return return_list

def get_import_dict( java_file_list ):
    import_dict = ImportDict()
    
    for i in java_file_list:
        import_dict.insert( os.path.split( os.path.abspath( i ) ) [1].split( '.' )[0], \
                            os.path.split( get_root_folder( i ) )[0].replace( '/', '.' ) )
    return import_dict

def get_import_list( java_file_list ):
    """DEPRECATED"""
    import_list = []    

    for i in java_file_list:
        import_list.append( os.path.split( get_root_folder( i ) )[0].replace( '/', '.' )+ \
                            '.'+ \
                            os.path.split( os.path.abspath( i ) ) [1].split( '.' )[0] )
    return import_list

def get_package_list( java_file_list ):
    package_list = []
    for i in java_file_list:
        package_list.append( os.path.split( get_root_folder( i ) )[0].replace( '/', '.' ) )
    return set(package_list)


def main( arguments, options):

    if options.check_packages:
        pkgs = get_package_list( get_java_files( options.src ) )

        # the following loop checks and fixes wrong package names in the
        # path given through options.src
        for i in get_java_files( options.src ):

            ofo = open( i, 'r' )
            java_stmt = ofo.readlines()
            ofo.close()

            new_java_stmt = check_and_fix_package_name( java_stmt , \
                             os.path.split( get_root_folder( i ) )[0].replace( '/', '.' ), \
                             options.reallydoit )

            if new_java_stmt is not None:
                print "writing new file %s\nbacking up the old one in %s" \
                    %( i, os.path.abspath( os.path.curdir )+os.path.sep+i+'_bak')

                fo = open( i, 'w' )
                fo.writelines( new_java_stmt )
                fo.close()

                if not options.no_backup:
                    fo = open( os.path.abspath( os.path.curdir )+os.path.sep+i+"_bak", 'w' )
                    fo.writelines( java_stmt )
                    fo.close()

    if options.check_imports:
        imps = get_import_dict( get_java_files( options.src ) )
        print "checking imports"

        for i in get_java_files( options.src ):
            ofo = open( i, 'r' )
            java_stmt = ofo.readlines()
            ofo.close()

            new_java_stmt = check_and_fix_imports( java_stmt , \
                                                   imps )

            if new_java_stmt is not None:
                print "writing new file %s\nbacking up the old one in %s" \
                    %( i, os.path.abspath( os.path.curdir )+os.path.sep+i+'_bak')

                fo = open( i, 'w' )
                fo.writelines( new_java_stmt )
                fo.close()

                if not options.no_backup:
                    fo = open( os.path.abspath( os.path.curdir )+os.path.sep+i+"_bak", 'w' )
                    fo.writelines( java_stmt )
                    fo.close()

    print "Files checked = %s"% ( _files_checked )

def _test( verbosity=False ):
    import doctest
    doctest.testmod( verbose=verbosity )

if __name__ == '__main__':

    usage = """%prog -h | [-t] [-s|--source sourcefolder]"""

    parser = OptionParser( usage=usage )

    parser.add_option( "-t", "--test", dest="test", action="store_true", 
                       default=False, help="runs doctests on the program")
    parser.add_option( "-v", "--verbosetest", dest="vtest", action="store_true", 
                       default=False, help="runs doctests on the program and prints status of each test")
    parser.add_option( "-i", "--check_imports", dest="check_imports", action="store_true", 
                       default=False, help="Check imports in files in src")
    parser.add_option( "-p", "--check_packages", dest="check_packages", action="store_true", 
                       default=False, help="Check package names in files in src")

    parser.add_option( "--yestoall", dest="reallydoit", action="store_true", 
                       default=False, help="only use this option if you're absolute sure you know what I do. I try to backup everything. But given my authors mental capabilities, there every reason to expect fuckups")
    parser.add_option( "--dont_do_backups", dest="no_backup", action="store_true", 
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
