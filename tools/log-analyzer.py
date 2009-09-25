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
import datetime
import functools
import re
import logging as log

### default values, consists of lists with operations, logfiletype, and regular expression to search logfile for
types = { "index":        [ "pti", { 'logmsg' : re.compile( r"""PTIThread done with result: """ ) }, "indexing operations" ],
          "store":        [ "datadock", { 'logmsg' : re.compile( r"""Got estimate of """ ) }, "stores in repository" ],
          "job_recieved": [ "datadock", { 'logmsg' : re.compile( r"""found new job: path=""" ) }, "jobs recieved" ],
          "fedora_calls": [ "datadock", { 'logmsg' : re.compile( r"""Fedora call end: (?P<ms>\d+)""" ) }, "calls to fedora" ] }

logline_re = re.compile( r"""(?P<timestamp>(?P<year>^\d{4})(?:-)(?P<month>\d{2})-(?P<day>\d{2})\s(?P<time>(?:\d{2}:?)*)),(?P<level>[A-Z]+),(?P<qname>[^,]*),(?P<context>[^,]*),(?P<class>[^,]*),(?P<method>[^\s]*)\s(?P<logmsg>.*)""" )

default_resolution = 100

log.basicConfig( level = log.INFO,
                format = '%(asctime)s %(levelname)s %(message)s' )
log.getLogger( '' )


def linreg(X, Y):
    """
    Summary
        Linear regression of y = ax + b
    Usage
        real, real = linreg(list, list)
    Returns coefficients to the regression line y=ax+b from x[] and y[]
    """
    from math import sqrt
    if len(X) != len(Y): raise ValueError, 'unequal length'

    N = len(X)
    Sx = Sy = Sxx = Syy = Sxy = 0.0
    for x, y in map(None, X, Y):
        Sx = Sx + x
        Sy = Sy + y
        Sxx = Sxx + x*x
        Syy = Syy + y*y
        Sxy = Sxy + x*y
    det = Sxx * N - Sx * Sx
    a, b = (Sxy * N - Sy * Sx)/det, (Sxx * Sy - Sx * Sxy)/det

    meanerror = residual = 0.0
    for x, y in map(None, X, Y):
        meanerror = meanerror + (y - Sy/N)**2
        residual = residual + (y - a * x - b)**2
    RR = 1 - residual/meanerror
    ss = residual / (N-2)
    Var_a, Var_b = ss * N / det, ss * Sxx / det

#     print "y=ax+b"
#     print "N= %d" % N
#     print "a= %g \\pm t_{%d;\\alpha/2} %g" % (a, N-2, sqrt(Var_a))
#     print "b= %g \\pm t_{%d;\\alpha/2} %g" % (b, N-2, sqrt(Var_b))
#     print "R^2= %g" % RR
#     print "s^2= %g" % ss

    return a, b 

def print_values( timings, resolution ):
    """
    prints the values and status collected from the logfile
    """
    format = "%-20s %-20s"
    print format % ("indexed", "timedelta")
    for i,timing in enumerate( timings ):
        if i == 0:
            print format % ( i*resolution, timing-timing )
        else:
            print format % ( i*resolution, timing-timings[i-1] )


def plot_values( timings, resolution ):
    """
    Plot values with pyplot
    """
    x_axis = []
    y_axis = []
    
    for i,timing in enumerate( timings ):
        x_axis.append( i*resolution )
        if i == 0:
            y_axis.append( (timing-timing).seconds )
        else:
            y_axis.append( (timing-timings[i-1]).seconds )

    x_axis = x_axis[1:]
    y_axis = y_axis[1:]    

    # axis
    plt.ylabel('seconds')
    plt.xlabel('indexed')    

    # regression
    a, b = linreg( x_axis, y_axis )
    plt.plot( [x_axis[0],x_axis[-1]], [(a*x_axis[0]+b),(a*x_axis[-1]+b)] )
    
    # mean
    plt.plot( [x_axis[0],x_axis[-1]],[mean(y_axis),mean(y_axis)], 'g' )

    # plot datapoints
    plt.plot( x_axis, y_axis, 'r' )
    plt.show()


def construct_time( timestr ):
    """
    Constructs datetime object from logfile timestamp
    """
    year = str.split( timestr, "-", 1 )[0] 
    month = str.split( timestr, "-", 2 )[1] 
    day = str.split( str.split( timestr, "-", 2 )[2], " ", 1)[0] 
    time = str.split( str.split( str.split( timestr, "-", 2 )[2], " ", 2)[1], ":" ) 
                
    timing = datetime.datetime( int(year), int(month), int(day), \
                                int(time[0]), int(time[1]), int(time[2]) )
    return timing


