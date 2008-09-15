package dbc.threadexamples;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import java.util.Random;


public class ObjectWithLock {
	String message = "empty";
	public Lock objectLock = new ReentrantLock();
	public ObjectWithLock(){
		
	}
	public void setMessage(String newMessage){
		message = newMessage;
	}
	public String getMessage(){
		return message;
	}
}
