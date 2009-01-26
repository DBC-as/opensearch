package dk.dbc.opensearch.common.pluginframework;

import java.util.Vector;
import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;
/**
 * 
 */
public class TasksNotValidatedException extends Exception {
    Vector<Pair> exceptionVector;
    String message;
  /**
   * 
   */
    public TasksNotValidatedException( Vector<Pair> exceptoinVector, String message) {
      this.exceptionVector = exceptionVector;
      this.message = message;
  }
    public String getMessage(){
        return message;
    }

    public Vector<Pair> getExceptionVector(){
        return exceptionVector;
    }
}