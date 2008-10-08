#!/usr/bin/env python
# -*- coding: utf-8 -*- 

import fileinput
import urllib2
import string
import urllib
import os
import sys

from BeautifulSoup import BeautifulSoup          # For processing HTML
from BeautifulSoup import BeautifulStoneSoup     # For processing XML
  

def  retrieveData( record, destdir ):
    if destdir[-1] != '/':
        destdir = destdir + '/'

    ### retriving rub page
    dspaceSoup = BeautifulSoup( str(record) )
    urllst = dspaceSoup.findAll(  'dc:identifier' )
    url = str(urllst[-1]).split("<dc:identifier>")[1].split("</dc:identifier>")[0]
    #print url
    page = urllib2.urlopen( url )

    ### isolating document references
    pagesoup = BeautifulSoup( page )
    table = pagesoup.findAll( 'table', attrs={"class" : "miscTable", "align" : "center" } )[1]
    tablesoup = BeautifulSoup( str(table) )
    hreflst = tablesoup.findAll( 'a')

    ### make recdir
    recdir = str( hreflst[0] ).split('/')[-5]+'/'
    os.mkdir( destdir + recdir )

    ### write record
    print 'record destfile ' + destdir + recdir + 'dc.xml'
    recordfile = open( destdir + recdir + 'dc.xml', 'w')                    
    recordfile.write( str( record) )                    
    recordfile.close() 

    ### for each document reference download it
    for href in hreflst:
        
        href = "http://rudar.ruc.dk"+str( href ).split( 'href="' )[1].split( '">')[0]
        docname = href.split( '/' )[-1]
        destfile = destdir + recdir + docname
        #print "href          -> ", href
        #print "docname -> ", docname
        #print "destfile    -> ", destfile
        print 'downloading ', docname
        urllib.urlretrieve( href  , destfile )



def main(destdir):
    
    page = urllib2.urlopen( 'http://rudar.ruc.dk/dspace-oai/request?verb=ListRecords&metadataPrefix=oai_dc' )
    RUBsoup = BeautifulStoneSoup( page )
    reclst = RUBsoup.findAll( 'oai_dc:dc' )
    for rec in reclst:
        retrieveData( rec, destdir )

if __name__ == '__main__':
    main( sys.argv[1:2][0])




    
