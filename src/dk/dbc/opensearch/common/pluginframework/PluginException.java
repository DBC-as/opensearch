package dk.dbc.opensearch.common.pluginframework;

import java.lang.Exception;
import java.lang.Throwable;

/**
 * Exception thrown in case of plugin processing or calculation
 * errors. All exceptions that are raised during plugin execution are
 * wrapped in a PluginException. In this way the pluginframework only
 * exposes one kind of exception. The wrapped (original) exception can
 * be accessed by the getException() method.
 */
public class PluginException extends Exception{

    private Throwable e;
    private String msg;

    /**
     * default constructor. Please avoid using this, as a call to this
     * contructor will effectively swallow the original Throwable.
     * 
     * Constructs the Exception with null as the message and an empty
     * Throwable
     */
    public PluginException() 
    {
        this.e = new Throwable();
        this.msg = null;
    }

    /**
     * Constructor wrapping the original exception.
     * 
     * Constructs the Exception with null as the message.
     */
    public PluginException( Throwable e )
    {
        this.e = e;
        this.msg = null;

    }

    /**
     * Constructor with wrapped exception and message explaining the
     * cause from the plugin point of view.
     */
    public PluginException( String msg, Throwable e )
    {
        this.msg = msg;
        this.e = e;
    }

    /**
     * Returns the wrapped (original) exception that was caught inside
     * the plugin
     * 
     * @return Throwable the wrapped exception
     */
    public Throwable getException()
    {
        return e;
    }

    /**
     * Returns the message that was given from the plugin at the
     * cathing of the original exception.
     */
    public String getMessage()
    {
        return msg;
    }
}
