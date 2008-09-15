package dbc.threadexamples;

//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class ReadWriteThread implements Runnable {
	String name = "";
	ObjectWithLock OWL;
	Random generator = new Random();
	int sleepTime = generator.nextInt(5000);
	int i = 0;
	
	public ReadWriteThread(String name , ObjectWithLock ObjectWL){
		OWL = ObjectWL;
		this.name = name;
	}
	public void run(){
		System.out.print(name +" trying to read the Object\n");
		OWL.objectLock.lock();
		try {
			System.out.print(name+" read "+OWL.getMessage()+ " from OWL\n");
		} finally {
		    OWL.objectLock.unlock();
		} 
		
		while(i < 3){
			i++;
			try{
				Thread.sleep(sleepTime);
			}catch ( InterruptedException exception ){
				exception.printStackTrace();
				}
			OWL.objectLock.lock();
			try {
				OWL.setMessage(name);
				System.out.print("message set to " + OWL.getMessage() + " " + i + " time\n" );
				try{
					Thread.sleep(sleepTime);
				}catch ( InterruptedException exception ){
					exception.printStackTrace();
				}
				System.out.print(name + " read " + OWL.getMessage() + " " + i + " time\n");
			} finally {
			    OWL.objectLock.unlock();
			}
		}
		System.out.print(name + " done\n");
	}
}
