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
    print len( rows )
    return len( rows )

process_queue_count()