#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import os
import sys
import subprocess


def main( app, action ):

    pid_filename = ""
    pid_location = os.getcwd()
    process      = ""
    q_name       = ""
    new_pid      = ""
    do_start     = False
    
    if app == "pti":
        pid_filename = "ptiDaemon.pid"
        q_name       = "dk.dbc.opensearch.components.pti.PTIMain"
        
    elif app == "datadock":
        pid_filename = 'datadockDaemon.pid'
        q_name       = "dk.dbc.opensearch.components.datadock.DatadockMain"
    
    pid_file = os.path.join( pid_location, pid_filename )

    if ( get_pid( pid_file ) != "" ):
        pid = get_pid( pid_file )
        if action == "restart":
            print "stopping process with pid %s"%( pid )
            stop_daemon( pid )
            os.unlink( pid_filename )
            do_start = True
        elif action == "stop":
            print "stopping process with pid %s"%( pid )
            stop_daemon( pid )
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
        proc, pid = start_daemon( q_name, pid_filename )
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
            '-cp',
            cp,
            q_name ]

    cmd = ' '.join( cmd )

    proc = subprocess.Popen( cmd, shell=True, stdout=subprocess.PIPE )
    return ( proc, proc.pid )


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

