#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-


import subprocess, sys, os, fnmatch
import datetime, os, shutil, sys


files = [ 'dist_copy.py', 'xsd2xml.py' ]
fldrs = [ 'admin', 'bin', 'config', 'dist', 'lib', 'tools' ]
trees = [ 'Harvest', 'build' ]


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


def __cp_fldr( src, dst, filename ):
    if not os.path.exists( dst ):
        os.mkdir( dst )

    files = os.listdir( src )
    for f in files:
        if __test_file( f ):
            shutil.copyfile( src + f, dst + f )

def __cp_tree( src, dst, folder ):
    shutil.copytree( src, dst, symlinks=False )


def __create_copy_dist():
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
            __cp_fldr( src, dst, name )
        elif name in trees:
            __cp_tree( src, dst, name )
            
    return suffix


""" #########################################################
    Functions for setting server and copying folder to server
    #########################################################"""
src_dir = os.getcwd()

server = 'sempu'
middle = ':./'
folder = 'tst'

ant_dist = "ant dist"
ant_pti  = "ant dist_pti"
ant_data = "ant dist_datadock"
scp_dist = "scp -r "


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
    global ant_dist
    global scp_dist

    if src_dir.endswith("tools"):
        src_dir = src_dir.replace("/tools", "" )
        
    if dist == 'datadock':
        runproc = subprocess.Popen( ant_data, shell=True, cwd=src_dir )
    elif dist == 'pti':
        runproc = subprocess.Popen( ant_pti,  shell=True, cwd=src_dir )    
    else:
        runproc = subprocess.Popen( ant_dist, shell=True, cwd=src_dir )

    runproc.communicate()[ 0 ]


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
        

if __name__ == '__main__':    
    if not os.getcwd().endswith( 'tools' ):         
        sys.exit( "This script should be executed from dir '.../tools/'" )

    from optparse import OptionParser
    
    parser = OptionParser( usage="%prog [options] datadock|pti|both" )

    parser.add_option( "-l", dest="listjars", action="store_true",
                       default=False, help="List available " )
    parser.add_option( "-s", type="string", dest="server", 
                       help="Name of server to copy to." )
   
    (options, args) = parser.parse_args()

    target_list = [ 'datadock', 'pti', 'both' ]
    server_list = [ 'andrus', 'sempu' ]
    
    if options.server not in server_list:
        print "Use option -s to specify server. Available servers are:\n"
        print "\n".join( server_list ) + "\n"
        parser.print_help()
        sys.exit()

    __check_conn( options.server )
    
    if len( args ) != 1:
        parser.print_help()
        sys.exit( "\nPlease specify target" )
        
    __make_dist( args[0] )

    del_dir = __create_copy_dist()
    __set_copy_from_folder( del_dir )
    __set_copy_to_server( options.server )
    __set_copy_to_folder( folder )

    print """
Creating jar(s): """ + args[0] + """
copying folder:  """ + folder + """
to server:       """ + server

    __copy_dist()

     # Delete tmp dir
    os.system( "tree " + del_dir )
    #os.system( "rm -fr " + del_dir )

    print 'tmp dir deleted'

