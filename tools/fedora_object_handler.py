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


import urllib2
import sys


class FedoraObjectHandler( object ):
    """ Class that handles fedora objects based on their identifier
    (pid)
    """

    def __init__( self, fedora_url ):
        self.fedora_url = fedora_url


    def login( self, user, passwd ):
        """does a session login to the fedora repository
        """
        password_mgr = urllib2.HTTPPasswordMgrWithDefaultRealm()
        tlu = self.fedora_url
        password_mgr.add_password( None, tlu, user, passwd )

        handler = urllib2.HTTPBasicAuthHandler( password_mgr )
        opener = urllib2.build_opener( handler )
        urllib2.install_opener( opener )


    def retrieve( self, pidlist ):
        """ Retrieves all pids in self.pidlist 

        returns a dictionary with the pids as keys and the foxml for
        the pid as value.
        """
        pidlist = self._construct_pidlist( pidlist )
        datadict = dict()
        for pid in pidlist:
            datadict[ pid ] = self._get_xml( pid )

        return datadict


    def delete( self, pidlist ):
        """Deletes all pids in self.pidlist

        returns a list of pairs with pid at first position and a
        boolean indicating the result of the delete operation on that
        pid at second position.
        """
        pidlist = self._construct_pidlist( pidlist )
        return_values = list()
        for pid in pidlist:
            return_values = [ pid, self._delete_object( pid ) ]
        return return_values
    

    def search( self, query, field_list ):
        print self._search_repository( query, field_list, "xml")
        

    def _construct_pid_list( self, pidlist ):
        """ Tries to recognize the format of the pidlist (can be one,
        several separated by commas or a range within a namespace)
        """
        self.pidlist = list()
        pidlist = pidlist[0]
        if "-" in pidlist:
            firstpid =  int( pidlist[ pidlist.index( ":" )+1:pidlist.index( "-" ) ] )
            lastpid = int( pidlist[ pidlist.rindex( ":" )+1:len( pidlist ) ] )
            pidns = pidlist[ :pidlist.index( ":" ) ]

            for rang in xrange( firstpid, lastpid+1 ):
                self.pidlist.append( pidns+":"+str( rang ) )
        elif "," in pidlist:
            self.pidlist = pidlist.split( "," )
        else:
            self.pidlist.append( pidlist )

            
    def _delete_object( self, pid ):
        """Deletes a single object from the fedora repository.  It is
        unfortunately not (yet) possible to specify a list or a range
        of pids to be deleted, so we make one request per deletion
        """

        del_url = "%s/objects/%s"%( self.fedora_url, pid )

        req = urllib2.Request( del_url )
        req.get_method = lambda: 'DELETE'
        resp = self._do_http_request( req )
        
        if resp is not None:
            print "deleted %s"%( pid )
        
        # try:
        #     response = urllib2.urlopen( req )
        # except urllib2.URLError, e:
        #     if hasattr(e, 'reason'):
        #         sys.exit( 'Could not connect to server at %s. Reason: %s'%( self.feodra_url, e.reason ) )
        # else:
        #     print response.code
        #     if response.code == 204 or response.code == 200:
        #         return True
        #     elif response.code == 404:
        #         # no path in db registry for pid
        #         return False
        #     else:
        #         return False

    def _get_xml( self, pid ):
        """retrieves the foxml of an object designated by `pid`
        """
        del_url = "%s/objects/%s/objectXML?format=xml"%( self.fedora_url, pid )
        req = urllib2.Request( del_url )
        resp = self._do_http_request( req )
        
        if resp is not None:
            return resp
            
        # try:
        #     response = urllib2.urlopen( req )
        # except urllib2.URLError, e:
        #     if hasattr(e, 'reason'):
        #         sys.exit( 'Could not connect to server at %s. Reason: %s'%( self.feodra_url, e.reason ) )
        # else:
        #     if response.code == 204 or response.code == 200:
        #         return response.read()
        #     elif response.code == 404:
        #         # no path in db registry for pid
        #         pass

    def _search_repository( self, search_terms, fields, return_format ):
        """ Performs a search in the fedora repository given a
        `search_term`, a `list` of `fields` (minimum one item is
        required) and will return the result in `return_format`, where
        `return_format` is one of `xml` or `html`
        """
        search_url = "%s/objects?terms=%s%s&resultFormat=%s"%(self.fedora_url, search_terms, "".join( [ "&%s=true"%(field) for field in fields ] ), return_format )

        req = urllib2.Request( search_url )
        resp = self._do_http_request( req )

        if resp is not None:
            return resp


    def _do_http_request( self, req ):
        try:
            response = urllib2.urlopen( req )
        except urllib2.URLError, e:
            if hasattr(e, 'reason'):
                sys.exit( 'Could not connect to server at %s. Reason: %s'%( self.feodra_url, e.reason ) )
        else:
            if response.code == 204 or response.code == 200:
                return response.read()
            elif response.code == 404:
                # no path in db registry for pid
                return None
            else:
                return None

        

        
