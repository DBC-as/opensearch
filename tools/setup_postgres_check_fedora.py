#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import postgres_setup, fedora_conn
import build_config
import shutil
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

shutil.copytree( '/data1/harvest-test', dest )

postgres_setup.setup()
fedora_conn.test_fedora_conn( 'localhost', '8080' )


