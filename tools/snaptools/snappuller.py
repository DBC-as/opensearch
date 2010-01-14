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

# Python script to copy snapshots of a Solr Lucene collection from the master

import os.path
import subprocess
import socket
import datetime
import ConfigParser

def mkdir_p( newdir ):
     """
     works the way a good mkdir should :)
        - already exists, silently complete
        - regular file in the way, raise an exception
        - parent directory(ies) does not exist, make them as well
     """
     if os.path.isdir(newdir):
          pass
     elif os.path.isfile(newdir):
          raise OSError("a file with the same name as the desired " \
                      "dir, '%s', already exists." % newdir)
     else:
          head, tail = os.path.split(newdir)
          if head and not os.path.isdir(head):
               _mkdir(head)
          if tail:
               os.mkdir(newdir)

def push_status(files, msg):
     """
     writes msg to all files in files
     """
     for filename in files:
          f = open(filename, 'w')
          f.write(msg)
          f.close()

def snappuller(solr_root, master_dir, slave_dir, master_status_dir, slave_status_dir, snapname, verbose):
     """
     Copys a snapshot from master_dir and copys it to local snapshot folder
     on slave
     """
    
     if not os.path.exists( "%s/logs/snappuller-enabled"%solr_root):
          print "snappuller disabled... use snappuller-enable to enable"
          exit(1)

     ## generate status folders if none are specified
     if master_status_dir == "":
          master_status_dir = os.path.join(solr_root, "logs", "master_status")
     if slave_status_dir == "":
          slave_status_dir = os.path.join(solr_root, "logs", "slave_status")
          
     master_status_file = os.path.join(master_status_dir,"snappuller.status")+".%s"%socket.gethostname()
     slave_status_file = os.path.join(slave_status_dir,"snappuller.status")
     ## get newest snapname if none is specified
     if snapname == "":
          cmd_str = "find %s -name snapshot.\* | sort -r  | head -1"%master_dir
          retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
          if retcode[1]:
               print "error caught while trying to find snapshot: %s" % retcode[1]
               exit(3)
          if retcode[0].strip() == "":
               print "no snapshots available"
               exit(4)
     snapname = retcode[0].strip()
    
     if os.path.exists(os.path.join(slave_dir, os.path.split(snapname)[1])) or os.path.exists(os.path.join(slave_dir, os.path.split(snapname)[1]+"-wip")):
          print "No new snapshot is avaiable on master %s"%os.path.join(slave_dir, os.path.split(snapname)[1])
          exit(5)

     # take snapshot of current index so that only modified files will be rsync-ed
     cmd_str = "cp -lr %s %s" %(os.path.join(slave_dir, "index"),os.path.join(slave_dir, os.path.split(snapname)[1]+"-wip"))
     retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
     if retcode[1]:
          print "Caught error trying to take snapshot of current index :%s" % retcode[1]
          sys.exit(4)

     print "pulling snapshot"

     ## push status to slave and master
     mkdir_p(master_status_dir)
     mkdir_p(slave_status_dir)
     start_time = datetime.datetime.now()
     start_status = "rsync of %s started:%s"%(os.path.split(snapname)[1], start_time.strftime("%Y%m%d-%H%M%S"))

     push_status([slave_status_file, master_status_file], start_status)
    
     ## rsync command
     v_str = ""
     stats = ""
     if verbose:
          v_str = "v"
          stats = "--stats"
        
     cmd_str = "rsync -Wa%s %s --delete %s %s"%(v_str, stats, os.path.join(snapname,""), os.path.join(slave_dir, os.path.split(snapname)[1]+"-wip"))

     ## do rsync
     retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
     end_time = datetime.datetime.now()
     if retcode[1]:
          print "rsync failed:%s" % retcode[1] 
          ## rsync failed.. clean up and set status
          failed_status = "%s failed:%s"%(start_status, _time.strftime("%Y%m%d-%H%M%S"))
          push_status([slave_status_file, master_status_file], failed_status)
          sys.exit(5)

     if verbose:
          print retcode[0]

     # move into place atomically
     cmd_str = "mv %s %s"%(os.path.join(slave_dir, os.path.split(snapname)[1]+"-wip"), os.path.join(slave_dir, os.path.split(snapname)[1]))
     retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
     if retcode[1]:
          print "caught error while mv rsynced index:%s" % retcode[1]
          sys.exit(6)

     # push end status
     elapsed = end_time - start_time;
     end_status = "%s ended:%s rsync-elapsed:%s"%(start_status, end_time.strftime("%Y%m%d-%H%M%S"), elapsed.seconds)
     push_status([slave_status_file, master_status_file], end_status)

if __name__ == '__main__':

     from optparse import OptionParser
    
     parser = OptionParser( usage="%prog [options]" )
     
     parser.add_option( "-r", dest="solr_root", 
                        help="the path to the solr installation. Mandatory (Can be set in snap.conf)" )

     parser.add_option( "-m", dest="master_index", 
                        help="the path to the master index folder (the one containing the index folder). Mandatory (Can be set in snap.conf)" )

     parser.add_option( "-s", dest="slave_index", 
                        help="the path to the slave index folder (the one containing the index folder). Mandatory (Can be set in snap.conf)" )

     parser.add_option( "-v", action="store_true", dest="verbose", default=False,
                        help="verbose rsync output" )

     parser.add_option( "--masterstatus", dest="master_status_folder", default="",
                        help="set master status folder, defaults to ${solr_root}/logs/master-status ")

     parser.add_option( "--slavestatus", dest="slave_status_folder", default="",
                        help="set slave status folder, defaults to ${solr_root}/logs/slave-status ")

     parser.add_option( "--snapshot", dest="snapshotname", default="",
                        help="Use this if you want to pull specific snapshot. if not set, newest snapshot is pulled")

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

     master_index = "";
     if not options.master_index:
          try:
               config.get("snap-configuration", "master_index" )
          except ConfigParser.NoOptionError:
               print "please supply master_index or set one in snap.conf"
               exit(2)
          master_index = config.get("snap-configuration", "master_index" )
     else:
          master_index = options.master_index

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
    
     snappuller( solr_root, \
                 master_index, \
                 slave_index, \
                 options.master_status_folder, \
                 options.slave_status_folder, \
                 options.snapshotname, \
                 options.verbose )
    
