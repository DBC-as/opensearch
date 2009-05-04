import psycopg2
import os

def process_queue_count():
    usern = os.environ.get( 'USER' )

    try:
        conn = psycopg2.connect( "dbname='%s' user='%s' password='%s'"%( usern, usern, usern))
    except:
        print "I am unable to connect to the database."
       
    cur = conn.cursor()
    try:
        cur.execute("""SELECT * from processqueue""")
    except:
        print "I can't SELECT from processqueue"
   
    rows = cur.fetchall()
    print "number of pending jobs: %s"%(len( rows ))

    try:
 	cur.execute( """SELECT processing FROM processqueue WHERE processing='Y'""")
    except:
	print "can't SELECT from processqueue"

    rows = cur.fetchall()
    print "number of jobs being processed: %s"%( len( rows) )

    try:
 	cur.execute( """SELECT * FROM notindexed""")
    except:
	print "can't SELECT from notindexed"

    rows = cur.fetchall()
    print "number of jobs not indexed: %s"%( len( rows) )

    return len( rows )

process_queue_count()
