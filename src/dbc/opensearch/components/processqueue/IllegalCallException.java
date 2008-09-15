package dbc.opensearch.components.processqueue;

/**
 * Exception used i processqueue component
 */    

public class IllegalCallException extends Exception {
	IllegalCallException() { 
        super(); 
    }
	IllegalCallException(String s) { 
        super(s); 
    }
}
