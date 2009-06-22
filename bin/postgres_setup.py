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
import sys
import psycopg2
import subprocess

src_dir = os.getcwd()

import logging as log

log.basicConfig( level = log.DEBUG,
                format = '%(asctime)s %(levelname)s %(message)s' )
log.getLogger( '' )


def login():
    '''handles the login to the database and, if successful, returns a
    connection object'''
    usern = os.environ.get( 'USER' )
    conn = None

    try:
        conn = psycopg2.connect( "dbname='%s' user='%s' password='%s'"%( usern, usern, usern))
    except psycopg2.InterfaceError, ife:
        log.fatal( ife.message )
        sys.exit( "I am unable to connect to the database; %s"%( ife.message ) )
       
    return conn


def _open_and_execute( cursor, sqlfile ):
    td = open( sqlfile, 'r' ).read()
    log.debug( "trying to execute %s with %s"%( sqlfile, cursor ) )
    try:
        cursor.execute( td )
    except psycopg2.ProgrammingError, pe:
        sys.exit( "Cannot execute sqlcommand '%s'\nReason: %s"%( td, pe.message ) )


def teardown_setup( cursor ):
    '''Performs a teardown and subsequent setup of the database. This
    method holds an internal list of sql files to execute in a given
    order
    '''
    _open_and_execute( cursor, '../admin/teardown.sql' )

    _init_db = [ 'processqueue_init', 'statistics_init', 'not_indexed', 'not_docked' ]

    for sql in _init_db:
        _open_and_execute( cursor, '../admin/'+sql+'.sql' )
    

def main():
    conn = login()
    teardown_setup( conn.cursor() )
    conn.commit()


if __name__ == "__main__":
    main()
