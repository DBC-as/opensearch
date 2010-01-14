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

# python script to take a snapshot of a Solr Lucene collection.

import os
import os.path
import datetime
import subprocess
import ConfigParser

def snapshooter(master_dir, name, check):
    """
    Makes snapshot of index folder located in master_dir, which is
    a folder in master_dir called snapshot.[now] where now is a
    timestamp. this folder contains hardlinks to all files in the
    index folder
    """
    
    now = datetime.datetime.now()
    #print "started %s by %s"%(now.ctime(), os.environ.get( 'USER' ))

    ## generate names
    index = os.path.join(master_dir, name)
    snapname_filename = "snapshot." + now.strftime("%Y%m%d%H%M%S")
    snapname = os.path.join(master_dir, snapname_filename)
    snaptemp = os.path.join(master_dir, "temp-"+snapname_filename);

    if os.path.exists(snapname):
        print "snapshot directory already exists '%s'" % snapname
        exit(1)

    if os.path.exists(snaptemp):
        print "snapshooting of '%s' already in progress" % snapname
        exit(2)

    if check:
        cmd_str = "find %s -name snapshot.\* | sort -r  | head -1"%master_dir
        retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
        if retcode[1]:
            print "to check fedoraAdmin user:\n%s" % retcode[1]
            exit(3)
        if retcode[0] != "":
            cmd_str = "diff -q %s %s | wc -l"%(index, retcode[0].strip())
            print "cmd",cmd_str
            retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
            if retcode[1]:
                print "adsaddsa:\n%s" % retcode[1]
                exit(3)
            if retcode[0].strip() == "0":
                print "index does not contain any new data. The snapshot would be the same as last time, exiting"
                exit(3);
                                    
    print "taking snapshot"

    cmd_str = "cp -lr %s %s" %(index, snaptemp)
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "Caught error while copying:%s" % retcode[1]
        exit(4)

    cmd_str = "mv %s %s" %(snaptemp, snapname)
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "Caught error while copying: %s"% retcode[1]
        exit(5)
        
if __name__ == '__main__':
    from optparse import OptionParser
    
    parser = OptionParser( usage="%prog [options] master index folder " )
    
    parser.add_option( "-c", action="store_true", dest="check", default=False,
                       help="Only take snapshot if different than previous one" )
  
    (options, args) = parser.parse_args()

    config = ConfigParser.RawConfigParser()
    config.read(os.path.join(os.getcwd(),"snap.conf"))

    master_index = "";
    if args:
        if not os.path.exists( args[0] ):
            print "%s doesn't exist, exiting."% args[0]
            exit(1)
        master_index = args[0]
        
    else:
        try:
            config.get("snap-configuration", "master_index" )
        except ConfigParser.NoOptionError:
            print "please supply a master index or set one in snap.conf"
            exit(1)
        master_index = config.get("snap-configuration", "master_index" )

    index_name = ""
    try:
        index_name = config.get("snap-configuration", "master_index_name" )
    except ConfigParser.NoOptionError:
        pass
    if index_name == "":
        index_name = "index"
        
    snapshooter(master_index, index_name, options.check)
    
