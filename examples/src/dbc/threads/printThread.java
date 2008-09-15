package dbc.threadexamples;
import java.util.Random;

public class printThread implements Runnable{
	private int sleepTime; 
    private String threadName; 
    private static Random generator = new Random();
	        
	// naming thread
    public printThread(String name){
    	threadName = name; 
	        //  0-10 seconds sleeptime
	    sleepTime = generator.nextInt( 10000 );
	} 
	    
	 //code executed while running
	public void run(){
		 try //sleep for sleepTime seconds
		 {
			 System.out.print(threadName + " going to sleep for " + sleepTime + " milliseconds.\n");   
	         Thread.sleep(sleepTime); // makes the thread wait for sleepTime and let other threads run 
	     }     // if interrupted print stack trace
	     catch ( InterruptedException exception ){
	    	 exception.printStackTrace();
	     }
	     
	     System.out.print(threadName +" done sleeping. Terminating thread\n");
	} 

}
