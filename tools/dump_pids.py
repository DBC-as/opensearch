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

from sets import Set
import sys
import re


def read_pids():
    """
    Read pids from stdin, and returns a list of
    unique pids
    """
    pid_regex = re.compile( '(?<=\'info:fedora/)[a-zA-Z0-9]+:[0-9]+(?=\')' )
    pids = [] 
    while 1:
        line = sys.stdin.readline().strip()
        if not line:
            break
        result = pid_regex.search( line )
        print line
        if result:
            pid = result.group()
            pids.append( pid )
    return list(Set(pids))


def dump_pids( filename, pids ):
    """
    Dumps PIDS to FILE, one pid pr line
    """
    file = open(filename, 'w')
    for pid in pids:
        file.write( pid+"\n" )
    file.close()


def main( filename ):
    """
    read pids from stdin and dump them into FILENAME
    """
    pids = read_pids()
    dump_pids( filename, pids )
    print "dumped %s pids to file: %s" % ( len(pids) filename )

    
if __name__ == '__main__':    
    from optparse import OptionParser
    parser = OptionParser( usage="%prog [options]\n Reads lines from stdin, identifies pids, and dump them into a file." )

    parser.add_option( "-o", type="string", action="store", dest="filename",
                       help="The file to dump pids into (defaults to pids.out)")

    (options, args) = parser.parse_args()

    filename = "pids.out"
    if options.filename:
       filename = options.filename 
    
    main( filename )
