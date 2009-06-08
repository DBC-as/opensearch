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

# class __FedoraError(Exception):
#     def __init__(self, value):
#         self.value = value
#         def __str__(self):
#             return repr(self.value)


def test_fedora_conn( servername, port ):
    theurl = 'http://%s:%s/fedora'%( servername, port )
    req = urllib2.Request(theurl)
    try:                               
        handle = urllib2.urlopen(req)
    except urllib2.URLError, uer:
        sys.exit( "Could not connect to fedora at %s: '%s' (urllib error code '%s')"%( theurl, uer.reason[1], uer.reason[0] ) )
#     except IOError, e:
#         if hasattr(e, 'code'):
#             if e.code != 401:
#                 __FedoraError, 'This should not happen. Authentication error expected'
#                 print e.code            
#             elif e.code == 401:
#                 print e.headers
#                 print e.headers['www-authenticate']
#                 print 'FEDORA IS UP AND RUNNING!'
#         else:
#             raise __FedoraError, 'FEDORA IS NOT UP AND RUNNING!'
            
urllib2.URLError

if __name__ == '__main__':
    
    test_fedora_conn( 'localhost', '8080' )

