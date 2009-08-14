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


import postgres_setup, fedora_conn
import shutil
import os.path
import sys
sys.path.append( "../tools/" )
import build_config


try:
    import xml.etree.ElementTree as ET
except ImportError:
    try:
        import elementtree.ElementTree as ET
    except ImportError:
        sys.exit( "could not import elementtree library. Is it installed?" )

def main( harvest_folder, delete_existing ):
    
    build_config.main( [ '', '../' ] )

    config = ET.parse( '../config/config.xml' )

    dest = config.findall( '//toharvest' )[0].text

    if delete_existing:
        if os.path.exists( dest ):
            shutil.rmtree( dest )

    if harvest_folder != "":
        shutil.copytree( harvest_folder, dest )

    postgres_setup.main()
    fedora_conn.test_fedora_conn( 'localhost', '8080' )

if __name__ == '__main__':

    from optparse import OptionParser
    
    parser = OptionParser( usage="%prog [options]" )
    
    parser.add_option( "-f", dest="harvest_folder", 
                       action="store", help="The full path to the folder containing the data to be harvested" )
    parser.add_option( "--delete", action="store_true", dest="DEL", default="False",
                       help="does not delete the folder containing the local harvest files (if -f was given, the folder will be deleted)" )
    parser.add_option( "--use-defaults", action="store_true", dest="DEFAULT", 
                       help="use /data1/harvest-test as default folder (for backwards compatibility...)")
    (options, args) = parser.parse_args()

    harvest_folder = ""

    if options.DEFAULT:
    	harvest_folder = '/data1/harvest-test' 
    if options.harvest_folder:
        harvest_folder = options.harvest_folder


    main( harvest_folder, options.DEL )
