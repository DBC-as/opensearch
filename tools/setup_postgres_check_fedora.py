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
import build_config
import shutil
import os.path
try:
    import xml.etree.ElementTree as ET
except ImportError:
    try:
        import elementtree.ElementTree as ET
    except ImportError:
        sys.exit( "could not import elementtree library. Is it installed?" )


build_config.main( [ '', '../' ] )

config = ET.parse( '../config/config.xml' )

dest = config.findall( '//toharvest' )[0].text

if os.path.exists( dest ):
    shutil.rmtree( dest )

shutil.copytree( '/data1/harvest-kanon', dest )

postgres_setup.main()
fedora_conn.test_fedora_conn( 'localhost', '8080' )


