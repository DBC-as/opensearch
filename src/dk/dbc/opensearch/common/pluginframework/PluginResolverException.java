/**
 * \file PluginResolverException 
 * \brief
 * \package pluginframework
 */
package dk.dbc.opensearch.common.pluginframework;

import java.util.Vector;
import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;
/**
 * This class is a custom Exception for handling the many exceptions that can be 
 * thrown from the PluginResolvers components that not nessecarily should halt the 
 * executing. It therefore contains a Vector of <Pair <Throwable, String>> 
 * that can be examined where the PluginResolver is being called from.
 * To get the class of the Exception from the Throwable object call getClass()
 * The paired String tells what object the exception is concerned with or caused by.
 *      
 */
public class PluginResolverException extends Exception {
    Vector<Pair<Throwable, String>> exceptionVector;
    String message;

    /**
     * @param exceptionVector is the Vector containing the pairs of Throwables 
     * and what caused the Throwable.
     * @param String, message is the general message about the Exception, stating
     * what the collection of Throwables are regarding. 
     */
    public PluginResolverException( Vector<Pair<Throwable, String>> exceptionVector, String message ) {
        this.exceptionVector = exceptionVector;
        this.message = message;
    }

    /**
     * Constructor for sending a single message when the flow of the 
     * PluginResolvers components is as expected and no Exceptions where 
     * caused, but there are values that are not computed or retrived as 
     * expected. The exceptionVector will be null when the exception is 
     * constructed this way.
     * @param String, message is the general message about the Exception, stating 
     * what the collection of Throwables are regarding. 
     */
    public PluginResolverException( String message ) {
        this.exceptionVector = null;
        this.message = message;
    }

    /**
     * The standard method for retrieving the overall information about 
     * the Exception.  
     * @return String, the overall information about the Exception.
     */
    public String getMessage(){
        return message;
    }

    /**
     * The method for retrieving the Vector containing the Throwables and 
     * eachs paired information. The returned Vector should allways be 
     * checked for being null before used.
     * @return Vector<Pair<Throwable, String>> the Vector with the Throwables 
     * and information about them.
     */
    public Vector<Pair<Throwable, String>> getExceptionVector(){
        return exceptionVector;
    }
}