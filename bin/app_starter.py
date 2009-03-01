#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import os
import sys
import subprocess
import logging as log


def main( app, action ):
    log.basicConfig( level = log.DEBUG,
                     format = '%(asctime)s %(levelname)s %(message)s',
                     filename='app_starter.log' )
    log.getLogger( '' )
    

    #generate config file for the system
    generate_config()

    pid_filename = ""
    pid_location = os.getcwd()
    process      = ""
    q_name       = ""
    new_pid      = ""
    do_start     = False
    
    if app == "pti":
        pid_filename = "ptiDaemon.pid"
        q_name       = "../dist/OpenSearch_PTI.jar"
        #q_name       = "dk.dbc.opensearch.components.pti.PTIMain"
       
    elif app == "datadock":
        pid_filename = "datadockDaemon.pid"
        q_name       = "../dist/OpenSearch_DATADOCK.jar"

    pid_file = os.path.join( pid_location, pid_filename )

    if ( get_pid( pid_file ) != "" ):
        pid = get_pid( pid_file )

        log.debug( "read pid=%s"%pid )
        
        if action == "restart":
            log.debug( "Restarting. Killing process with pid=%s"%( pid ) )
            print "stopping process with pid %s"%( pid )
            stop_daemon( pid )
            log.debug( "Removing pid-file %s"%pid_filename )
            os.unlink( pid_filename )
            do_start = True
            log.debug( "Setting do_start=%s"%( do_start ) )
        elif action == "stop":
            log.debug( "Stopping. Killing process with pid=%s"%( pid ) )
            print "stopping process with pid %s"%( pid )
            stop_daemon( pid )
            log.debug( "Removing pid_filename=%s"%( pid_filename ) )
            os.unlink( pid_filename )
        elif action == "start":
            print "Only one %s instance is allowed to run at a time"% ( app )
    elif action == "start":
        do_start = True
    elif action == "restart":
        print "No running process"
        do_start = True
    else:
        sys.exit( "Cannot stop nonrunning process" )

    if do_start:
        print "starting process"
        log.debug( "Starting process with q_name=%s, pid_filename=%s"%( q_name, pid_filename ) )
        proc, pid = start_daemon( q_name, pid_filename )
        log.debug( "Started process with pid=%s"%( pid ) )
        open( pid_filename, 'w' ).write( str( pid ) )
        print "process started with pid=%s"%( pid )

        
        
def start_daemon( q_name, pid_filename ):
    """
    Starts the Application daemon
    """
    runproc = subprocess.Popen( [ './run' ], shell=True, stdout=subprocess.PIPE ) 
    cp = runproc.communicate()[ 0 ].strip( '\n' )

    cmd = [ 'java',
            '-Ddaemon.pidfile=%s'%( pid_filename ),
            '-jar',
            q_name ]
            
    cmd = ' '.join( cmd )
        
    print cmd
    
    log.debug( "Running process from cmd '%s'"%( cmd ) )

    proc = subprocess.Popen( cmd, shell=True, stdout=subprocess.PIPE )
    log.debug( "Started java process with proc.pid=%s"%( proc.pid ) )

    pid = int(proc.pid)+1

    #hackety-hack
    return ( proc, pid )


def stop_daemon( pid ):

    try:
        os.kill( int( pid ), int( 15 ) )
    except OSError:
        print "could not kill process. removing the pid file"


def get_pid( pid_file ):
    """ Tries to locate the pid_file and returns the pid if the file can be read
    """
    if( os.path.exists( pid_file ) ):
        pid = ""
        try:
            pid = open( pid_file ).read()
        except IOError, ioe:
            sys.exit( "Daemon seems to be running, "\
                      "but I cannot read the pid file at %s: %s"\
                      %( pid_file, ioe ) )
        return pid
    else:
        return ""

def generate_config():
    """Generates a new config file in the config folder using
    tools/build_config.py
    """
    
    runproc = subprocess.Popen( [ 'python ../tools/build_config.py ../' ], shell=True, stdout=subprocess.PIPE ) 
    res = runproc.communicate()[ 0 ].strip( '\n' )
    print res
    

if __name__ == '__main__':
    from optparse import OptionParser
    parser = OptionParser( usage="%prog [options] start|stop|restart" )

    parser.add_option( "-a", type="string", action="store", dest="app",
                       help="Name of app to execute")
    parser.add_option( "-l", dest="listapps", action="store_true",
                       default=False, help="List available apps" )

    (options, args) = parser.parse_args()

    app_list = [ 'datadock', 'pti' ]

    if options.listapps:
        print "Available applications:\n"
        print '\n'.join( app_list )
        sys.exit()

    if options.app not in app_list:
        parser.print_help()
        sys.exit( "Can only start one of: %s"%( ', '.join( app_list ) ) )

    if len( args ) != 1:
        sys.exit( parser.print_help() )

    main( options.app, args[0] )

