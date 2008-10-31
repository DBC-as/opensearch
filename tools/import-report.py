import os
import os.path
import string
import sys
import time

def analyze_files( file_lst ):
    """
    Counts the number of import lines in a file.
    """
    
    import_lines = 0

    for file in file_lst:
        if not os.path.isfile( file ):
            print 'Couldnt find file :"'+file+'"'
        for line in open(file,'r'):
            if line[0:6]  == 'import':
                import_lines += 1
    return import_lines

#print analyze_files( src_files )


def compile_build( compile_command, builddir ):
    """
    Compiles the project
    """
    
    cur_dir = os.getcwd();

    compile_str = 'cd %s && %s && cd %s' % ( builddir, compile_command , cur_dir )
    #print compile_str
    stdin, stdout, stderr = os.popen3( compile_str )
    out = stdout.read()
    if out.rfind( 'BUILD FAILED' ) > 0:
        return False
    elif out.rfind( 'BUILD SUCCESSFUL' ) > 0:
        return True

def verify_imports( compile_command, builddir, file ):
    """
    Verifies the import statements in the argument file.  For each
    import statement the statement is commented out, and we try to
    build the project to determine if the import statement is
    necessary.  Returns a tuple where the first element is a list of
    wildcard imports, and the second list contains unnecessary imports.
    """

    imports_not_used = []

    if not os.path.isfile( file ):
        print 'Couldnt find file :"'+file+'"'
    file_handle = open(file)
    original_file = file_handle.readlines()
    org_file_str = string.join( original_file, '' )
    file_handle.close()

    imports = []
    wildcards = []

    for i, line in enumerate ( original_file ):
        if line[0:6]  == 'import':
            imports.append( i );
            
            if line[:-1][-2:] == '*;':
                wildcards.append( [i, line[:-1] ] )
    for importline in imports:

        test_file = original_file
                
        ### create new testfile with 1 specific importline commented
        #print 'checking %s' % test_file[importline]
        org_line = test_file[importline] 
        test_file[importline] = '//'+test_file[importline]
        test_file_str = string.join( test_file, '' )

        file_handle = open( file, 'w' )
        file_handle.write( test_file_str )
        file_handle.close()

        test_file[importline] = org_line
        
        if compile_build( compile_command, builddir ):
            imports_not_used.append( [ importline, org_line[:-1] ] )
    
    # re-establish original file
    file_handle = open( file, 'w' )
    file_handle.write( org_file_str )
    file_handle.close()
    
    return ( wildcards, imports_not_used )

def get_input():
    """
    Get input from user.
    """
    def ask():
        resp = raw_input( 'continue?[Y/n]' )
        return resp
    
    answer = ask()
    while answer != 'y' and answer != 'n' and answer != 'Y' and answer != 'N' and answer != '':
        #print answer
        answer = ask()

    if answer == 'n' or answer == 'N':
        return False
    else:
        return True

def create_file_list( directory ):
    """
    Descend recursively down through a directory and all find java
    source files, and return them in a list.
    """
    file_list=[]
    
    def f( arg,d,flst):
        for fl in flst:
            tmp_lst = fl.split( '.' )
            if len( tmp_lst ) >= 2 and tmp_lst[-1] == 'java':
                
                file_list.append( os.path.abspath( d + '/' + fl ) )
    os.path.walk( directory , f, [])
    
    return file_list

def usage(compile_command):
    """
    Help string
    """

    print """
    Used to test the import lines in java files.
    The first argument is the directory from where to call %s
    to build the application.
    Second argument is the file or directory of files to test

    """ % compile_command

def main( arg_lst ):
    """
    The main method of the script.  Firstly the command line
    parameters are parsed, and afterwards the given files are
    analyzed.
    """
    
    ### the compile command
    compile_command = 'ant compile-tests'

    ### Parse arguments

    if len(arg_lst) == 0 or arg_lst[0] in ( '-h', '--help' ):
        usage( compile_command )
        sys.exit(2)

    if not os.path.isdir( arg_lst[0] ):
        print 'the build directory %s does not exist' % arg_lst[0]
        sys.exit(2)

    build_directory = arg_lst[0]

    ### Create file list
    file_list = []

    for arg in arg_lst[1:]:
        if os.path.isfile( arg ):
            tmp_lst = arg.split( '.' )
            if len( tmp_lst ) >= 2 and tmp_lst[-1] == 'java':                
                file_list.append( os.path.abspath( arg ) )
        elif os.path.isdir( arg ):
            file_list += create_file_list( arg )
        else:
            print 'Cannot find the folder or file:\'%s\'' % arg
            sys.exit(2)

    if len( file_list ) == 0:
        print 'Found no files to analyze'
        sys.exit(2)
    #print file_list

    ### Verify build and record time for building project
    before = time.time()
    compile_build( compile_command, build_directory )
    build = after = time.time()
    build_time = after - before
    if not build:
        print 'couldn\'t build from dir %s' % build_directory
        sys.exit(2)

    builds = analyze_files( file_list )
    estimation = build_time * builds

    ### obtain confirmation
    print 'Found %d import lines in %d files.' % ( builds, len(file_list) )
    print 'Estimated time before finishing: %d seconds' % estimation
    
    if not get_input():
        print 'Exiting without doing anything... bye.'
        sys.exit(1)
    print '\n'

    ### analyze files
    before = time.time()

    for file in file_list:
        print '------------------------------------------------------------------------------------------------------------------------'
        print 'Examining %s' % file
        print '------------------------------------------------------------------------------------------------------------------------'
        
        wildcards, not_used = verify_imports(compile_command, build_directory, file )

        if len( wildcards ) > 0:
            print '\n  Found the following wildcard imports:'
            for w in wildcards:
                print '  %d  %s' % ( w[0], w[1] )
        if len( not_used ) > 0:
            print '\n  Project can compile without the following import lines:'
            for n in not_used:
                print '  %d  %s' % ( n[0], n[1] )
    build = after = time.time()
    build_time = after - before

    print '========================================================================================================================'
    print ' time: %d seconds' % build_time


### Starts the main method
if __name__ == "__main__":
    main( sys.argv[1:])
    
