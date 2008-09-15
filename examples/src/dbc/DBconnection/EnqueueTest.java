package dbc.opensearch.components.processqueue;

import java.sql.*;
import oracle.jdbc.driver.OracleDriver;

import org.apache.log4j.Logger;

/**
 * \brief Enqueue is the threadsafe part of ProcessQueue, which
 * accepts fedoraHandles and puts the on the queue.
 * Enqueue is implemented through a database and handles nothing but
 * the connection, thread issues and buffering.
 */
public class EnqueueTest {

    /**
     * Databasedriver
     */
    private static String driver; /** \todo: should be in a config file */

    /**
     * Url to database
     */
    private static String url; /** \todo: should be in a config file */

    /**
     * UserID to log into database
     */
    private static String userID; /** \todo: should be in a config file */

    /**
     * password to log into database
     */

    private static String passwd; /** \todo: should be in a config file */

    private static Connection con = null;
   
    private Statement stmt;

    /**
     *  Log
     */
    private static final Logger log = Logger.getLogger(EnqueueTest.class);
   
    

    /**
     * Constrcutor for the Engueue class. Creates an instance of
     * Enqueue, with a fedorahandle, pointing to the dataobject in the
     * Opensearch repository. If the fedorahandle conforms, and the
     * database information resolves, the fedorahandle is pushed to
     * the queue, and ready for processing.
     * \see dbc.opensearch.components.pti
     * @params fedorahandle: a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     */
    public EnqueueTest( String fedoraHandle ) throws Exception
    {

        //throw new UnsupportedOperationException( "method not implemented" );
        /** \todo: Read from config into variables: driver, url, userID, passwd */
        driver = "oracle.jdbc.driver.OracleDriver";
        url    = "jdbc:oracle:thin:tora1.dbc.dk:1521";
        userID = "shm";
        passwd = "shm";

        /** \todo: check that the input conforms to a fedoraHandle (how?)*/

        // establish databaseconnection
        con = establishConnection();
        
        // 30: make queueID

        ResultSet rs = stmt.executeQuery("select QUEUEID, FEDORAHANDLE from PROCESSQUEUE");

        while (rs.next()){
            int id = rs.getInt("QUEUEID");
            String handle = rs.getString("FEDORAHANDLE");
            System.out.println(id+" "+handle);
        }





        // 30: make queueID
        // 40: write fedoraHandle and queueID to database
        // 50: Close database connection
        


    }

    private static Connection establishConnection() throws Exception
    {

        try {
            Class.forName(driver);

        } catch(java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
        }

        try {
            con = DriverManager.getConnection(url, userID, passwd);
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        return con;


    }


    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        EnqueueTest q = new EnqueueTest("testhandle");
    }

}
