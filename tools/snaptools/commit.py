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


# Python script to force a commit of all changes since last commit for a Solr server

import subprocess

def commit(solr_hostname, solr_port, solr_webapp):
    """
    function to force a commit of all changes since last commit for a
    Solr server
    """
    curl_url = "http://%s:%s/%s/update"%(solr_hostname, solr_port, solr_webapp)
    curl_cmd = """curl %s -s -H 'Content-type:text/xml; charset=utf-8' -d \"<commit/>\" """%curl_url

    retcode = subprocess.Popen( curl_cmd, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught error during commit: %s" % retcode[1]
        exit(1)

    if not '<lst name="responseHeader"><int name="status">0</int>' in retcode[0]:
        print "commit request to Solr at %s with %s failed: %s"%(curl_url, curl_cmd, retcode[0])
        exit(2)

    print "committed to http://%s:%s/%s"%(solr_hostname, solr_port, solr_webapp)

if __name__ == '__main__':
    default_solr_hostname = "localhost"
    default_solr_port = 8983
    default_solr_webapp = "solr"

    from optparse import OptionParser
    
    parser = OptionParser( usage="%prog [options]" )
    
    parser.add_option( "-n", dest="hostname", default=default_solr_hostname,
                       help="Hostname of the solr server to commit to. defaults to '%s'"%default_solr_hostname )
    parser.add_option( "-p", dest="port", type="int", default=default_solr_port,
                       help="Port of the solr server to commit to. defaults to '%s'"%default_solr_port )
    parser.add_option( "-w", dest="webapp", default=default_solr_webapp,
                       help="Webapp name of the solr server to commit to. defaults to '%s'"%default_solr_webapp )

    (options, args) = parser.parse_args()
    
    config = ConfigParser.RawConfigParser()
    config.read(os.path.join(os.getcwd(),"snap.conf"))
        
    hostname = default_solr_hostname
    if options.hostname:
        hostname = options.hostname
    else:
        try:
            config.get("snap-configuration", "solr_host" )
        except ConfigParser.NoOptionError:    
            pass
        if config.get("snap-configuration", "solr_host" ) != "":
            hostname = config.get("snap-configuration", "solr_host" )
            
    port = default_solr_port
    if options.port:
        port = options.port
    else:
        try:
            config.get("snap-configuration", "solr_port" )
        except ConfigParser.NoOptionError:    
            pass
        if config.get("snap-configuration", "solr_port" ) != "":
            port = config.get("snap-configuration", "solr_port" )
            
    webapp = default_solr_webapp
    if options.webapp:
        webapp = options.webapp
    else:
        try:
            config.get("snap-configuration", "solr_webapp" )
        except ConfigParser.NoOptionError:    
            pass
        if config.get("snap-configuration", "solr_webapp" ) != "":
            webapp = config.get("snap-configuration", "solr_webapp" )
    
    commit(hostname, port, webapp)    



    

    
