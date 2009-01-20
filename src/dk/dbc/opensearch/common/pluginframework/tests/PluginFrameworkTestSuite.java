package dk.dbc.opensearch.common.pluginframework.tests;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestSuite;


@RunWith(Suite.class)

@Suite.SuiteClasses(
    {
        PluginFinderTest.class,
        PluginLoaderTest.class
    }
)

public class PluginFrameworkTestSuite  
{
    // Leave class empty!
}