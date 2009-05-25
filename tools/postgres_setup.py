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
       
    return conn


def _open_and_execute( cursor, sqlfile ):
    td = open( sqlfile, 'r' ).read()
    print "trying to execute %s with %s"%( sqlfile, cursor )
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
    conn = login()
    teardown_setup( conn.cursor() )
    conn.commit()

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
