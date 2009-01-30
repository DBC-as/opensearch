package dk.dbc.opensearch.common.pluginframework;

/**
 * The PluginId type handles information about plugins.
 *
 */

/**
 * PluginID
 */
public class PluginID {

    private String submitter;
    private String format;
    private String task;

    /**
     *
     */
    public PluginID( String submitter, String format, String task ) {
        this.submitter = submitter;
        this.format = format;
        this.task = task;
    }

    public int getPluginID(){
        String hashSubject = submitter+format+task;
        return hashSubject.hashCode();
    }

    public String getPluginSubmitter(){
        return submitter;
    }
    public String getPluginFormat(){
        return format;
    }
    public String getPluginTask(){
        return task;
    }
}
