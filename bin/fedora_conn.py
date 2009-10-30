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


def test_fedora_connection( servername, port ):
    theurl = 'http://%s:%s/fedora'%( servername, port )
    req = urllib2.Request( theurl )
    try:                               
        handle = urllib2.urlopen( req )
    except urllib2.HTTPError, her:

        if her.code == 401:
            return (True, her.headers )
        else:
            return (False, ( "Could not connect to fedora at '%s' : '%s' (http status code '%s')"%( her.url, her.msg, her.code ) ) )
    except urllib2.URLError, uer:
        return (False, ( "Could not connect to fedora at '%s' : '%s' (urllib error code '%s')"%( theurl, uer.reason[1], uer.reason[0] ) ) )
            
def main( servername, port ):
    print "Testing for running fedora at %s:%s..."%( servername, port )
    (success, answer_msg ) = test_fedora_connection( servername, port)
    print answer_msg
    if success:
        print 'FEDORA IS UP AND RUNNING!'
        

if __name__ == '__main__':
    
    main( 'localhost', '8080' )

