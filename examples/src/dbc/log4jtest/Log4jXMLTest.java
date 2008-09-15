
import org.apache.log4j.*;

import java.io.ByteArrayInputStream;

/**
 * 
 */
public class Log4jXMLTest {
    
    private static final Logger log1 = Logger.getRootLogger();

    public static void main(String[] args)
    {
        log1.info( "test fra main" );
        
        ByteArrayInputStream bais = new ByteArrayInputStream( new byte[6000] );
        
        log1.info( String.format( "length of bais=%d", bais.available() ) );
        log1.info( String.format( "length of bais=%d", bais.available() ) );
    }
}