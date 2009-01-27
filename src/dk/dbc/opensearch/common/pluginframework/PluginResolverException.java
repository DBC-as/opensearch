package dk.dbc.opensearch.common.pluginframework;

import java.util.Vector;
import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;
/**
 *
 */
public class PluginResolverException extends Exception {
    Vector<Pair<Throwable, String>> exceptionVector;
    String message;

    /**
     *
     */
    public PluginResolverException( Vector<Pair<Throwable, String>> exceptionVector, String message ) {
        this.exceptionVector = exceptionVector;
        this.message = message;
    }

    public PluginResolverException( String message ) {
        this.exceptionVector = null;
        this.message = message;
    }
    public String getMessage(){
        return message;
    }

    public Vector<Pair<Throwable, String>> getExceptionVector(){
        return exceptionVector;
    }
}