#!/bin/bash

# directory where this script is located:
SCRIPTDIR="$( cd "$( dirname "$0" )" && pwd )" # using quotes to eliminate errors from nasty characters like space.
JAVAEXECUTABLE=$SCRIPTDIR/../dist/OpenSearch_EMPTYFEDORA.jar
JAVASOURCE=$SCRIPTDIR/../javatools/src/dk/dbc/opensearch/common/fedora/EmptyFedora.java
SVNVERSION=
EXECUTABLEVERSION=

##
#  Programflow:
#  *) Check that executable is available
#     *) If not, check that source is avaliable
#        *) if not, abort
#     *) Else, check version of executable againts current version
#        *) If current is newer than executable, recompile
#  *) Run executable


get_exeutable_svn_version() 
{
    echo Hello
}

get_current_svn_version()
{
    OLD_DIR=$PWD
    cd $SCRIPTDIR/..
    SVNVERSION=`svnversion`
    cd $OLD_DIR
}

check_availability_of_source()
{
    if [ ! -e $JAVASOURCE ]
    then
	echo Can not find the sourcecode: $JAVASOURCE
	echo Exiting...
	exit 1
    fi
}

rebuild_executable()
{
    echo Rebuilding ....
    check_availability_of_source
    ( cd $SCRIPTDIR && cd ../ && ant dist_emptyfedora )
    if [ "$?" -ne "0" ]
    then 
	echo Building the executable failed.
	echo Exiting...
    fi
}

run_executable()
{
    ( cd $SCRIPTDIR/../ && java -jar dist/OpenSearch_EMPTYFEDORA.jar )
}

rebuild_executable

run_executable

#get_current_svn_version

#echo current svnversion is: $SVNVERSION
