package dbc.opensearch.tools.tests;


import java.io.IOException;

import dbc.opensearch.tools.FedoraTools;

import dbc.opensearch.components.datadock.CargoContainer;

import org.junit.*;
import static org.easymock.classextension.EasyMock.*;

import org.apache.log4j.Logger;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;


public class FedoraToolsTest
{
    Logger log = Logger.getLogger( "FedoraToolsTest" );


    CargoContainer cargo;


    @Before public void SetUp() 
    {
        cargo = createMock ( CargoContainer.class );
    }


    @After public void TearDown()
    {

    }


    @Test public void constructFoxmlTest() throws IOException, MarshalException, ValidationException
    {
        byte[] b = FedoraTools.constructFoxml( cargo, "", "", "" );
    }    
}