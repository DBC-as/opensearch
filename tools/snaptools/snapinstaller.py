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

import os.path
import subprocess
import shutil
import commit
import socket
import ConfigParser

# Python script to install a snapshot into place as the Lucene collection
# for a Solr server

def push_status(files, msg):
    """
    writes msg to all files in files
    """
    for filename in files:
        f = open(filename, 'w')
        f.write(msg)
        f.close()

def snap_install( solr_root , slave_dir, solr_hostname, solr_port, solr_webapp, master_status_dir, slave_status_dir ):
    """
    Function used to install a
    snapshot into place, and commit
    changes to solr instance
    """
    
    ## generate status folders if none are specified
    if master_status_dir == "":
        master_status_dir = os.path.join(solr_root, "logs", "master_status")
    if slave_status_dir == "":
        slave_status_dir = os.path.join(solr_root, "logs", "slave_status")

    master_status_file = os.path.join(master_status_dir,"snappuller.status")+".%s"%socket.gethostname()
    slave_status_file = os.path.join(slave_status_dir,"snappuller.status")
    slave_snapshot_current_file = os.path.join(solr_root, "logs","snapshot.current")
    master_snapshot_current_file = os.path.join(master_status_dir, "snapshot.current")
    
    lockdir = os.path.join(solr_root, "logs", "snapinstaller-lock")
    pidfile = os.path.join(lockdir, "PID")
    
    # check old pidfile
    if os.path.exists(pidfile):
        other_pid = open(pidfile).read().strip()
        cmd_str = "kill -0 %s &>/dev/null"%(other_pid)
        retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
        if retcode[1]:
            print "process %s not active, removing stale PIDFILE: %s"%(other_pid ,pidfile)
            os.remove(pidfile)
        else:
            print "Lock failed PID '%s' is active"%other_pid
            exit(3)

    # write new pidfile
    if not os.path.exists(lockdir):
        os.mkdir(lockdir)
    pf = open(pidfile, 'w')
    pf.write(str(os.getpid()));
    pf.close();

    # get snapshot filename
    cmd_str = "find %s -name snapshot.\* | sort -r  | head -1"%slave_dir
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "Caught error during snapshot check: '%s'" % retcode[1]
        exit(3)
    if retcode[0].strip() == "":
        print "no snapshots available"
        exit(4)

    # has snapshot already been installed
    last_snapname = retcode[0].strip()
    if os.path.exists(slave_snapshot_current_file):
        current = open(slave_snapshot_current_file).read().strip()
        print "DEBUG last_snapname '%s', current '%s'"%(last_snapname, current)
        if os.path.split(last_snapname)[1] in current:
            print "latest snapshot %s already installed"%last_snapname
            exit(5);

    # install using hard links into temporary directory
    # remove original index and then atomically copy new one into place
    cmd_str = "cp -lr %s/ %s/index.tmp%s  && rm -rf %s/index && mv -f %s/index.tmp%s %s/index "% \
              (last_snapname, slave_dir, os.getpid(), slave_dir, slave_dir, os.getpid(), slave_dir )
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "Caught error during snapshot copy: '%s'" % retcode[1]
        exit(3)

    # update stats
    push_status([slave_snapshot_current_file, master_snapshot_current_file], last_snapname)
    
    # notify Solr to open a new Searcher
    commit.commit(solr_hostname, solr_port, solr_webapp)    

    shutil.rmtree(lockdir)

if __name__ == '__main__':
    default_solr_hostname = "localhost"
    default_solr_port = 8983
    default_solr_webapp = "solr"

    from optparse import OptionParser
    
    parser = OptionParser( usage="%prog [options]" )
    
    parser.add_option( "-n", dest="hostname",
                       help="Hostname of the solr server to commit to. defaults to '%s'"%default_solr_hostname )
    parser.add_option( "-p", dest="port", type="int",
                       help="Port of the solr server to commit to. defaults to '%s'"%default_solr_port )
    parser.add_option( "-w", dest="webapp",
                       help="Webapp name of the solr server to commit to. defaults to '%s'"%default_solr_webapp )
    parser.add_option( "-r", dest="solr_root", 
                       help="the path to the solr installation. Mandatory" )
    parser.add_option( "-s", dest="slave_index", 
                       help="the path to the slave index folder (the one containing the index folder). Mandatory" )

    parser.add_option( "--masterstatus", dest="master_status_folder", default="",
                       help="set master status folder, defaults to ${solr_root}/logs/master-status ")
    parser.add_option( "--slavestatus", dest="slave_status_folder", default="",
                       help="set slave status folder, defaults to ${solr_root}/logs/slave-status ")
    
    (options, args) = parser.parse_args()

    config = ConfigParser.RawConfigParser()
    config.read(os.path.join(os.getcwd(),"snap.conf"))

    solr_root = "";
    if not options.solr_root:
        try:
            config.get("snap-configuration", "solr_root" )
        except ConfigParser.NoOptionError:
            print "please supply solr_root or set one in snap.conf"
            exit(1)
        solr_root = config.get("snap-configuration", "solr_root" )
    else:
        solr_root = options.solr_root

    slave_index = "";
    if not options.slave_index:
        try:
            config.get("snap-configuration", "slave_index" )
        except ConfigParser.NoOptionError:
            print "please supply slave_index or set one in snap.conf"
            exit(2)
        slave_index = config.get("snap-configuration", "slave_index" )
    else:
        slave_index = options.slave_index
        
    hostname = default_solr_hostname
    if options.hostname:
        hostname = options.hostname
    else:
        try:
            config.get("snap-configuration", "solr_host" )
        except ConfigParser.NoOptionError:    
            pass
        if config.get("snap-configuration", "solr_host" ) != "":
            hostname = config.get("snap-configuration", "solr_host" )
            
    port = default_solr_port
    if options.port:
        port = options.port
    else:
        try:
            config.get("snap-configuration", "solr_port" )
        except ConfigParser.NoOptionError:    
            pass
        if config.get("snap-configuration", "solr_port" ) != "":
            port = config.get("snap-configuration", "solr_port" )
            
    webapp = default_solr_webapp
    if options.webapp:
        webapp = options.webapp
    else:
        try:
            config.get("snap-configuration", "solr_webapp" )
        except ConfigParser.NoOptionError:    
            pass
        if config.get("snap-configuration", "solr_webapp" ) != "":
            webapp = config.get("snap-configuration", "solr_webapp" )

    snap_install( solr_root, \
                  slave_index, \
                  hostname, \
                  port, \
                  webapp, \
                  options.master_status_folder, \
                  options.slave_status_folder )
