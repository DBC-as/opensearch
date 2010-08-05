26#!/usr/bin/env python
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


import os
import sys
import subprocess
import logging as log
import fedora_conn
import glob

def main( app, action, monitor, fedora_arg, harvester, esharvester_cleanup, mem_allocation, force ):
    log_filename = 'app_starter.log'
    log_path = os.path.abspath( 'log-files' )

    if not os.path.isdir( log_path ):
        print "log-path '%s' does exists. Creating folder" % log_path,
        os.mkdir( log_path )
    
    log.basicConfig( level = log.DEBUG,
                     format = '%(asctime)s %(levelname)s %(message)s',
                     filename= os.path.join( log_path, log_filename ) )
    log.getLogger( '' )
    

    #generate config file for the system
    generate_config()

    pid_filename = ""
    pid_location = os.getcwd()
    process      = ""
    q_name       = ""
    new_pid      = ""
    do_start     = False
    do_bench     = False
    
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
        
        if action == "restart" or ( force and ( action == "start" or action == "bench" ) ):
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
            if( os.path.isfile( pid_filename ) ):
                os.unlink( pid_filename )

        elif action == "start":
            print "Only one %s instance is allowed to run at a time (check for pidfile)"% ( app )
        elif action == "bench":
            print "Only one %s instance is allowed to run at a time (check for pidfile)"% ( app )

    elif action == "start":
        do_start = True
    elif action == "restart":
        print "No running process"
        do_start = True
    elif action == "bench":
        do_bench = True
    else:
        sys.exit( "Cannot stop nonrunning process" )

    if do_start:

        if fedora_arg:
        ### Check if fedora is up and running
            print "Checking Fedora %s:%s   "%( fedora_arg['host'], fedora_arg['port'] ),
            ( success, answer ) = fedora_conn.test_fedora_connection( fedora_arg['host'], fedora_arg['port'] )
            if success:
                print "OK"
            else:
                print "FAIL\nMessage: %s\nExiting. "%answer
                sys.exit( 1 )
        
        ### check if jar file is available
        if not os.path.isfile( q_name ):
            print "Could not find jarfile '%s'\nExiting" % q_name
            sys.exit( 1 )    
        
        print "starting process"
        log.debug( "Starting process with q_name=%s, pid_filename=%s"%( q_name, pid_filename ) )
        proc, pid = start_daemon( q_name, pid_filename, monitor, harvester, esharvester_cleanup, mem_allocation )
        log.debug( "Started process with pid=%s"%( pid ) )
        open( pid_filename, 'w' ).write( str( pid ) )
        print "process started with pid=%s"%( pid )

    if do_bench :
        print "starting process"
        args = "-DshutDownOnJobsDone=true"
        
        proc, pid = start_daemon( q_name, pid_filename, monitor, harvester, esharvester_cleanup, mem_allocation, args )
        print "Waiting for process to stop pid=%s "%(pid)
        os.waitpid( pid, 0 );
        
        print "print done waiting"

def start_daemon( q_name, pid_filename, monitor, harvester, esharvester_cleanup, mem_allocation, args=None ):
    
    """
    Starts the Application daemon
    """
    runproc = subprocess.Popen( [ './run' ], shell=True, stdout=subprocess.PIPE ) 
    cp = runproc.communicate()[ 0 ].strip( '\n' )

    properties = []

    monitor_args = ''
    if monitor == 'tijmp':
        monitor_args = "-agentlib:tijmp"
    if monitor == 'jconsole':
        monitor_args = "-Dcom.sun.management.jmxremote.port=8155 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
    if monitor == 'debug':
        monitor_args = "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044"
    if monitor_args:
        properties.append( monitor_args )

    if mem_allocation[0]:
        properties.append( "-Xms%s" % mem_allocation[0] )
    if mem_allocation[1]:
        properties.append( "-Xmx%s" % mem_allocation[1] )

    if harvester:
        properties.append( "-Dharvester=%s" % harvester )

    if args:
        cmd.append(args)

    cmd = [ 'java' ]
    for p in properties:
        cmd.append(p)
    cmd.append('-Ddaemon.pidfile=%s'%( pid_filename ) )
    cmd.append('-jar' )
    cmd.append( q_name )

    printcmd = ' '.join( cmd )
    
    log.debug( "Running process from cmd '%s'"%( printcmd ) )

    print "CMD %s" % cmd

    proc = subprocess.Popen( cmd, shell=False, stdout=subprocess.PIPE )
    log.debug( "Started java process with proc.pid=%s"%( proc.pid ) )

    pid = int(proc.pid)
    
    return ( proc, pid )


