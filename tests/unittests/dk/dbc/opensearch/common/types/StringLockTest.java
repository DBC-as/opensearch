/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s, 
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
  
  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


package dk.dbc.opensearch.common.types;


import org.junit.*;
import static org.junit.Assert.*;


/**
 * @author jda
 *
 * This is a darn difficult class to test.  The strategy for the test
 * will be to do a blackbox-test based on the documentation/interface,
 * prefferbly using sequential tests. It may prove necessary to use
 * some threading to ensure correctness of locking between threads.
 * 
 * It should be noted, that these tests are nowhere complete, since no tests
 * for race-conditions are performed.
 */
public class StringLockTest 
{

    @Test( expected=IllegalStateException.class )
    public void testUnlockWithoutLock()
    {
	StringLock sl = new StringLock();
	sl.unlock("id_1");
    }


    @Test( expected=IllegalStateException.class )
    public void testLockWithNULL()
    {
	StringLock sl = new StringLock();
	sl.lock(null);
    }


    /**
     *  The following tests are performed sequentially if the
     *  name of the function is ending with "Seq".
     */ 

    @Test
    public void testSimpleLockUnlockSeq()
    {
	StringLock sl = new StringLock();
	sl.lock("id_1");
	sl.unlock("id_1");
    }


    @Test
    public void testReentrancyOfLockSeq()
    {
	StringLock sl = new StringLock();
	sl.lock("id_1");
	sl.lock("id_1");
	sl.unlock("id_1");
	sl.unlock("id_1");
    }


    @Test
    public void testRepeatOfLockSeq()
    {
	StringLock sl = new StringLock();
	sl.lock("id_1");
	sl.unlock("id_1");
	sl.lock("id_1");
	sl.unlock("id_1");
    }


    @Test
    public void testTwoLocksSeq()
    {
	StringLock sl = new StringLock();
	sl.lock("id_1");
	sl.lock("id_2");
	sl.unlock("id_1");
	sl.unlock("id_2");
    }


    @Test
    public void testTwoLocksReverseUnlockSeq()
    {
	StringLock sl = new StringLock();
	sl.lock("id_1");
	sl.lock("id_2");
	sl.unlock("id_2");
	sl.unlock("id_1");
    }

    /**
     * The following tests are concurrency tests.
     * The names end with "Concurrent".
     * Normally it is undesired to have threads in unittests, 
     * but this is not a normal class.
     */ 

    /**
     * In this test the following should happen:
     * CurrentThread: lock( "id_1" )
     * OtherThread:   lock( "id_2" )
     * OtherThread:   unlock( "id_2" )
     * CurrentThread: unlock( "id_1" )
     */ 
    @Test
    public void testTwoLocksConcurrent() throws InterruptedException
    {
	final StringLock sl1 = new StringLock(); // needs unique name

	// Creating inner class with thread for test
	class MyThread extends Thread
	{
	    public void run() 
	    {
		sl1.lock( "id_2" );
		sl1.unlock( "id_2" );
	    }
	}

	sl1.lock( "id_1" );
	MyThread t = new MyThread();
	t.start();
	t.join(); // waiting for other thread to finish
	sl1.unlock( "id_1" );
    }


    /**
     * In this test the following should happen:
     * OtherThread:   lock( "id_1" )
     * CurrentThread: unlock( "id_1" ) - Exception
     *
     * The unlock should throw a IllegalMonitorException since
     * CurrentThread does not hold the lock for "id_1".
     */ 
    @Test( expected=IllegalMonitorStateException.class )
    public void testLockInOneThreadUnlockInAnotherThreadConcurrent() throws InterruptedException
    {
	final StringLock sl2 = new StringLock(); // needs unique name

	// Creating inner class with thread for test
	class MyThread extends Thread
	{
	    public void run() 
	    {
		sl2.lock( "id_1" );
	    }
	}

	MyThread t = new MyThread();
	t.start(); // starting the other thread to get the lock
	t.join(); // wait for the other thread to finish
	sl2.unlock( "id_1" ); // unlock in current thread; this should throw an exception. 
    }


}