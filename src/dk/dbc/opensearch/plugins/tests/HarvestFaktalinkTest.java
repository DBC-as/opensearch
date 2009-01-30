package dk.dbc.opensearch.plugins.tests;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.plugins.HarvestFaktalink;

import java.io.IOException;

//import static org.easymock.classextension.EasyMock.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.Test;




public class HarvestFaktalinkTest
{
	private String path = "faktalink.xml";
	
	//private HarvestFaktalink mockHarvestFaktaLink;
	
	
	@Before 
	public void SetUp()
	{
		//mockHarvestFaktaLink = createMock( HarvestFaktalink.class );
	}
	
	
	@After
	public void TearDown()
	{
		//reset( mockHarvestFaktaLink );
	}
	
	
    @Ignore("Just because")
    @Test
    public void testSubmitter() throws IllegalArgumentException, NullPointerException, IOException
    {
    	String submitter = "submitter";
    	
    	//mockHarvestFaktaLink = new HarvestFaktalink( "id", path, submitter, "format" );
    	//HarvestFaktalink hfl = new HarvestFaktalink( "id", path, submitter, "format" );
    	//CargoContainer cc = mockHarvestFaktaLink.getCargoContainer();
    	// CargoContainer cc = hfl.getCargoContainer();
    	// boolean submitterChecked = cc.checkSubmitter( submitter );
    	// assertTrue( submitterChecked );
    }
}