def stop_daemon( pid ):

    try:
        os.kill( int( pid ), int( 15 ) )
    except OSError:
        print "could not kill process. removing the pid file"

    #Compass has some problems finishing the optimizer thread, so
    #we'll check if there remains a lock file in the index dir:
    index_dir = os.path.join( "indexes", "index", "opensearch-index", "*lock*" )
    for fil in glob.glob( index_dir ):
        if fil.find( 'lock' ) > 0:
            print "The program still retains a lock on the indexes."
            print "This indicates that the program has not yet"
            print "finished running an optimizer on the indices."
            print "This operation can take more than 10 minutes"
            print "depending on the size of the indices."

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

    available_monitors = ["tijmp", "jconsole", "debug"]
    app_list = [ 'datadock', 'pti', 'both' ]
    fedora_arg = {'host': 'localhost',
                  'port': '8080'
                  }
    actions = ["start", "stop", "restart", "bench"]
        
    # if harvester == "file":
    #     harvester = "FileHarvest"
    # elif harvester == "light":
    #     harvester = "FileHarvestLight"  
    # elif harvester == "es":
    #     harvester = "ESHarvest"

    harvester_list = [ ["file", "FileHarvest"],
                       ["light", "FileHarvestLight"],
                       ["es", "ESHarvest"] ]
        
    parser = OptionParser( usage="%prog [options] " + "|".join( actions ) )

    parser.add_option( "-a", type="string", action="store", dest="app",
                       help="Name of app to execute. Available apps: %s" % ", ".join( app_list ) )
    parser.add_option( "-m", type="string", action="store", dest="monitor",
                       help="monitor the application. available monitors: %s" % ", ".join( available_monitors ) )
    parser.add_option( "-c", dest="checkFedora", action="store_true",
                       default=False, help="Checks whether fedora is up and running" )
    parser.add_option( "--host", type="string", action="store", dest="host",
                       help="The hostname of the fedora repository. ignored without -c (default value: %s)" % fedora_arg['host'] )
    parser.add_option( "--port", type="string", action="store", dest="port",
                       help="The portnumber of the Fedora Repository. ignored without -c (default value: %s)" % fedora_arg['port']  )
    parser.add_option( "--harvester", type="string", action="store", dest="harvester",
                       help="Selects which harvester type to use. Available options: %s" % ", ".join( map( lambda x: x[0], harvester_list ) ) )
    parser.add_option( "--force", action="store_true", dest="force",
                       help="forces a run, ignoring warnings of already running processes" )
    parser.add_option( "--Xms", type="string", action="store", dest="Xms",
                       help="The initial heap size (default 2mb)" )
    
    parser.add_option( "--Xmx", type="string", action="store", dest="Xmx",
                       help="The maximum heap size (default 64mb)" )

    parser.add_option( "--esharvester_clean_inprogress", action="store_true", dest="esharvester_cleanup",
                       help="resets records in database which are set to recordstatus=inProgress. The recordsstatus are reset to 'queued'." )

    (options, args) = parser.parse_args()

    mem_allocation = [options.Xms, options.Xmx]


    if len( args ) == 0:
        print "\nPlease supply some arguments!\n"
        sys.exit( parser.print_help() )
    if options.port:
        fedora_arg['port'] = options.port
    if options.host:
        fedora_arg['host'] = options.host
    if not options.checkFedora:
        fedora_arg = None
        
    if not args[0] in actions:
        print "Action '%s' not supported. Use one of the following: %s" % ( args[0], "|".join( actions ) )
        sys.exit( parser.print_help() )

    if options.monitor and not options.monitor in available_monitors:
        print "Available monitors:\n"
        print '\n'.join( available_monitors )
        sys.exit(2)
    if not options.monitor:
        options.monitor = ''

    harvester = None 
    if options.harvester:
        for known_harvester in harvester_list:
            if options.harvester == known_harvester[0]:
                harvester = known_harvester[1]
        if not harvester:
            print "unknown harvester '%s'"% options.harvester
            print "Available harvesters", ", ".join( map( lambda x: x[0], harvester_list ) )
            sys.exit(2)
            
    if options.app not in app_list:
        parser.print_help()
        sys.exit( "Can only start one of: %s"%( ', '.join( app_list ) ) )

    if len( args ) != 1:
        sys.exit( parser.print_help() )

    if options.app == 'both':
        main( 'pti', args[0], options.monitor, fedora_arg, options.harvester, options.esharvester_cleanup )
        main( 'datadock', args[0], options.monitor, fedora_arg, options.harvester, options.esharvester_cleanup )
    else:
        main( options.app, args[0], options.monitor, fedora_arg, harvester, options.esharvester_cleanup, mem_allocation, options.force )
