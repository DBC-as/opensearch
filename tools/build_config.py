#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import sys

try:
    import xml.etree.ElementTree as ET
except ImportError:
    try:
        import elementtree.ElementTree as ET
    except ImportError:
        sys.exit( "could not import elementtree library. Is it installed?" )

import os
from time import strftime, gmtime

# this will probably not work for cruisecontrol user
usern = os.environ.get( 'USER' )

if len(sys.argv) != 2:#"tools" in os.listdir( path ):
    sys.exit( "please specify location of the trunk as argument to the program") #run this program from the root of the trunk" )
else:
    path = sys.argv[1]
    path = os.path.abspath( path )

#should check that path stat's

#pluginpath = os.path.join( path, "build/classes/dk/dbc/opensearch/plugins" )
pluginpath = os.path.join( path, "plugins" )
print pluginpath

root   = ET.Element( "opensearch-definition" )

now = strftime("%d/%m/%y %H:%M:%S", gmtime() )
comment = ET.Comment( "This file was autogenerated by tools/build_config.py on %s. All edits to this file will be overwritten on next build"%( now ) )
root.append( comment )

db     = ET.SubElement( root, "database" )
dd     = ET.SubElement( root, "datadock" )
fedora = ET.SubElement( root, "fedora" )
filest = ET.SubElement( root, "filesystem" )
harvest= ET.SubElement( root, "harvester" )
pidmng = ET.SubElement( root, "pidmanager" )
pti    = ET.SubElement( root, "pti" )

# database settings
driver = ET.SubElement( db, "driver" )
url    = ET.SubElement( db, "url" )
user   = ET.SubElement( db, "userID" )
passwd = ET.SubElement( db, "passwd" )
driver.text = "org.postgresql.Driver"
url.text    = "jdbc:postgresql:lvh"
user.text   = usern
passwd.text = usern

# datadock settings
poll      = ET.SubElement( dd, "main-poll-time" )
reject    = ET.SubElement( dd, "rejected-sleep-time" )
shutdown  = ET.SubElement( dd, "shutdown-poll-time" )
queuesz   = ET.SubElement( dd, "queuesize" )
corepool  = ET.SubElement( dd, "corepoolsize" )
maxpool   = ET.SubElement( dd, "maxpoolsize" )
keepalive = ET.SubElement( dd, "keepalivetime" )

poll.text      = "1000" 
reject.text    = "3000"
shutdown.text  = "1000"
queuesz.text   = "1"
corepool.text  = "1"
maxpool.text   = "1"
keepalive.text = "10"

#fedora settings
host   = ET.SubElement( fedora, "host" )
port   = ET.SubElement( fedora, "port" )
user   = ET.SubElement( fedora, "user" )
passwd = ET.SubElement( fedora, "passphrase" )
host.text   = "localhost"
port.text   = "8080"
user.text   = "fedoraAdmin"
passwd.text = "fedoraAdmin"

#filesystem settings
# filest_comment = ET.Comment( "all elements are relative to trunk")
# filest.append( filest_comment )
trunk   = ET.SubElement( filest, "trunk" )
plugins = ET.SubElement( filest, "plugins" )
datadock = ET.SubElement( filest, "datadock" )
pti_el   = ET.SubElement( filest, "pti" )
cpm_xml  = ET.SubElement( filest, "cpm" )
trunk.text   = path
plugins.text = pluginpath
datadock.text = os.path.join( path, "config/datadock_jobs.xml" )
pti_el.text   = os.path.join( path, "config/pti_jobs.xml" )
cpm_xml.text = os.path.join( path, "config/xml.cpm.xml" )

#harvester settings
folder = ET.SubElement( harvest, "folder" )
folder.text = os.path.join( path, "Harvest/pollTest" )

#pidmanager settings
num_of_pids = ET.SubElement( pidmng, "num-of-pids-to-retrieve" )
num_of_pids.text = "10"

#pti settings
poll      = ET.SubElement( pti, "main-poll-time" )
reject    = ET.SubElement( pti, "rejected-sleep-time" )
shutdown  = ET.SubElement( pti, "shutdown-poll-time" )
resultsz  = ET.SubElement( pti, "queue-resultset-maxsize" )
queuesz   = ET.SubElement( pti, "queuesize" )
corepool  = ET.SubElement( pti, "corepoolsize" )
maxpool   = ET.SubElement( pti, "maxpoolsize" )
keepalive = ET.SubElement( pti, "keepalivetime" )

poll.text      = "1000" 
reject.text    = "3000"
shutdown.text  = "1000"
resultsz.text  = "20"
queuesz.text   = "1"
corepool.text  = "1"
maxpool.text   = "1"
keepalive.text = "1"


import sys

f = open( os.path.join( path, "config", "config.xml" ), "w" )
f.write( ET.tostring( root, "UTF-8" ) )


