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


import subprocess
import sys


# default values
database = "fedora32"
username = "fedoraAdmin"
password = "fedoraAdmin"
    

def is_fedora_user( username ):
    """checks if there is a role with username in database """
    
    cmd_str = 'sudo su postgres -c"psql -c\\\"select usename from pg_user where usename=\''+ username +'\';\\\""'
    
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught the following error trying to check fedoraAdmin user:\n%s" % retcode[1]
        sys.exit(2)
        
    elif "(0 rows)" in retcode[0]:
        return False
    else:
        return True


def is_fedora_database( dbname ):
    """checks if there is a database with name dbname"""
    
    cmd_str = 'sudo su postgres -c"psql -c\\\"select datname from pg_database where datname=\''+dbname+'\';\\\""'

    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught the following error trying to check % table:\n%s" % (dbname, retcode[1])
        sys.exit(2)
        
    elif "(0 rows)" in retcode[0]:
        return False
    else:
        return True
    

def create_database_and_role( database, username, password ):
    """Creates a database and a user for a fedora repository"""

    print "using\n Database: %s\n Username: %s\n Password: %s" % ( database, username, password )
    # check if table or user exist
    if is_fedora_database( database ) or is_fedora_user( username ):

        # generate prompt_str
        prompt_str = ""
        if is_fedora_database( database ):
            prompt_str = prompt_str + "database"
        if is_fedora_user( username ):
            if prompt_str != "":
                prompt_str = prompt_str + " and username"
            else:
                prompt_str = "username"
        prompt_str = prompt_str + " already exists. whipe it? [Y/n]"

        # prompt for answer
        answer = None
        while not( answer == "" or answer == "y" or answer == "Y" or answer == "yes" or answer == "n" or answer == "N" or answer == "no" ):
            answer = raw_input( prompt_str )

        if answer == "n" or answer == "N" or answer == "no":
            print "Exiting"
            sys.exit(2)

        ## drop database            
        if is_fedora_database( database ): 
            cmd_str = 'sudo su postgres -c"psql -c\\\"drop database '+ database +';\\\""'
            retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
            if retcode[1]:
                print "caught the following error trying to drop table:\n%s" % retcode[1]
                sys.exit(2)
            print retcode[0], 

        ## drop user
        if is_fedora_user( username ):
            cmd_str = 'sudo su postgres -c"psql -c\\\"drop role \\\\\\\"'+ username +'\\\\\\\";\\\""'
            retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
            if retcode[1]:
                print "caught the following error trying to drop user:\n%s" % retcode[1]
                sys.exit(2)
            print retcode[0],
    
    ### Create user
    cmd_str = 'sudo su postgres -c"psql -c\\\"CREATE ROLE \\\\\\\"'+ username +'\\\\\\\" LOGIN PASSWORD \''+ password +'\';\\\""'
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught the following error trying to create database:\n%s" % retcode[1]
        sys.exit(2)
    print retcode[0],

    ### Create database
    cmd_str = 'sudo su postgres -c"psql -c\\\"CREATE DATABASE \\\\\\\"'+ database +'\\\\\\\" WITH ENCODING=\'UTF8\' OWNER=\\\\\\\"'+ username +'\\\\\\\";\\\""'
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught the following error trying to create database:\n%s" % retcode[1]
        sys.exit(2)
    print retcode[0],

    print "created role and database for fedora repository"
    
    
if __name__ == '__main__':
    from optparse import OptionParser

    parser = OptionParser( usage="%prog [options]" )
    
    parser.add_option( "-d", type="string", action="store", dest="database",
                       help="Name of the database to create "+
                       "                                  "+
                       "(if not set, value='"+database+"')")
    
    parser.add_option( "-u", type="string", action="store", dest="username",
                       help="username of the role to create "+
                       "                                  "+
                       "(if not set, value='"+username+"')")
    
    parser.add_option( "-p", type="string", action="store", dest="password",
                       help="password for the role to create"+
                       "                                  "+
                       "(if not set, value='"+password+"')")

    (options, args) = parser.parse_args()


    # parse arguments
    if options.database:
        database = options.database
    if options.username:
        username = options.username
    if options.password:
        password = options.password
    
    create_database_and_role( database, username, password)