if __name__ == '__main__':
    from optparse import OptionParser

    usagetext = """%prog [options] pid{{,pid}|-pid}

    examples of usage:
    %prog -s localhost:8080 -q 710100 -f pid,title


    %prog -s localhost:8080 -d demo:1
      deletes the pid specified by demo:1
    %prog -s localhost:8080 -d demo:1,demo:4
      deletes pids demo:1 and demo:4
    %prog -s localhost:8080 -d demo:1-demo:4
      deletes pids demo:1 through demo:4 inclusive

    %prog -s localhost:8080 -r demo:1
      retrieves the pid specified by demo:1
    %prog -s localhost:8080 -r demo:1,demo:4
      retrieve pids demo:1 and demo:4
    %prog -s localhost:8080 -r demo:1-demo:4
      retrieve pids demo:1 through demo:4 inclusive

    Please note that when providing a list of pids, a comma (',') is the only allowed separator
    """
    parser = OptionParser( usage=usagetext )

    parser.add_option( "-s", "--server", dest="server",
                       action="store", help="Server name of fedora repository")

    parser.add_option( "-u", "--user", dest="usern",
                       action="store", help="username if authentication is required" )
    
    parser.add_option( "-p", "--pass", dest="passwd",
                       action="store", help="passwd if authentication is required" )
    
    parser.add_option( "-d", "--delete", dest="delete", 
                       action="store_true", help="Delete pid, list of pids or range of pids" )

    parser.add_option( "-r", "--retrieve", dest="retrieve",
                       action="store_true", help="Retrieve pid, list of pids or range of pids" )

    parser.add_option( "-q", "--query", dest="query",
                       action="store", help="Queries repository with `query`.\nDefault field to search is `pid`, use -f option to specify fields" )

    parser.add_option( "-f", "--fields", dest="field_list",
                       action="store", help="Used in conjunction with -q, this option specifies which fields are searched in the repository" )

    (options, args) = parser.parse_args()

    if args == "":
        parser.error( "Please specify one or more pids or a range of pids" )
    
    if not ( options.delete or options.retrieve or options.query ):
        parser.error( "Please give an action to perform" )

    if not options.server:
        parser.error( "Please specify server name and optionally port number (if != 80)" )
    url = "http://%s/fedora"%( options.server)

    c = FedoraObjectHandler( url )

    if options.usern and options.passwd:
        user = options.usern
        passwd = options.passwd
        c.login( user, passwd )
    elif options.usern or options.passwd:
        parser.error( "please provide both username and password for authentication" )

    if options.retrieve:
        datalist = c.retrieve( args )
        for data in datalist.keys():
            if datalist.get( data ) is not None:
                print "="*30, " foxml for pid ",data,":", "="*30
                print datalist.get( data )
    elif options.delete:
        retlist = c.delete( args )
        print retlist
        for val in retlist:
            print "deleted object: ", val

    elif options.query is not None:
        query = options.query
        fields = options.field_list.split( "," )
        if len( fields ) < 1:
            fields = [ 'pid' ]
        c.search( query, fields )
