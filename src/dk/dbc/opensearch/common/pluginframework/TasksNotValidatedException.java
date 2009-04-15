package dk.dbc.opensearch.common.pluginframework;

import java.util.Vector;

/**
 * 
 */
public class TasksNotValidatedException extends Exception {
    Vector<String> exceptionVector;
    String message;
  /**
   * 
   */
    public TasksNotValidatedException( Vector<String> exceptionVector, String message) {
      this.exceptionVector = exceptionVector;
      this.message = message;
  }
    public String getMessage(){
        return message;
    }

    public Vector<String> getExceptionVector(){
        return exceptionVector;
    }
}