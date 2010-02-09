/*   
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * \file
 * \brief
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.TargetFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.ObjectIdentifier;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.javascript.JavaScriptWrapperException;
import dk.dbc.opensearch.common.types.OpenSearchTransformException;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
//import org.mozilla.javascript.xmlimpl.XML;

import org.apache.log4j.Logger;

/**
 * !! This code is a draft !! 
 *
 * This plugin handles the matching of objects in the fedora objectrepository 
 * with the special workobjects
 * In contrast to other plugins of the RELATION type it uses more than 1 javascript
 * to handle the business logic
 */
public class MarcxchangeWorkRelation_2 implements IRelation 
{
    private static Logger log = Logger.getLogger( MarcxchangeWorkRelation_2.class );

    private PluginType pluginType = PluginType.RELATION;

    private IObjectRepository objectRepository;

    private SimpleRhinoWrapper rhinoWrapper;
    private DocumentBuilder builder;
    private Document doc;    

    public MarcxchangeWorkRelation_2() 
    {
    }

    @Override
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       try 
       {
           builder = factory.newDocumentBuilder();
       }
       catch( ParserConfigurationException pce )
       {
           String error = String.format( "Caught error while trying to instantiate documentbuilder '%s'", pce.getMessage() );
       log.error( error );
       throw new PluginException( error, pce );
       }
       List<InputPair<TargetFields, String>> searchPairs = getSearchPairs( cargo );
        // pidList = getWorkList( searchPairs );
        // thePid = checkMatch( cargo, pidList );
        // if( pid == null )
        // { pid = createAndStoreWorkobject( cargo ) }
        // relate cargo and workobject
    
        return cargo;
    }

    /**
     * method that generates the list containing the fields to look in and the 
     * corresponding values to match with
     * @param cargo, the CargoContianer to generate searchpairs for
     * @return a list of InputPairs containing a serachfield and the value to match 
     */
    private List<InputPair<TargetFields, String>> getSearchPairs( CargoContainer cargo ) throws PluginException
    {
        List<InputPair<TargetFields, String>> searchList = new ArrayList<InputPair<TargetFields, String>>();
        //calls a javascript, with the dc-stream of the cargo as argument, 
        //that returns an xml with the pairs to generate
        //start with dummy xml on the javascript-side
        try
        {
            rhinoWrapper = new SimpleRhinoWrapper( "GenerateWorkSearchPairs.js" );
        }
        catch( JavaScriptWrapperException jswe )
        {
            String errorMsg = "An exception occured when trying to instantiate the NaiveJavaScriptWrapper";
	    log.fatal( errorMsg, jswe );
	    throw new PluginException( errorMsg, jswe );
        }
        // get the DC-Stream
        DublinCore theDC = (DublinCore)cargo.getMetaData( DataStreamType.DublinCoreData );
        //create outputstream
        ByteArrayOutputStream dcOutputStream = new ByteArrayOutputStream();

        // serialize the DC into an outputStream
        try
        {
            theDC.serialize( dcOutputStream, null);
        }
        catch( OpenSearchTransformException oste )
        {
            String msg = "Exception occured while trying to serialize the dc-stream";
            log.error( msg, oste );
            throw new PluginException(  msg, oste );
        }
        byte[] strippedDC = E4XXMLHeaderStripper.strip( dcOutputStream.toByteArray() );
        String dcString = new String ( strippedDC );
        System.out.println( String.format( "the dc-string: %s", dcString ) );

        String[] pairsXml;
        try
        {        
            pairsXml = (String[])rhinoWrapper.run( "generateSearchPairs", dcString );
        }
        catch( JavaScriptWrapperException jswe )
        {
            String msg = "Exception while running generateSearchPairs";
            log.error( msg, jswe );
            throw new PluginException( msg, jswe );
        } 
        System.out.println( String.format( "The pairsXml: %s",pairsXml ) );
        //doc = builder.parse( new InputSource( new ByteArrayInputStream( pairsXml.getBytes() ) ) );
        //go through the xml and create the pairs and put them on the list

        return searchList;
    }
    /**
     * method that finds the workobjects that match the cirterias specified in 
     * the searchList
     */
    private List<PID> getWorkList( List<InputPair<TargetFields, String>> searchList )
    {
        //call getIdentifiers on the object repository
        //return the resulting list of PIDs

        return null;
    }
    /**
     * method that checks if the cargo has a match with an existing work object
     * the candidates are each checked by a method in javascript
     * @param cargo, the cargoContainer to find a workobject for
     * @param pidList, the list of pids to chack for matches
     * @return the pid of the matching work object, null if none is found
     */
    private PID checkMatch( CargoContainer cargo, List<PID> pidList )
    {
        //get the xml for the cargo
        //for each pid in the list 
        //get the objects xml
        //call the match test with the xmls until a match occurs
        //return the pid for the matching workobject
        //if no match occur, return null
        return null;
    }

    /**
     * method that creates a workobject from a CargoContainer based upon 
     * the fields in the searchList. Thje workobject is stored in the objectrepository
     * @param cargo, the post to creare a workobject from
     * @return the pid of the new workobject
     */
    private PID createAndStoreWorkobject( CargoContainer cargo )
    {
        //get the cargos xml
        //call the javascript that creates a workobject xml from a cargo xml
        //use the xml to create the work object
        //store it in the objectrepository
        //return the PID of the new workobject
        return null;
    }

    /**
     * method that relates a post to a work object and the inverse
     * @param cargoPid, the pid of the post 
     * @param workPid, the pid of the work
     */
    private void relatePostAndWork( PID cargoPid, PID workPid )
    {
        //call the addRelation method on the objectRepository both ways
    }

    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }

    @Override
    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
    }
}