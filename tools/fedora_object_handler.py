#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import urllib2
import sys


class FedoraObjectHandler( object ):
    """ 
    """

    def __init__( self, pidlist, fedora_url ):
        """ 
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

        self.fedora_url = fedora_url


    def login( self, user, passwd ):
        """
        """
        password_mgr = urllib2.HTTPPasswordMgrWithDefaultRealm()
        tlu = self.fedora_url
        password_mgr.add_password( None, tlu, user, passwd )

        handler = urllib2.HTTPBasicAuthHandler( password_mgr )
        opener = urllib2.build_opener( handler )
        urllib2.install_opener( opener )

    def retrieve( self ):
        datadict = dict()
        for pid in self.pidlist:
            datadict[ pid ] = self._get_xml( pid )
        return datadict


    def delete( self ):
        return_values = list()
        for pid in self.pidlist:
            return_values = [ pid, self._delete_object( pid ) ]
        return return_values
    
            
    def _delete_object( self, pid ):
        """Deletes a single object from the fedora repository.  It is
        unfortunately not (yet) possible to specify a list or a range
        of pids to be deleted, so we make one request per deletion
        """

        del_url = "%s/objects/%s"%( self.fedora_url, pid )

        req = urllib2.Request( del_url )
        req.get_method = lambda: 'DELETE'
        
        try:
            response = urllib2.urlopen( req )
        except urllib2.URLError, e:
            if hasattr(e, 'reason'):
                sys.exit( 'Could not connect to server at %s. Reason: %s'%( self.feodra_url, e.reason ) )
        else:
            print response.code
            if response.code == 204 or response.code == 200:
                return True
            elif response.code == 404:
                # no path in db registry for pid
                return False
            else:
                return False

    def _get_xml( self, pid ):
        """
        """
        del_url = "%s/objects/%s/objectXML?format=xml"%( self.fedora_url, pid )

        req = urllib2.Request( del_url )
        
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
                pass

            
if __name__ == '__main__':
    from optparse import OptionParser

    usagetext = """%prog [options] pid{{,pid}|-pid}

    examples of usage:
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
    
    (options, args) = parser.parse_args()

    if args == "":
        parser.error( "Please specify one or more pids or a range of pids" )
    
    if not ( options.delete or options.retrieve ):
        parser.error( "Please give an action to perform" )

    if not options.server:
        parser.error( "Please specify server name and optionally port number (if != 80)" )
    url = "http://%s/fedora"%( options.server)

    c = FedoraObjectHandler( args, url )

    if options.usern and options.passwd:
        user = options.usern
        passwd = options.passwd
        c.login( user, passwd )
    elif options.usern or options.passwd:
        parser.error( "please provide both username and password for authentication" )

    if options.retrieve:
        datalist = c.retrieve()
        for data in datalist.keys():
            if datalist.get( data ) is not None:
                print "="*30, " foxml for pid ",data,":", "="*30
                print datalist.get( data )
    elif options.delete:
        retlist = c.delete()
        print retlist
        for val in retlist:
            print "deleted object: ", val


