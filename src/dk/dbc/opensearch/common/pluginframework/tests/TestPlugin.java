package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.IPluggable;

public class TestPlugin implements IPluggable{

    private String pluginID;;

    public TestPlugin(){}

    public String getPluginID(){
        return pluginID;
    }
    public String getTaskName(){
        return "testtask";
    }

}