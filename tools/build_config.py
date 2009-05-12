#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import sys
import ConfigParser

try:
    import xml.etree.ElementTree as ET
except ImportError:
    try:
        import elementtree.ElementTree as ET
    except ImportError:
        sys.exit( "could not import elementtree library. Is it installed?" )

import os
from time import strftime, gmtime


def check_trunk( args ):
    path = str()
    if len( args ) != 2:#"tools" in os.listdir( path ):
        sys.exit( "please specify location of the trunk as argument to the program") #run this program from the root of the trunk" )
    else:
        path = args[1]
        path = os.path.abspath( path )
        
    #should check that path stat's
    return path


def build_config( path ):
    # this will probably not work for cruisecontrol user
    usern = os.environ.get( 'USER' )

    #pluginpath = os.path.join( path, "build/classes/dk/dbc/opensearch/plugins" )
    pluginpath = os.path.join( path, "plugins" )

    root   = ET.Element( "opensearch-definition" )

    now = strftime("%d/%m/%y %H:%M:%S", gmtime() )
    comment = ET.Comment( "This file was autogenerated by tools/build_config.py on %s. All edits to this file will be overwritten on next build"%( now ) )
    root.append( comment )

    config_txt = ConfigParser.RawConfigParser()
    config_txt.read( path + '/config/config.txt' )

    compass= ET.SubElement( root, "compass" )
    db     = ET.SubElement( root, "database" )
    dd     = ET.SubElement( root, "datadock" )
    fedora = ET.SubElement( root, "fedora" )
    filest = ET.SubElement( root, "filesystem" )
    harvest= ET.SubElement( root, "harvester" )
    pidmng = ET.SubElement( root, "pidmanager" )
    pti    = ET.SubElement( root, "pti" )

    # compass settings
    configpath = ET.SubElement( compass, "configpath" )
    xsempath   = ET.SubElement( compass, "xsempath" )
    compass_section = "compass"
    configpath.text = path + "/config/" + config_txt.get( compass_section, "configpath" )
    xsempath.text   = path + "/config/" + config_txt.get( compass_section, "xsempath" )

    # database settings
    driver = ET.SubElement( db, "driver" )
    url    = ET.SubElement( db, "url" )
    user   = ET.SubElement( db, "userID" )
    passwd = ET.SubElement( db, "passwd" )
    database_section = "database"
    driver.text = config_txt.get( database_section, "driver" )
    url.text    = config_txt.get( database_section, "url" ) + usern 
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
    joblimit  = ET.SubElement( dd, "joblimit" )
    datadock  = ET.SubElement( dd, "path" )
    datadock_section = "datadock" 
    poll.text      = config_txt.get( datadock_section, "main_poll_time" )
    reject.text    = config_txt.get( datadock_section, "rejected_sleep_time" )
    shutdown.text  = config_txt.get( datadock_section, "shutdown_poll_time" )
    queuesz.text   = config_txt.get( datadock_section, "queue_size" )
    corepool.text  = config_txt.get( datadock_section, "core_poll_size" )
    maxpool.text   = config_txt.get( datadock_section, "max_poll_size" )
    keepalive.text = config_txt.get( datadock_section, "keep_alive_time" )
    joblimit.text  = config_txt.get( datadock_section, "job_limit" )
    datadock.text  = path + "/" + config_txt.get( datadock_section, "path" )

    #fedora settings
    host   = ET.SubElement( fedora, "host" )
    port   = ET.SubElement( fedora, "port" )
    user   = ET.SubElement( fedora, "user" )
    passwd = ET.SubElement( fedora, "passphrase" )
    fedora_section = "fedora"
    host.text   = config_txt.get( fedora_section, "host" )
    port.text   = config_txt.get( fedora_section, "port" )
    user.text   = config_txt.get( fedora_section, "user" )
    passwd.text = config_txt.get( fedora_section, "passphrase" )

    #filesystem settings
    trunk   = ET.SubElement( filest, "trunk" )
    plugins = ET.SubElement( filest, "plugins" )
    xsd     = ET.SubElement( filest, "jobsxsd" )
    trunk.text   = path
    plugins.text = pluginpath
    xsd.text     = os.path.join( path, "config/jobs.xsd" )

    #harvester settings
    toharvestfolder   = ET.SubElement( harvest, "toharvest" )
    harvestdonefolder = ET.SubElement( harvest, "harvestdone" )
    maxtoharvest      = ET.SubElement( harvest, "maxtoharvest" )
    harvest_section        = "harvester"
    toharvestfolder.text   = path + '/' + config_txt.get( harvest_section, "toharvest" )
    harvestdonefolder.text = path + '/' + config_txt.get( harvest_section, "harvestdone" )
    maxtoharvest.text      = config_txt.get( harvest_section, "maxtoharvest" )

    #pidmanager settings
    num_of_pids = ET.SubElement( pidmng, "num-of-pids-to-retrieve" )
    pidmanager_section = "pidmanager"
    num_of_pids.text = config_txt.get( pidmanager_section, "num_of_pids" )

    #pti settings
    poll      = ET.SubElement( pti, "main-poll-time" )
    reject    = ET.SubElement( pti, "rejected-sleep-time" )
    shutdown  = ET.SubElement( pti, "shutdown-poll-time" )
    resultsz  = ET.SubElement( pti, "queue-resultset-maxsize" )
    queuesz   = ET.SubElement( pti, "queuesize" )
    corepool  = ET.SubElement( pti, "corepoolsize" )
    maxpool   = ET.SubElement( pti, "maxpoolsize" )
    keepalive = ET.SubElement( pti, "keepalivetime" )
    pti_el    = ET.SubElement( pti, "path" )
    pti_section = "pti"
    poll.text      = config_txt.get( pti_section, "main_poll_time" )
    reject.text    = config_txt.get( pti_section, "rejected_sleep_time" )
    shutdown.text  = config_txt.get( pti_section, "shutdown_poll_time" )
    resultsz.text  = config_txt.get( pti_section, "queue_resultset_maxsize" )
    queuesz.text   = config_txt.get( pti_section, "queue_size" )
    corepool.text  = config_txt.get( pti_section, "core_poll_size" )
    maxpool.text   = config_txt.get( pti_section, "max_poll_size" )
    keepalive.text = config_txt.get( pti_section, "keep_alive_time" )
    pti_el.text    = path + "/" + config_txt.get( pti_section, "path" )

    return root


def write_config( path, root ):
    f = open( os.path.join( path, "config", "config.xml" ), "w" )
    f.write( ET.tostring( root, "UTF-8" ) )


def main( path_to_trunk ):
    path = check_trunk( path_to_trunk )
    root = build_config( path )
    write_config( path, root )


if __name__ == '__main__':
    main( sys.argv )

