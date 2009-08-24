#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-


import os.path
import string
import datetime
#import matplotlib.pyplot as plt
import sys
from pylab import *

def construct_time( timestr ):
    year = str.split( timestr, "-", 1 )[0] 
    month = str.split( timestr, "-", 2 )[1] 
    day = str.split( str.split( timestr, "-", 2 )[2], " ", 1)[0] 
    time = str.split( str.split( str.split( timestr, "-", 2 )[2], " ", 2)[1], ":" ) 
                
    timing = datetime.datetime( int(year), int(month), int(day), \
                                int(time[0]), int(time[1]), int(time[2]) )
    return timing


def construct_total_str( timings, resolution ):
    total = "Total time " + str( timings[-1]-timings[0] )
    total = total + "\nindexed posts " + str( len(timings)*resolution)
    total = total + "\naverage indextime pr " + str( resolution ) + " posts = " + str( (timings[-1]-timings[0])/len( timings ) )    
    return total



def print_values( timings, resolution ):
    format = "%-20s %-20s"
    print format % ("indexed", "timedelta")
    for i,timing in enumerate( timings ):
        if i == 0:
            print format % ( i*resolution, timing-timing )
        else:
            print format % ( i*resolution, timing-timings[i-1] )

    print "---------------------------------------"
    print construct_total_str( timings, resolution )
    

def plot_values( timings, resolution ):
    x_axis = []
    y_axis = []
    
    
    for i,timing in enumerate( timings ):
        x_axis.append( i*resolution )
        if i == 0:
            y_axis.append( (timing-timing).seconds )
        else:
            y_axis.append( (timing-timings[i-1]).seconds )
            
    plt.text(0.5, 0.5, construct_total_str( timings, resolution ), bbox=dict(facecolor='red', alpha=0.5))

    mean(y_axis)
    plt.ylabel('seconds')
    plt.xlabel('indexed')    

    meany = mean( y_axis[3:] )    
    mean_y = [meany for x in range(0,len(x_axis)) ]
    
    plt.plot(x_axis, y_axis, 'r')
    plt.plot(x_axis, mean_y, 'g')
    plt.show()


def main( file, resolution ):

    os.path.exists( file )
    os.path.isfile( file )
    f = open( file, "r" )
    
    timings = []
    cur_line = 0

    for line in f:
        if cur_line == resolution:
            cur_line = 0
        
        if "PTIThread done with result: " in line:            
            if cur_line == 0:
                timing = str.split( line, "," )[0]
                timings.append( construct_time( timing ) )
            cur_line = cur_line + 1
    
    
    print_values( timings, resolution )    
    plot_values( timings, resolution )


if __name__ == '__main__':    
    from optparse import OptionParser
    parser = OptionParser( usage="%prog [options] pti.log file" )
    
    parser.add_option( "-r", type="int", action="store", dest="resolution",
                       help="resolution of the plot" )

    (options, args) = parser.parse_args()

    resolution = 100
    if options.resolution:
        resolution = options.resolution
    
    if not args:
        sys.exit( "need a pti.log file as argument" )
    
    main( args[0], resolution )
