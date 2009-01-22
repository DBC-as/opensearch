package dk.dbc.opensearch.common.os.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)

@Suite.SuiteClasses(
    {
		FileHandlerTest.class,
		FileHandlerStaticCallTest.class,
		XmlFileFilterTest.class,
		FileFilterTest.class
    }
)
public class OSTestSuite
{
	// Leave class empty!
}
