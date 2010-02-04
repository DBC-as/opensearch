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

def is_user( username ):
    """checks if there is a role with username in database """
    
    cmd_str = 'sudo su postgres -c"psql -c\\\"select usename from pg_user where usename=\''+ username +'\';\\\""'
    
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught the following error trying to check user:\n%s" % retcode[1]
        sys.exit(2)
        
    elif "(0 rows)" in retcode[0]:
        return False
    else:
        return True


def is_database( dbname ):
    """checks if there is a database with name dbname"""
    
    cmd_str = 'sudo su postgres -c"psql -c\\\"select datname from pg_database where datname=\''+dbname+'\';\\\""'

    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught the following error trying to check %s table:\n%s" % (dbname, retcode[1])
        sys.exit(2)
        
    elif "(0 rows)" in retcode[0]:
        return False
    else:
        return True
    

def create( database, username, password):
    """makes user with password and table for each argument given."""
    if is_database( database ) or is_user( username ):

        # generate prompt_str
        prompt_str = ""
        if is_database( database ):
            prompt_str = prompt_str + "database"
        if is_user( username ):
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
        if is_database( database ): 
            cmd_str = 'sudo su postgres -c"psql -c\\\"drop database '+ database +';\\\""'
            retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
            if retcode[1]:
                print "caught the following error trying to drop table:\n%s" % retcode[1]
                sys.exit(2)
            print retcode[0], 

        ## drop user
        if is_user( username ):
            cmd_str = 'sudo su postgres -c"psql -c\\\"drop role \\\\\\\"'+ username +'\\\\\\\";\\\""'
            retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
            if retcode[1]:
                print "caught the following error trying to drop user:\n%s" % retcode[1]
                sys.exit(2)
            print retcode[0],

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

    ### create language
    cmd_str = 'sudo su postgres -c"psql -d '+username+' -c\\\"CREATE LANGUAGE plpgsql;\\\""'
    retcode = subprocess.Popen( cmd_str, shell=True, stderr=subprocess.PIPE, stdout=subprocess.PIPE ).communicate()
    if retcode[1]:
        print "caught the following error trying to create language:\n%s" % retcode[1]
        sys.exit(2)
    print retcode[0],


if __name__ == '__main__':
    from optparse import OptionParser
    usage = "usage:\n   makes user with password and table for each argument given."
    parser = OptionParser( usage="%prog username1 username2 ..." )
    (options, args) = parser.parse_args()
    
    if len(args) == 0:
        print "This script needs arguments."
        print usage
    elif args[0] == "-h" or args[0] == "-help" or args[0] == "--help":
        print usage
    else:
        for arg in args:
            print "creating table and role for %s" % arg
            create( arg, arg, arg)
    
