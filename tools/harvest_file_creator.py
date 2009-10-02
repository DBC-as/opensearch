import getopt
import os
import sys

def main(argv):
    path    = "."
    lang    = "dk"

    try:
        opts, args = getopt.getopt(argv, "hp:", ["help", "path="])
    except getopt.GetoptError:
        usage()
        sys.exit(2)

    for opt, arg in opts:
        if opt in ("-h", "--help"):
            usage()
            sys.exit()
        elif opt in ("-p", "--path"):
            path = arg

    # Check whether path exists, and whether it is a dir or a file:
    if ( not os.path.exists( path ) ):         
        print( path + " does not exist." )
        sys.exit(2);
    if ( not os.path.isdir( path ) ):
        print( path + " is not a directory." )
        sys.exit(2);

    # find all submitters and formats and write them to independent files
    # using create_addi_file:
    for submitter in os.listdir( path ):
        for format in os.listdir( path + "/" + submitter ):
            new_path = path + "/" + submitter + "/" + format
            # test whether new_path contains a dir:
            if ( not os.path.isdir( new_path ) ):
                 continue
            create_addi_file( submitter, format, lang, new_path )


def create_addi_file( submitter , format , lang , path ):
    """Creates an addi file with name: \"submitter_format_lang.addi\" from the files in new_path"""
    
    # Create xml:
    # This referencedata xml is the same for all the elements in the addi-file.
    xml = """<?xml version="1.0" encoding="UTF-8" ?>
<referencedata> 
<info submitter=\"""" + submitter + """\" format=\"""" + format + """\" lang=\"""" + lang + """\"/>
</referencedata>
"""

    # output filename:
    filename = submitter + "_" + format + "_" + lang + ".addi"

    # open file for writing:
    outfile = open( filename, "w" )

    xml_length = len(xml)

    for file in os.listdir( path ):
        if ( os.path.isdir( file ) ):
            continue
        # open infile and read content:
        infile = open( path + "/" + file , "r" )
        content = infile.read()
        infile.close()

        # write referencedata xml:
        outfile.write( str( xml_length ) )
        outfile.write( "\n" )
        outfile.write( xml )
        outfile.write( "\n" )

        # write content to outfile:
        content_length = len(content)
        outfile.write( str( content_length ) )
        outfile.write( "\n" )
        outfile.write( content )
        outfile.write( "\n" )

    outfile.close()



def usage():
    print("""
The """ + str(sys.argv[0]) + """ creates addi files for use in an ESbase from a FileHarvester directory structure. 
The FileHarvester directory structure must have this layout:

path/submitter/format/files 

Where path is the path given with the -p option (see below).
 

Options for harvest_file_creator:

  -h, --help:    This help text.
  -p, --path:    Path to directory containing FileHarvester directory structure. Default is current dir.
""")


if __name__ == "__main__":
    main(sys.argv[1:])
