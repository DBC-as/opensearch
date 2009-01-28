package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.IPluggable;

public class TestPlugin implements IPluggable{

    public TestPlugin(){}
    
    public String getPluginID(){
        return "TestPluginID";
    }
}