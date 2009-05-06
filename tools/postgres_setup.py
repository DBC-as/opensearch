#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import os
import subprocess


src_dir = os.getcwd()

import psycopg2

def login():
    usern = os.environ.get( 'USER' )

    try:
        conn = psycopg2.connect( "dbname='%s' user='%s' password='%s'"%( usern, usern, usern))
    except:
        print "I am unable to connect to the database."
       
    return conn.cursor()


def _open_and_execute( cursor, sqlfile ):
    td = open( sqlfile, 'r' ).read()
    try:
        cursor.execute( td )
    except Exception, e:
        print "Cannot execute sqlcommand: %s"%(e)


def teardown_setup( cursor ):

    _open_and_execute( cursor, '../admin/teardown.sql' )

    init_db = [ 'processqueue_init', 'statistics_init', 'not_indexed', 'not_docked' ]

    for sql in init_db:
        _open_and_execute( cursor, '../admin/'+sql+'.sql' )
    

def main():
    cursor = login()
    teardown_setup( cursor )

# def setup():
#     global src_dir

#     if src_dir.endswith("tools"):
#         src_dir = src_dir.replace("tools", "admin" )

#     runproc = subprocess.Popen( 'psql -f teardown.sql', shell=True, cwd=src_dir ) 
#     runproc.communicate()[ 0 ]
#     runproc = subprocess.Popen( 'psql -f init.sql', shell=True, cwd=src_dir )    
#     runproc.communicate()[ 0 ]

if __name__ == "__main__":
    main()
