package dk.dbc.opensearch.plugins;

import static org.junit.Assert.*;
import org.junit.Test;
import dk.dbc.opensearch.plugins.MarcxchangeWorkRelation_1;

public class MarcxchangeWorkRelationTest
{      
    
    @Test
    public void testNormalizeString( ) throws Exception {       
        
       String es = new String("harry potter");
       String res = MarcxchangeWorkRelation_1.normalizeString("Harry Potter");
       assertEquals(es, res);
       
       es = new String("jacobfisk");
       res = MarcxchangeWorkRelation_1.normalizeString("jacob~'fisk");
       assertEquals(es, res);       
     }
}
