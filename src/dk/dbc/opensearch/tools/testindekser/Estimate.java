/**
 * \file Estimate.java
 * \brief The Estimate class
 * \package testindexer;
 */

package dk.dbc.opensearch.tools.testindexer;


import dk.dbc.opensearch.common.statistics.IEstimate;

public class Estimate implements IEstimate{
    public Estimate(){}
    public float getEstimate( String mimetype, long length){
        return 0f;
    }
    
    public void updateEstimate( String mimeType, long length, long time ){}
}