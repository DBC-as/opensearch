package dbc.threadexamples;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ExecutorExp1 {
	public static void main(String[] args){
		
		System.out.print("Starting threads\n");
		printThread thread1 = new printThread("thread1");
		printThread thread2 = new printThread("thread2");
		printThread thread3 = new printThread("thread3");
		printThread thread4 = new printThread("thread4");
		printThread thread5 = new printThread("thread5");
		
		/*
		 *Creating a threadpool with less worker threads than the number 
		 *I give to the Executor. To see the handling. Saw that the tasks of 
		 *thread4 and thread5 are not given cpu time until the other tasks are 
		 *ended, even though they sleep. Note to self: so always make enough 
		 *threads in the threadpool cause it handles the cpu for u.  
		 */ 
		ExecutorService ThreadExecutor = Executors.newFixedThreadPool(3);
		
		ThreadExecutor.execute(thread1);
		ThreadExecutor.execute(thread2);
		ThreadExecutor.execute(thread3);
		ThreadExecutor.execute(thread4);
		ThreadExecutor.execute(thread5);
		System.out.print("threads started\n");
		ThreadExecutor.shutdown();
		System.out.print("shutting down worker threads\n");
		System.out.print("main terminates\n");
	}

}
