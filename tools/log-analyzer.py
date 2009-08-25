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
import matplotlib.pyplot as plt
from pylab import *

### default values, consists of lists with logfiletype, and prefix to search logfile for
types = [["pti","PTIThread done with result: "],
         ["datadock", "Got estimate of "]]

default_resolution = 100


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


def analyze_log_file( logfile, match, resolution ):
    """
    Analyzes LOGFILE. Reads file, and returns a tuple
    where the first element is the number of lines matching
    MATCH, and the second element is a list with datetimes
    for every RESOLUTION matched lines.
    """
    lines = 0
    timings = []
    cur_line = 0

    f = open( logfile, "r" )
    
    for line in f:
        if match in line:
            lines = lines + 1
            if lines % resolution == 0:
                timing = str.split( line, "," )[0]
                timings.append( construct_time( timing ) )
    return ( lines, timings )
        

def find_files( logtype, folder ):
    """
    find files in FOLDER that matches prefix LOGTYPE, and postfix '.log'.
    if folder is a file it is testet the same way.
    returns a list with matching files. 
    """
    if not os.path.exists( folder ):
        print "Could not find folder: %s ...Exiting." % folder
        sys.exit( 2 )

    files = []    
    ## if file, check if it is a logfile and return it. else check files in folder
    if os.path.isfile( folder ) and logtype in f and ".log" in f:      
        return [folder]
    else:
        for f in os.listdir( folder ):
            if logtype in f and ".log" in f and os.path.isfile( os.path.join( folder, f ) ):
                files.append( os.path.join( folder, f ) )

    if not files:
        print "No log files found in folder %s" % folder
        sys.exit( 2 )
    return sorted( files )


def main( log_type, resolution, folder, plot, verbose ):
    """
    Main method
    Identifies string to match in log file through LOG_TYPE
    Identifies log files in FOLDER
    analyzes log files.
    maybe print and plot, depending on the PLOT, VERBOSE arguments 
    """
    ## find string to match in logfile.
    match = ""
    for t in types:
        if log_type in t[0]:
            match = t[1]
            break

    if match == "":
        print "Could not find type %s. Known logtypes: %s" % ( log_type, " ".join( [x[0] for x in types] ) )
        sys.exit( 2 )

    log_files = find_files( log_type, folder )

    print "--------------- Analyzing ---------------"
    total_lines = 0
    timings = []

    for f in log_files:
        print "analyzing %s" % f, 
        (l, t) = analyze_log_file( f, match, resolution )
        total_lines = total_lines + l
        for elem in t:
            timings.append( elem )
        print " - Hits: %s" % l

    if verbose:
        print_values( timings, resolution )

    print "--------------- Summary ---------------"
    print "total time = %s" % str( timings[-1] - timings[0] )
    print "indexed posts = %s" % total_lines
    print "average index time per %s posts = %s" % ( resolution, str( (timings[-1]-timings[0])/len( timings ) ) )

    if plot:
        plot_values( timings, resolution )
    

if __name__ == '__main__':    

    from optparse import OptionParser

    parser = OptionParser( usage="%prog [options] folder or file to analyze" )

    
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
    
    (options, args) = parser.parse_args()

    
    ## Parsing arguments    
    print "--------------- arguments ---------------"
    if not options.resolution:
        options.resolution = default_resolution
        print "resolution: %s (default)" % default_resolution
    else:
        print "resolution: %s" % options.resolution
    
    if not options.logfile_type:
        options.logfile_type = types[0][0]
        print "log type: %s (default)" % options.logfile_type
    else:
        print "log type: %s" % options.logfile_type

    folder = ""
    if len( args ) < 1:
        folder = os.getcwd()
        print "folder/file: %s (default)" % folder
    else:
        folder = os.path.abspath( args[0] )
        print "folder/file: %s" % folder
    
    main( options.logfile_type, options.resolution, folder, options.plot, options.verbose  )  
