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
# 


import psycopg2
import os

def login():
    '''handles the login to the database and, if successful, returns a
    connection object'''
    usern = os.environ.get( 'USER' )
    conn = None

    try:
        conn = psycopg2.connect( "dbname='%s' user='%s' host='%s' password='%s'"%( usern, usern, 'localhost', usern))
    except psycopg2.InterfaceError, ife:
        log.fatal( ife.message )
        sys.exit( "I am unable to connect to the database; %s"%( ife.message ) )
    return conn



def showall( cursor ):
    """
    this script print the rows stored in the processqueue and related tables,
    and prints a small summary.
    """


    format = "%-15s %-18s %-2s"
    
    try:
        cursor.execute("""SELECT * from processqueue""")
    except:
        print "I can't SELECT from processqueue"
        
    rows = cursor.fetchall()

    print "processqueue"
    print "----------------------------------------"
    print format % ( "queueID", "fedorahandle", "processing" )
    for r in rows :
        print format % ( r[0],r[1],r[2]  )

    try:
        cursor.execute("""SELECT * from notindexed""")
    except:
        print "I can't SELECT from notindexed"
        
    rows = cursor.fetchall()

    print "\n\nnotindexed"
    print "----------------------------------------"
    print format % ( "queueID", "fedorahandle", "")
    for r in rows :
        print format % ( r[0],r[1], ""  )


    try:
        cursor.execute("""SELECT COUNT(*) from processqueue""")
    except:
        print "I can't SELECT COUNT(*)from processqueue"
        
    total_rows = cursor.fetchall()[0][0]

    try:
        cursor.execute("""SELECT COUNT(*) from processqueue  WHERE processing='Y'""")
    except:
        print "I can't SELECT COUNT(*)from processqueue"

    try:
        active_rows = cursor.fetchall()[0][0]
    except:
        active_rows = 0
            
    print "\n----------------------------------------"
    print "Summary:"
    print " Total Number of jobs in processqueue: %s\n      ( %s marked ready to process, %s marked as processing )." % (total_rows, total_rows-active_rows,active_rows)

    
    try:
        cursor.execute("""SELECT COUNT(*) from notindexed""")
    except:
        print "I can't SELECT COUNT(*)from notindexed"
        
    total_rows = cursor.fetchall()[0][0]

    print " Total Number of jobs in notindexed: %s" % (total_rows)


def main():
    conn = login()
    showall( conn.cursor() )
    conn.commit()


if __name__ == "__main__":
    main()
