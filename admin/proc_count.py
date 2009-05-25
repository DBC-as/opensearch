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


import psycopg2
import os

def process_queue_count():
    usern = os.environ.get( 'USER' )

    try:
        conn = psycopg2.connect( "dbname='%s' user='%s' password='%s'"%( usern, usern, usern))
    except:
        print "I am unable to connect to the database."
       
    cur = conn.cursor()
    try:
        cur.execute("""SELECT * from processqueue""")
    except:
        print "I can't SELECT from processqueue"
   
    rows = cur.fetchall()
    print "number of pending jobs: %s"%(len( rows ))

    try:
 	cur.execute( """SELECT processing FROM processqueue WHERE processing='Y'""")
    except:
	print "can't SELECT from processqueue"

    rows = cur.fetchall()
    print "number of jobs being processed: %s"%( len( rows) )

    try:
 	cur.execute( """SELECT * FROM notindexed""")
    except:
	print "can't SELECT from notindexed"

    rows = cur.fetchall()
    print "number of jobs not indexed: %s"%( len( rows) )

    return len( rows )

process_queue_count()