def analyze_log_file( logfile, analyzer, resolution ):
    """
    Analyzes `logfile` with `analyzer` returning a tuple containing
    count of lines analyzed and a list containing the analyzer return
    values for each `resolution` matched lines.
    """
    lines = 0
    timings = []
    cur_line = 0

    f = open( logfile, "r" )
    
    for line in f:
        if analyzer( line ):
            lines = lines + 1
            if lines % resolution == 0:
                #timing = str.split( line, "," )[0]
                timings.append( analyzer( line ) )

    return ( lines, timings )

# def fedora_call_timer_analyzer( line, matcher ):
#     """tries to find calls to the fedora repository and do a timing on them
#     """
    

def fedora_calls_analyzer( line, matcher ):
    """
    analyzes `line` from a logfile and returns true if conditions for
    the match is true, false otherwise
    """
    if logline_re.search( line ):
        #log.debug( "logline_re.search( line ).group( matcher[0][0] )=%s"%( logline_re.search( line ).group( matcher[0][0] ) ) )
        subject = logline_re.search( line ).group( matcher[0][0] )
        if "Got estimate of" in line:
            print line
        if matcher[1][0].search( subject ):
            result = matcher[1][0].search( subject )
            #log.debug( "result=%s"%( result.groups() ) )
            #timing = matcher.search( line ).group( 'time' )
            return result.groups()
    else:
        return None

        
def store_analyzer( line, matcher ):
    if logline_re.search( line ):
        subject = logline_re.search( line ).group( matcher[0][0] )
        if matcher[1][0].search( subject ):
            return True
    else:
        return False
    
def job_recieved_analyzer( line, matcher ):
    if logline_re.search( line ):
        subject = logline_re.search( line ).group( matcher[0][0] )
        if matcher[1][0].search( subject ):
            return True
    else:
        return False


def find_files( logtype, folder ):
    """
    find files in FOLDER that matches prefix LOGTYPE, and postfix '.log'.
    if folder is a file it is testet the same way.
    returns a list with matching files. 
    """
    if not os.path.exists( folder ):
        sys.exit( "Could not find folder: %s ...Exiting." % folder )

    files = []    
    ## if file, check if it is a logfile and return it. else check files in folder
    if os.path.isfile( folder ) and logtype in folder and ".log" in folder:      
        return [folder]
    elif os.path.isfile( folder ) and not logtype in folder:
        sys.exit( "Cannot analyze %s as '%s' logfile type"%( folder, logtype ) )
    else:
        for f in os.listdir( folder ):
            if logtype in f and ".log" in f and os.path.isfile( os.path.join( folder, f ) ):
                files.append( os.path.join( folder, f ) )

    if not files:
        sys.exit( "No log files found in folder %s" % folder )
    return sorted( files )


def _get_matching_logfiles( log_type, folder ):
    """
    Internal helper method to get all logfiles in `folder` matching
    criterias `log_type`.

    returns a list of log_files, if eg.:
    
    """
    return find_files( log_type, folder )


def _get_matcher( log_type, operation ):
    """
    Retrives a matcher for log_lines based on `log_type`. The matcher
    is specified as a tuple containing the field to match on and the
    regexp to match with.
    """

    log.debug( "log_type=%s, operation=%s"%( log_type, operation ) )
    log.debug( "types.get( operation )[0] =%s"%( types.get( operation )[0]  ) )
    ## find string to match in logfile.
    match = ""
    if log_type in types.get( operation )[0]:
        # we assume a dictionary at position 1 in the list with only one key and one value:
        match = ( types.get( operation )[1].keys(), types.get( operation )[1].values() )
    else:
        sys.exit( "Could not find analyzer for type '%s', operation '%s'. Use -h to see available operations" % ( log_type, operation ) )

    log.debug( "match criteria: %s in %s"%( operation, types.get(operation)[0] ) )

    return match

def internal_analyzer( log_type, resolution, folder, operation ):
    log_files =  _get_matching_logfiles( log_type, folder )
    match = _get_matcher( log_type, operation )

    analyzer_func = globals()[ operation + "_analyzer" ]
    
    analyzer = functools.partial( analyzer_func, matcher=match )

    #print "-"*21 + " Analyzing " + "-"*21

    total_lines = 0
    timings = []

    for f in log_files:
        #print "analyzing %s" % f, 
        (l, t) = analyze_log_file( f, analyzer, resolution )
        total_lines = total_lines + l
        for elem in t:
            timings.append( elem )
        #print " - Hits: %s" % l

    # remove timestamps == 0
    timings = [ time for time in timings if time != 0 ]

    timings = sorted( timings )

    return (total_lines, timings)
    

def main_analyzer( total_lines, timings, resolution, verbose, operation ):
    """
    Function to print timings found in all logfiles in `folder`
    matching `log_type`.

    If requested, this function additionally plots the found values
    and/or prints details on the timings found
    """
    
    if verbose:
        print_values( timings, resolution )

    summary_func = globals()[ operation + "_summator"]

    summary_func( total_lines, timings, resolution )


