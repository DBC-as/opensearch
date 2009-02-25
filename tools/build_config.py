#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import xml.etree.ElementTree as ET
import os

# this will probably not work for cruisecontrol user
user = os.environ.get( 'USER' )

# should set path to trunk
path = os.path.abspath( ".." )

pluginpath = "build/classes/dk/dbc/opensearch/plugins"

root   = ET.Element( "opensearch-definition" )
db     = ET.SubElement( root, "database" )
dd     = ET.SubElement( root, "datadock" )
fedora = ET.SubElement( root, "fedora" )
filest = ET.SubElement( root, "filesystem" )
harvest= ET.SubElement( root, "harvester" )
pidmng = ET.SubElement( root, "pidmanager" )
plugin = ET.SubElement( root, "plugins" )
pti    = ET.SubElement( root, "pti" )

# database settings
driver = ET.SubElement( db, "driver" )
url    = ET.SubElement( db, "url" )
user   = ET.SubElement( db, "userID" )
passwd = ET.SubElement( db, "passwd" )
driver.text = "org.postgresql.Driver"
url.text    = "jdbc:postgresql:opensearch"
user.text   = user
passwd.text = user

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
queuesz.text   = "20"
corepool.text  = "3"
maxpool.text   = "6"
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
trunk   = ET.SubElement( filest, "trunk" )
plugins = ET.SubElement( filest, "plugins" )
trunk.text   = path
plugins.text = os.path.join( path, pluginpath )

#harvester settings
folder = ET.SubElement( harvest, "folder" )
folder.text = os.path.join( path, "Harvest/pollTest/" )

#pidmanager settings
num_of_pids = ET.SubElement( pidmng, "num-of-pids-to-retrieve" )
num_of_pids.text = "10"

#plugins settings
datadock = ET.SubElement( plugin, "datadock" )
pti_el   = ET.SubElement( plugin, "pti" )
datadock.text = os.path.join( path, "config/datadock_jobs.xml" )
pti_el.text      = os.path.join( path, "config/pti_jobs.xml" )

#pti settings
poll      = ET.SubElement( pti, "main-poll-time" )
reject    = ET.SubElement( pti, "rejected-sleep-time" )
shutdown  = ET.SubElement( pti, "shutdown-poll-time" )
queuesz   = ET.SubElement( pti, "queuesize" )
corepool  = ET.SubElement( pti, "corepoolsize" )
maxpool   = ET.SubElement( pti, "maxpoolsize" )
keepalive = ET.SubElement( pti, "keepalivetime" )

poll.text      = "1000" 
reject.text    = "3000"
shutdown.text  = "1000"
queuesz.text   = "20"
corepool.text  = "3"
maxpool.text   = "6"
keepalive.text = "10"


import sys
sys.stdout.write( ET.tostring( root, "UTF-8" ) )
