
package dk.dbc.opensearch.common.types;


import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


import org.apache.log4j.Logger;

/**
 * Class needed for synchronisation of access to modification of
 * objects represented by a PID (String)
 * The Map lockMap has a String (most often ad PID) as value and a
 * Pair consisting of a lock and a counter.
 * The counter is used to maintain the map so we know when a lock is no longer
 * in use and can be deleted
 */
public class StringLock
{

    Logger log = Logger.getLogger( StringLock.class );
    /**
     * inner class used for maintainence of the lock and its counter
     */

    private class LockAdmin
    {
        private ReentrantLock lock;
        private int counter;

        LockAdmin()
        {
            this.lock = new ReentrantLock();
            this.counter = 0;
        }

        /**
         *  Retrieves the lock of the pair.
         *  @return ReentrantLock The lock of the pair.
         */
        ReentrantLock getLock()
        {
            return lock;
        }

        /**
         *  Retrieves the counter of the pair.
         *  @return int The counter the pair.
         */
        boolean counterIsZero()
        {
            if( counter == 0 )
            {
                return true;
            }
            return false;
        }

        /**
         * decreases the counter part of the pair with 1
         * @throws IllegalStateException if the counter is attempted to
         * be decreased below zero
         */
        void decreaseCounter() throws IllegalStateException
        {
            if( counter == 0 )
            {
                String msg = "counter is decreased below zero!!" ;
                log.error( msg );
                throw new IllegalStateException( msg );
            }

            counter--;
        }

        /**
         * increases the counter part of the pair with 1
         */
        void increaseCounter()
        {
            counter++;
        }

        /**
         *  A string representation of the elements in the following format:
         *  a string representation of the lock and the value of the counter
         *  @return a String representation of the object
         */
        @Override
        public String toString()
        {
            return String.format( "LockAdmin, Lock: %s, Counter: %s >", lock.toString(), counter );
        }

        /**
         *  Returns a unique hashcode for the specific combination
         *  of elements in this LockAdmin
         */
        @Override
        public int hashCode()
        {
            return lock.hashCode() ^ counter;
        }
    }

    private Map< String, LockAdmin > lockMap;

    /**
     * Constructor for the StringLock class
     */
    public StringLock()
    {
        lockMap = new HashMap< String, LockAdmin >();
        log.info( "StringLock constructed" );
    }

    public void lock( String pid )
    {
        log.info( String.format( "Thread '%s' calling StringLock.lock with pid: '%s'", Thread.currentThread().getId(), pid  ) );
        ReentrantLock pidLock= null;
        LockAdmin lockAdm = null;
        synchronized(lockMap)
        {
            lockAdm = lockMap.get( pid );
            if( lockAdm == null )
            {
                lockAdm = new LockAdmin();
                lockMap.put( pid, lockAdm );
            }

            lockAdm.increaseCounter();
            pidLock = lockAdm.getLock();
        }
        log.trace( String.format( "lock, Thread '%s' trying to get lock on pid :'%s'", Thread.currentThread().getId() ,pid  ) );
        pidLock.lock();
        log.trace( String.format( "lock, Thread '%s' got lock on pid: '%s'", Thread.currentThread().getId(), pid ) );
    }

    public void unlock( String pid )
    {
        log.debug( String.format( "Thread '%s' calling StringLock.unlock with pid: '%s'", Thread.currentThread().getId(), pid  ) );
        ReentrantLock pidLock = null;

        synchronized(lockMap)
        {
            LockAdmin lockAdm = lockMap.get( pid );
            if( lockAdm == null )
            {
                String msg = String.format( "unlock called and no LockAdmin corresponding to the PID: '%s' found in the lockMap", pid );
                log.error( msg );
                throw new IllegalStateException( msg );
            }

            log.info( String.format( "unlock, thread: '%s' released lock on pid: '%s'", Thread.currentThread().getId(), pid ) );
            lockAdm.decreaseCounter();
            lockAdm.getLock().unlock();

            if( lockAdm.counterIsZero() )
            {
                log.info( String.format( "unlock, removed lock associated with pid: '%s' from the lockMap", pid ) );
                lockMap.remove( pid );
            }
        }
    }
}
