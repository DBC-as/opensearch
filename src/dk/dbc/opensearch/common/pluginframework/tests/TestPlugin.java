package dk.dbc.opensearch.common.pluginframework.tests;

import java.io.InputStream;

import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginID;
import dk.dbc.opensearch.common.pluginframework.PluginType;


public class TestPlugin implements IPluggable
{
    private PluginID pluginID;;

    public TestPlugin(){}

    public void init(PluginID pluginId, InputStream data) 
	{
		// TODO Auto-generated method stub		
	}
    
    
    public PluginID getPluginID()
    {
        return pluginID;
    }
    
    
    public PluginType getTaskName()
    {
        return PluginType.ANNOTATE;
    }
}