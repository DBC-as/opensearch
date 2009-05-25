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


import subprocess, sys, os, fnmatch
import datetime, os, shutil, sys

import exceptions
from subprocess import PIPE


files = [ 'dist_copy.py', 'xsd2xml.py' ]
trees = []

# Globals
src_dir = os.getcwd()

# default values
#server = 'sempu'
middle = ':./'
#folder = 'tst'

scp_dist = "scp -rp "

"""#########################################
   Functions for copying files to distribute
   #########################################"""
def __test_file( f ):
    if f.startswith( 'ant' ) and f.endswith( '.jar' ):
        return False
    elif f.startswith( '.' ) or f.startswith( 'tmp_' ):
        return False
    elif f.endswith( '.log' ) or f.endswith( 'indexes' ):
        return False
    elif f in files:
        return False
    else:
        return True    


def __cp_fldr( src, dst ):
    if not os.path.exists( dst ):
        os.mkdir( dst )

    files = os.listdir( src )
    for f in files:
        if __test_file( f ):
            shutil.copy2( src + f, dst + f )

def __cp_tree( src, dst ):
    shutil.copytree( src, dst, symlinks=False )


def __create_copy_dist( fldrs ):
    cur_dir = os.getcwd()

    # Create tmp dir. Suffix used for deletion later
    now = datetime.datetime.now()
    suffix = "tmp_" + str( now.day) + str( now.microsecond ) # DO NOT CHANGE!
    tmp_dir = cur_dir + '/' + suffix
    os.mkdir( tmp_dir )

    # Check that cwd is correct
    if not cur_dir.endswith( 'tools' ):
        sys.exit( "Script must be run from dir '.../tools/" )
    else:
        cur_dir = cur_dir.replace( "/tools", "" )

    # Copy files and folders to dst_dir preserving tree structure
    for name in sorted( os.listdir( cur_dir ) ):
        src = cur_dir + '/' + name + '/'
        dst = tmp_dir + '/' + name + '/' 
        if name in fldrs:
            #NOTE: name is not used....
            __cp_fldr( src, dst )
        elif name in trees:
            __cp_tree( src, dst )
            
    return suffix


""" #########################################################
    Functions for setting server and copying folder to server
    #########################################################"""

def __set_copy_from_folder( fldr ):
    global scp_dist
    scp_dist += fldr + '/* '


def __set_copy_to_server( srv ):
    global scp_dist
    if srv != '':
        server = srv        
    scp_dist += server


def __set_copy_to_folder( fldr ):
    global scp_dist 
    scp_dist += middle + fldr


def __make_dist( dist ):
    global src_dir
    global scp_dist

    if src_dir.endswith("tools"):
        src_dir = src_dir.replace("/tools", "" )

    if dist == 'all':
        runproc = subprocess.Popen( 'ant dist', shell=True, cwd=src_dir, stdin=PIPE, stderr=PIPE )
    else:
        dist_target = 'dist_'+dist
        runproc = subprocess.Popen( 'ant '+dist_target, shell=True, cwd=src_dir, stdin=PIPE, stderr=PIPE )
        
    stdin, stderr = runproc.communicate()
    if not stderr == '':
        raise Exception( stderr )
   
   
def __copy_dist():
    print scp_dist
    os.system( scp_dist )


def __check_conn( copy_server ):
    print 'testing connection to server'
    cmd = ''
    try:
        "snmap -p22 %s | grep 'open  ssh'"%copy_server
    except:
        raise Error( 'nmap does not seem to be installed on your box' )

    test = os.system( cmd )
    if test == '':
        sys.exit( "ssh on %s does not seem to be running" )


def __check_remote_folder_exists( remote_folder, server ):
    print "testing if remote folder exists"
    cmd = "ssh %s 'stat %s'"%( server, remote_folder )
    runproc = subprocess.Popen( cmd, shell=True, stdin=PIPE, stderr=PIPE )

    ret = runproc.communicate()

    if ret[1] != '':
        return False
    else:
        return True

def __create_remote_folder( folder_name, server ):
    print "testing if remote folder exists"
    cmd = "ssh %s 'mkdir %s'"%( server, folder_name )
    runproc = subprocess.Popen( cmd, shell=True, stdin=PIPE, stderr=PIPE )

    ret = runproc.communicate()



if __name__ == '__main__':    
    if not os.getcwd().endswith( 'tools' ):         
        sys.exit( "This script should be executed from dir '.../tools/'" )

    from optparse import OptionParser
    
    parser = OptionParser( usage="%prog [options] ant_target" )

    parser.add_option( "--ls", dest="listserv", action="store_true",
                       default=False, help="List available servers" )
    parser.add_option( "--lt", dest="listtarget", action="store_true",
                       default=False, help="List available ant targets" )
    parser.add_option( "-s", type="string", dest="server", 
                       help="Name of server to copy to." )
    parser.add_option( "-d", "--dest", type="string", dest="folder", 
                       help="Name of of destination folder (from ~/) to copy to. Defaults to 'tst'" )

    (options, args) = parser.parse_args()


    # The target list... new targets only needs to be added here, but
    # must comform to this pattern:
    # ant target-name = dist_[target_list-name]
    # ie.
    # target_list-name: datadock, ant target-name: dist_datadock

    target_list = [ 'all', 'datadock', 'pti', 'testindexer' ]
    server_list = [ 'andrus', 'sempu' ]

    if options.listserv:
        print "Available servers to copy to:"
        for server in server_list:
            print server
        sys.exit( 0 )

    if options.listtarget:
        print "Available ant targets:"
        for target in target_list:
            print target
        sys.exit( 0 )
    
    if options.server not in server_list:
        print "Use option -s to specify server. Available servers are:\n"
        print "\n".join( server_list ) + "\n"
        parser.print_help()
        sys.exit()

    if options.folder is None:
        parser.error( "please specify (existing) folder on remote host to copy to.\nUse -h to see available options" )
    #__check_conn( options.server )
    
    if len( args ) != 1:
        parser.print_help()
        sys.exit( "\nPlease specify 1 target" )    
    if args[0] in target_list:
        __make_dist( args[0] )
    else:
        sys.exit( "\nUnknown target "+args[0] )

    fldrs = [ 'admin', 'bin', 'config', 'dist', 'lib', 'plugins', 'tools' ]

    print fldrs

    if not __check_remote_folder_exists( os.path.join( options.folder ), options.server ):
        print( "remote folder %s does not exist. Creating it"% options.folder )
        __create_remote_folder( options.folder, options.server )
        

    if __check_remote_folder_exists( os.path.join( options.folder, "lib" ), options.server ):
        print "folder %s:%s/%s exists, skipping copy of libs"%( options.server, options.folder, "lib" )
        fldrs.remove( "lib" )

    del_dir = __create_copy_dist( fldrs )

    __set_copy_from_folder( del_dir )
    __set_copy_to_server( options.server )
    __set_copy_to_folder( options.folder )

    print """
    Creating jar(s): """ + args[0] + """
    copying folder:  """ + del_dir + """
    to server:       """ + options.server + """
    to remote folder:""" + options.folder

    try:
        __copy_dist()
    except:
        raise Error( "Unable to copy to " + server + " - check connection!" )

     # Delete tmp dir
    os.system( "tree " + del_dir )
    os.system( "rm -fr " + del_dir )

    print 'tmp dir deleted'

