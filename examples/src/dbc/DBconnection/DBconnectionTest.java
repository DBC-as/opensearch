package dbc.DBconnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.driver.OracleDriver;


/**
 * JDBC test.
 * Tester om der kan skabes forbindelse til et oracle.
 * Husk at sætte variablene, userid, password og url
 */
public class DBconnectionTest
{
    static String userid="shm";
    static String password = "shm";
    static String url = "jdbc:oracle:thin:tora1.dbc.dk:1521";

    static Connection con = null;
    public static void main(String[] args) throws Exception {

        Connection con = getOracleJDBCConnection();

        if(con!= null){
            System.out.println("Got Connection.");

        }else{
            System.out.println("Could not Get Connection");
        }
    }

    public static Connection getOracleJDBCConnection() throws Exception{

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch(java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
        }

        try {
            con = DriverManager.getConnection(url, userid, password);
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }

        return con;
    }


}
