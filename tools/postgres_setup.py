#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import os
import subprocess


src_dir = os.getcwd()


def setup():
    global src_dir

    if src_dir.endswith("tools"):
        src_dir = src_dir.replace("tools", "admin" )

    runproc = subprocess.Popen( 'psql -f teardown.sql', shell=True, cwd=src_dir ) 
    runproc.communicate()[ 0 ]
    runproc = subprocess.Popen( 'psql -f init.sql', shell=True, cwd=src_dir )    
    runproc.communicate()[ 0 ]

if __name__ == "__main__":
    setup()

