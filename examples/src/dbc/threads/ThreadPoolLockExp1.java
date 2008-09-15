package dbc.threadexamples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//shall show/try locks and threadpools 
public class ThreadPoolLockExp1 {
	public static void main(String[] args){
		ObjectWithLock OWL = new ObjectWithLock();
		ReadWriteThread thread1 = new ReadWriteThread("RWThread1", OWL);
		ReadWriteThread thread2 = new ReadWriteThread("RWThread2", OWL);
		ReadWriteThread thread3 = new ReadWriteThread("RWThread3", OWL);
		
		ExecutorService ThreadExecutor = Executors.newFixedThreadPool(3);
		
		ThreadExecutor.execute(thread1);
		ThreadExecutor.execute(thread2);
		ThreadExecutor.execute(thread3);
		
		System.out.print("threads started\n");
		ThreadExecutor.shutdown();
		System.out.print("shutting down worker threads\n");
		System.out.print("main terminates\n");	}
}
