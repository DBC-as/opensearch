package dk.dbc.opensearch.common.os.tests;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestSuite;


@RunWith(Suite.class)

@Suite.SuiteClasses(
    {
        FileFilterTest.class,
        FileHandlerTest.class,
        XmlFileFilterTest.class
    }
)

public class OSTestSuite  
{
    // Leave class empty!
}