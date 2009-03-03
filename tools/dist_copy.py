#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-


import subprocess, sys, os.path


help = """
Make and copy distribution jar files to <server> <folder>:

   default values:
           server: sempu
           folder: dist/dist
"""


src_dir = os.getcwd()

server = 'sempu'
middle = ':./'
folder = 'dist/dist'

ant_dist = "ant dist"
scp_dist = "scp -r ../dist/*.jar "


def __default():
    global scp_dist
    scp_dist += server + middle + folder


def set_server():
    global server
    global folder
    global scp_dist

    try:
        server = sys.argv[1]
        try:
            folder = sys.argv[2]
            __default()
        except:
            __default()
    except:
        __default()
    

def make_dist():
    global src_dir
    global ant_dist
    global scp_dist

    if src_dir.endswith("tools"):
        src_dir = src_dir.replace("/tools", "" )
        
    runproc = subprocess.Popen( ant_dist, shell=True, cwd=src_dir )    
    runproc.communicate()[ 0 ]
    print scp_dist
    os.system( scp_dist )


if __name__ == '__main__':
    if sys.argv[1] == '-h':
        print help
    else:
        set_server()
        make_dist()
