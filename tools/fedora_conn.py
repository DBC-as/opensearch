#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-


import urllib2


class __FedoraError(Exception):
    def __init__(self, value):
        self.value = value
        def __str__(self):
            return repr(self.value)


def test_fedora_conn():
    theurl = 'http://localhost:8080/fedora'
    req = urllib2.Request(theurl)
    try:
        handle = urllib2.urlopen(req)
    except IOError, e:
        if hasattr(e, 'code'):
            if e.code != 401:
                __FedoraError, 'This should not happen. Authentication error expected'
                print e.code            
            elif e.code == 401:
                print e.headers
                print e.headers['www-authenticate']
                print 'FEDORA IS UP AND RUNNING!'
        else:
            raise __FedoraError, 'FEDORA IS NOT UP AND RUNNING!'
            

if __name__ == '__main__':
    test_fedora_conn()


"""
# code for loggin in

try:
    fedora = urllib.urlopen( 'http://localhost:8080/fedora' )
except:
    print 'exception caught'

"""
