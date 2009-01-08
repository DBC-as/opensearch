#!/usr/bin/env python
# -*- coding: utf-8 -*- 

###
# Small python script to scrape the rub files
# The script takes one argument (NO CHECKS!), which is the destination directory
###

import fileinput
import urllib2
import string
import urllib
import os
import os.path
import sys

from BeautifulSoup import BeautifulSoup          # For processing HTML
from BeautifulSoup import BeautifulStoneSoup     # For processing XML
  

def  retrieveData( record, destdir ):
    if destdir[-1] != '/':
        destdir = destdir + '/'


    #print 'record',record

    ### retriving rub page

    dspaceSoup = BeautifulSoup( str(record) )
    urllst = dspaceSoup.findAll(  'dc:identifier' )
    url = str(urllst[-1]).split("<dc:identifier>")[1].split("</dc:identifier>")[0]
    page = urllib2.urlopen( url )

    ### isolating document references
    pagesoup = BeautifulSoup( page )
    table = pagesoup.findAll( 'table', attrs={"class" : "miscTable", "align" : "center" } )[1]
        
    tablesoup = BeautifulSoup( str(table) )

    ### find document name
    docTitle = str( tablesoup.findAll( 'td', attrs={"headers" : "t1", "class" : "standard" } )[0] )
    docTitle = docTitle.split('>')[1].split('<')[0] 
    
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
        destfile = destdir + recdir + docTitle
        #print "href          -> ", href
        #print "docTitle -> ", docTitle
        print "destfile    -> ", destfile
        print 'downloading ', docTitle
        urllib.urlretrieve( href  , destfile )



def main(destdir):
    if not os.path.isdir(destdir):
        os.mkdir( destdir )
    
    page = urllib2.urlopen( 'http://rudar.ruc.dk/dspace-oai/request?verb=ListRecords&metadataPrefix=oai_dc' )
    RUBsoup = BeautifulStoneSoup( page )
    reclst = RUBsoup.findAll( 'oai_dc:dc' )
    for rec in reclst:
        retrieveData( rec, destdir )

if __name__ == '__main__':
    main( sys.argv[1:2][0])




    
