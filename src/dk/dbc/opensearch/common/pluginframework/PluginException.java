package dk.dbc.opensearch.common.pluginframework;

import java.lang.Exception;

/**
 * Exception thrown in case of plugin processing or calculation errors. All
 * exceptions that are raised during plugin execution are wrapped in a
 * PluginException. In this way the pluginframework only exposes one kind of
 * exception. The wrapped (original) exception can be accessed by the
 * getException() method.
 */
public class PluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4451067896581886657L;
	private Exception e;
	private String msg;

	/**
	 * Constructor wrapping the original exception.
	 * 
	 * Constructs the Exception with null as the message.
	 * 
	 * @param e
	 *            The originating exception
	 */
	public PluginException(Exception e) {
		this.e = e;
		this.msg = null;

	}

	/**
	 * Constructor with wrapped exception and message explaining the cause from
	 * the plugin point of view.
	 * 
	 * @param msg
	 *            The message to annotate the PluginException from the Catch of
	 *            the Exception e
	 * @param e
	 *            The original Exception that was caught
	 */
	public PluginException(String msg, Exception e) {
		this.msg = msg;
		this.e = e;
	}

	/**
	 * Constructor for creating an Exception that origins from a plugin
	 * 
	 * @param msg
	 *            The reason for the throwing of the Exception
	 */
	public PluginException(String msg) {
		this.msg = msg;
		this.e = null;
	}

	/**
	 * Returns the wrapped (original) exception that was caught inside the
	 * plugin. Returns null if the PluginException is the originating exception
	 * 
	 * @return Exception the wrapped exception
	 */
	public Exception getException() {
		return e;
	}

	/**
	 * Returns the message that was given from the plugin at the cathing of the
	 * original exception. Returns null if no message was given at the time of
	 * the catch.
	 */
	public String getMessage() {
		return msg;
	}
}
