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

# Python script to clean up snapshots of a Solr Lucene collection.

import os.path
import subprocess
import shutil
import sys

def snapcleaner(num_of_shots, index_folder):
    """
    function to clean up snapshots of a Solr Lucene collection.
    """

    snapfiles = filter(lambda x: "snapshot." in x and True or False, os.listdir(index_folder))
    snapfiles.sort()
    old_snapfiles = snapfiles[:len(snapfiles)-num_of_shots]

    for removable_file in old_snapfiles:
        
        cmd_str = "ps -www -U %s |grep -w rsync|grep -v grep|grep -w %s"%(os.environ.get( 'USER' ), removable_file)
        retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
        if retcode[1]:
            print "caught error while checking if snapshot was syncing: %s" % retcode[1]
            sys.exit(1)

        if retcode[0]:
            print "cant remove %s, syncing in progress"%removable_file
        else:
            print "removing snapshot %s"%removable_file
            shutil.rmtree(os.path.join(index_folder, removable_file)) 
    
if __name__ == '__main__':
    from optparse import OptionParser
    
    parser = OptionParser( usage="%prog [options] index folder " )
    
    parser.add_option( "-n", type="int", dest="number", default=1,
                       help="leave the newest n snapshots. default to one" )
  
    (options, args) = parser.parse_args()
        
    if not args:
        print "please supply an index folder"
        exit(1)
    if not os.path.exists( args[0] ):
        print "%s doesn't exist, exiting."% args[0]
        exit(1)

    snapcleaner(options.number, args[0])