def fedora_calls_summator( total_lines, timings, resolution ):
    print "-"*22 + " Summary " + "-"*22
    print "Total matches of fedora calls:  %s" %( total_lines )
    if len( timings ) == 0:
        print "no time deltas found"
    else:
        print "total time:                                 %s%sms" %( " "*len( str( resolution ) ), sum( timings ) )
        print "avg. time per %s matches:                     %sms" %( resolution, sum( timings )/len( timings ) )


def store_summator( total_lines, timings, resolution ):
    print "-"*22 + " Summary " + "-"*22
    text = "Total number of stores in repository:"
    print "%s%s%s" %( text, " "*( 50-( len( str( total_lines ) )+len( text ) ) ), total_lines )

    
def job_recieved_summator( total_lines, timings, resolution ):
    print "-"*22 + " Summary " + "-"*22
    text = "Total number of jobs recieved:"
    print "%s%s%s" %( text, " "*( 50-( len( str( total_lines ) )+len( text ) ) ), total_lines )

def diff_job_summator( jobs_recieved, jobs_stored ):
    print "-"*22 + " Summary " + "-"*22
    text = "Total number of jobs recieved:"
    print "%s%s%s" %( text, " "*( 50-( len( str( jobs_recieved ) )+len( text ) ) ), jobs_recieved )
    text = "Total number of stores in repository:"
    print "%s%s%s" %( text, " "*( 50-( len( str( jobs_stored ) )+len( text ) ) ), jobs_stored )
    text = "Jobs lost along the way:"
    print "%s%s%s" %( text, " "*( 50-( len( str( jobs_recieved-jobs_stored ) )+len( text ) ) ), jobs_recieved-jobs_stored )


if __name__ == '__main__':    

    from optparse import OptionParser
    from optparse import OptionGroup
    
    parser = OptionParser( usage="%prog [options] folder or file to analyze.\n\n example:  python %prog -p -t pti -r 1000 logs/ --indexing\n   analyses all pti*.log files in the logs/ folder,\n   the estimates are made from timestamps made\n   for every 1000 (-r) hit and plots them ( -p )" )

    
    parser.add_option( "-t", type="string", action="store", dest="logfile_type",
                       help="The type of log file to analyse"+
                       "                                    "+
                       " (types available: %s" % " ".join( [x[0] for x in types] ) +")" )
    
    parser.add_option( "-r", type="int", action="store", dest="resolution",
                       help="resolution of timestamps, ie how many hits between retrieving timestamp" )

    parser.add_option( "-p", action="store_true", dest="plot",
                       help="plot the result with pyplot" )

    parser.add_option( "-v", action="store_true", dest="verbose",
                       help="writes all differences between timestamps" )

    group = OptionGroup( parser, "Operations",
                        "Operations that can be performed on the logfiles" )
    group.add_option( "--indexing", action="store_true", dest="index", help="Retrieve time estimates on indexing from logfile(s)" )
    group.add_option( "--stores", action="store_true", dest="store", help="Retrieve time estimates on stores in the repository from logfile(s)" )
    group.add_option( "--recieves", action="store_true", dest="receptor", help="Retrieve time estimates on job receptions from logfile(s)" )
    group.add_option( "--fedora_calls", action="store_true", dest="fedora_calls", help="Retrieve time estimates on calls to the fedora repository from logfile(s)" )
    group.add_option( "--job_diff", action="store_true", dest="job_diff", help="Prints difference between jobs recieved from the harvester and jobs stored in the repository" )
    parser.add_option_group( group )
    
    (options, args) = parser.parse_args()
        
    if options.plot:
        import matplotlib.pyplot as plt
        from pylab import *

    if not options.resolution:
        options.resolution = default_resolution
        log.debug( "resolution: %s (default)" % default_resolution )
    else:
        log.debug( "resolution: %s" % options.resolution )
    
    if not options.logfile_type:
        parser.error( "please specify a logfiletype to operate on")
    else:
        log.debug( "log type: %s" % options.logfile_type )

    folder = ""
    if len( args ) < 1:
        folder = os.getcwd()
        log.debug( "folder/file: %s (default)" % folder )
    else:
        folder = os.path.abspath( args[0] )
        log.debug( "folder/file: %s" % folder )

    operation = ""
    if options.index:
        operation = "index"
    elif options.store:
        operation = "store"
    elif options.receptor:
        operation = "job_recieved"
    elif options.fedora_calls:
        operation = "fedora_calls"
    elif options.job_diff:
        operation = "job_recieved"
        total_lines1, timings1 = internal_analyzer( options.logfile_type, options.resolution, folder, operation )
        operation = "store"
        total_lines2, timings2 = internal_analyzer( options.logfile_type, options.resolution, folder, operation )
        diff_job_summator( total_lines1, total_lines2 )
        sys.exit()
    else:
        parser.error( "please specify an operation")

    total_lines, timings = internal_analyzer( options.logfile_type, options.resolution, folder, operation )
    main_analyzer( total_lines, timings, options.resolution, options.verbose, operation  )  

    if options.plot:
        plot_values( timings, options.resolution )

