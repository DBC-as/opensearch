package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

public class HarvestFaktalink implements IHarvestable
{
	Logger log = Logger.getLogger( "HarvestFaktalink" );
	String xmlFilePath = "xml/faktalink_kanon.xml";
	
	private String path;
	private String submitter;
	private String format;
	
	
	public HarvestFaktalink( String path, String submitter, String format )
	{
			this.path= path;
			this.submitter = submitter;
			this.format = format;
	}
	
	
    public void init()
    {
    	
    }


    public String getPluginTask()
    {
        return "harvest";
    }


    public String getPluginFormat()
    {
        return format;
    }


    public String getPluginSubmitter()
    {
        return submitter;
    }

    public CargoContainer getCargoContainer() throws FileNotFoundException
    {
    	return createCargoContainerFromFile();
    }

    public String getTaskName(){
        return "harvest";
    }


    private CargoContainer createCargoContainerFromFile() throws FileNotFoundException
    {
    	String mimetype = "";
    	String lang = "";
    	CargoContainer cc = null;
    	
    	try 
    	{
    		File file = new File( path );
    		InputStream data = new FileInputStream( file );
    		
    		cc = new CargoContainer( data, mimetype, lang, submitter, format );
    		
    	}
    	catch ( FileNotFoundException ex )
    	{
    		String stackTrace = ex.getStackTrace().toString();
    		log.fatal( "File not found in HarvestFaktalink!" );
    		log.fatal( "Error message:" );
    		log.fatal( stackTrace );
    		throw new FileNotFoundException( stackTrace );
    	}
    	catch (IllegalArgumentException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NullPointerException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return cc;
        
        //CargoContainer cc = new CargoContainer(null, null, null, null);
        //cc = readXmlFile( cc );
        //return cc;
    }

/*
    private CargoContainer readXmlFile( CargoContainer cc )
    {
        // 10 read faktalink xml file
    	File faktaLink = new File( xmlFilePath );
    	
    	InputStream is = null;
    	StringBuilder sb = new StringBuilder();

        try 
        {
        	is = new FileInputStream( faktaLink );
        	
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));            

            String line = null;
            while ( ( line = reader.readLine() ) != null )
            {
                sb.append( line );
            }

        	// dispose all the resources after using them.
        	is.close();

        } 
        catch (FileNotFoundException e) 
        {
        	e.printStackTrace();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
        
        // 20 add to cc
        String str = sb.toString();

        // 30 return cc
        return cc;
    }
*/

	@Override
	public String getPluginID()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getFormat() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getSubmitter() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setFormat() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setSubmitter() {
		// TODO Auto-generated method stub
		
	}
}











