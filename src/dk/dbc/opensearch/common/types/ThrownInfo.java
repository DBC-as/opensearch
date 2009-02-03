package dk.dbc.opensearch.common.types;

/**
 * \brief A class for handling throwables and additional information 
 * \package types
 * This is a class for wrapping and transporting an exception that need to be 
 * handed up through the system with some additional information than the 
 * stacktrace and message.
 * This class is made to facilitate the need of the 
 * dk.dbc.opensearch.common.pluignframework.PluginResolverException class to 
 * have a containerclass for a Throwable and some additional information.
 */
public class ThrownInfo {
    Throwable theThrown;
    String info;
    
    /**
     * the public constructor that sets the fields
     * @param theThrown, the Throwable that shall be contained
     * @param info, the additional info about the Throwable
     */
    public ThrownInfo( Throwable theThrown , String info ) {
    
        this.theThrown = theThrown;
        this.info = info; 
  }
    /**
     * returns the Throwable
     * @return theThrown
     */
    public Throwable getThrowable(){
        return theThrown;
    } 
    /**
     * returns the additional information about the Throwable
     * @return info
     */
    public String getInfo(){
        return info;
    }
}