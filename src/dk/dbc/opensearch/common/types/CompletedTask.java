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
public class CompletedTask<V> 
{ 
    private FutureTask future;
    private V result;

    
    /**
     * Constructor of the CompletedTask instance.
     * 
     * @param future the FutureTask of the completed task
     * @param result the result of the completed task
     */
    public CompletedTask( FutureTask future, V result) 
    {
        this.future = future;
        this.result = result;
    }
   
    
    /**
     * Gets the future
     * 
     * @return The future
     */
    public FutureTask getFuture()
    {
        return future;
    }

    
    /**
     * Gets the result
     * 
     * @return The result
     */
    public V getResult()
    {
        return result;
    }

    
    /**
     * Sets the future of the completedTask
     * 
     * @param The future
     */    
    public void setFuture( FutureTask future )
    {
            this.future = future;
    }

    
    /**
     * Sets the result of the completedTask
     * 
     * @param The result
     */
    public void setResult( V result )
    {
        this.result = result;
    }
}
