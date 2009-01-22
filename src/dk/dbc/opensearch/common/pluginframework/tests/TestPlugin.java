package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.IPluggable;

public class TestPlugin implements IPluggable{

    public TestPlugin(){}
    
    public void init(){}
    
    public String getPluginTask(){
        return "testTask";
    }
    
    public String getPluginSubmitter(){
        return "testSubmitter";
    }
    
    public String getPluginFormat(){
        return "testFormat";
    }
}