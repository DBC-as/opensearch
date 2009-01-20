package dk.dbc.opensearch.common.fedora.tests;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestSuite;


@RunWith(Suite.class)

@Suite.SuiteClasses(
    {
        FedoraHandlerTest.class,
        FedoraToolsTest.class
    }
)

public class FedoraTestSuite  
{
    // Leave class empty!
}