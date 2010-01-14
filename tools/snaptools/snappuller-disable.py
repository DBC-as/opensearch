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

# Python script to enable snappuller

import os.path
import ConfigParser

def snappuller_disable( solr_root ):
    """
    disables snappulling by removing a file called snappuller-enabled in
    ${solr_root}/logs
    """
    enable_file = os.path.join(solr_root, "logs", "snappuller-enabled")
    if os.path.exists( enable_file ):
        os.remove( enable_file )
        print "snappuller disabled"
    else:
        print "snappuller already disabled"
        
if __name__ == '__main__':

    from optparse import OptionParser    
    parser = OptionParser( usage="%prog [options]" )

    parser.add_option( "-r", dest="solr_root", 
                       help="the path to the solr installation. Mandatory" )

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

    snappuller_disable( solr_root )
