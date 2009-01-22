#! /usr/bin/python

import sys
import os
import fnmatch


def writeTextToFile( dir, className, fileTxt ):
    fileName = dir + '/' + className + '.java'
    f = open( fileName, 'w')
    f.write(fileTxt)
    f.close()



def createTestSuiteFileText( dir, className, testNames ):
    fileTxt = str()
    tmp = str()
    
    # Write package name
    tmp = dir[dir.index( 'dk' ):]
    tmp = tmp.replace( '/', '.' )

    doubleNL = '\n\n'
    fileTxt += 'package ' + (tmp) + ';' + doubleNL

    # Write annotation
    fileTxt += 'import org.junit.runner.RunWith;\n'
    fileTxt += 'import org.junit.runners.Suite;\n' + doubleNL
    fileTxt += '@RunWith(Suite.class)' + doubleNL
    fileTxt += '@Suite.SuiteClasses(' + '\n    {\n'

    while len(testNames) > 1:
        fileTxt += '\t\t' + testNames.pop() + '.class,\n'
        
    fileTxt += '\t\t' + testNames.pop() + '.class\n'

    fileTxt += '    }\n)\n'

    # Write class
    fileTxt += 'public class ' + className + '\n{\n'
    fileTxt += '\t// Leave class empty!\n}\n'

    return fileTxt



def match( ptr, dir, names ):
    tests = list()
    fileTxt = str()

    # Consider only /tests folders
    if fnmatch.fnmatch( dir, '*tests' ):
        for name in names:
            if name.endswith( ptr ): 
                # Names for suite classes
                tests.append( name.replace( '.java', '' ) )

        # Create suite classes if '*Test.java' (ptr) files exist
        if len( tests ) > 0:
            projectName = str()

            # Process 'tests' folders
            if dir.endswith('/tests'):
                projectName = dir.replace( '/tests', '' ).split('/').pop()
                className = projectName.upper() + 'TestSuite'
                fileTxt = createTestSuiteFileText( dir, className, tests )
                writeTextToFile( dir, className, fileTxt )

    
    
sourceDir = os.getcwd()
#if sourceDir.endswith( 'tools'):
if os.path.basename(sourceDir) == 'tools':
     sourceDir = '../src/dk/dbc/opensearch'
     os.path.walk(sourceDir, match, 'Test.java')
     print 'Suite files successfully created!'
else:
    print '<ERROR>'
    print 'Current directory should be ~/.../opensearch/trunk/tools'



