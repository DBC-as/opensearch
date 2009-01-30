package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginID;

public class TestPlugin implements IPluggable{

    private PluginID pluginID;;

    public TestPlugin(){}

    public PluginID getPluginID(){
        return pluginID;
    }
    public String getTaskName(){
        return "testtask";
    }

}