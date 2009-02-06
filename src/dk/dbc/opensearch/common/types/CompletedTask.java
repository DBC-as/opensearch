/**
 * \file CompletedTask.java
 * \brief The CompletedTask class
 * \package types;
 */

package dk.dbc.opensearch.common.types;

import java.util.concurrent.FutureTask;

/**
 * The purpose of the CompletedTask is to hold information about a
 * completed threadpooljob. it contains a futureTask representing the
 * job, and a float which is the return value of the job.
 */
public class CompletedTask {
 
    FutureTask future;
    float result;
 
    /**
     * Constructor of the CompletedTask instance.
     * 
     * @param future the FutureTask of the completed task
     * @param result the result of the completed task
     */
    public CompletedTask( FutureTask future, float result) {
        this.future = future;
        this.result = result;
    }
   
    /**
     * Gets the future
     * 
     * @return The future
     */
    public FutureTask getFuture(){
        return future;
    }

    /**
     * Gets the result
     * 
     * @return The result
     */
    public float getResult(){
        return result;
    }

    /**
     * Sets the future of the completedTask
     * 
     * @param The future
     */    
    public void setFuture( FutureTask future ){
            this.future = future;
    }

    /**
     * Sets the result of the completedTask
     * 
     * @param The result
     */
    public void setResult( float result ){
        this.result = result;
    }
}
