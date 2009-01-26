package dk.dbc.opensearch.common.pluginframework;

/**
 * 
 */
public class NotValidatedException extends Exception {
    Vector<String> pluginsNotFoundVector;
  /**
   * 
   */
  public NotValidatedException( Vector<String> notFound) {
    pluginsNotFoundVector = notFound
  }
    public Vector<String> getMessage(){
        return pluginsNotFoundVector;
    }
}