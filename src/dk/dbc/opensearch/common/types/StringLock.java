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


import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * This class enables locking based on identifiers. Currently the
 * identifiers need to be {@link String}s.  There are no limitations
 * on the contents of the {@link String}, just that it is not null.
 * <p/>
 * The class contains two public methods {@link StringLock#lock(
 * String )} and {@link StringLock#unlock( String )}. It is therefore
 * evidient that the identifier is needed both for locking and
 * unlocking. This enables a thread to lock on more than one identifier at a time
 * <p/>
 * The internal lock is based on a reentrant lock type, and as such a
 * thread can lock the same identifier severeal times without
 * unlocking it. Please notice, that the thread must perform as many
 * unlocks as it performs locks in order to truly release the lock for
 * use by another thread. 
 * Please see {@link java.util.concurrent.locks.ReentrantLock} for reference.
 * <p/>
 * Internally the identifiers are connected each to a different
 * lock. A lock will only be kept internally as long as it is in use.
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
     * Constructor for the StringLock class. 
     */
    public StringLock()
    {
        lockMap = new HashMap< String, LockAdmin >();
        log.info( "StringLock constructed" );
    }

    /**
     * Tries to get a lock on the {@code identifier}.  If noone has
     * the lock it will be granted, otherwise the lock will block
     * until it is granted.
     * 
     * @param identifier the identifier you wish to lock on.
     *
     * @throws IllegalStateException if null is given as
     * identifer. That is, the null-object, not \"null\" as a {@link
     * String} which is a perfectly legal value.
     */
    public void lock( String identifier )
    {
        log.info( String.format( "Thread '%s' calling StringLock.lock with identifier: '%s'", Thread.currentThread().getId(), identifier  ) );

	if ( identifier == null )
	{
	    String errMsg = String.format( "\"null\" is not accepted as a legal value for lock()" );
	    log.warn( errMsg );
	    throw new IllegalStateException( errMsg );
	}

        ReentrantLock identifierLock= null;
        LockAdmin lockAdm = null;
        synchronized(lockMap)
        {
            lockAdm = lockMap.get( identifier );
            if( lockAdm == null )
            {
                lockAdm = new LockAdmin();
                lockMap.put( identifier, lockAdm );
            }

            lockAdm.increaseCounter();
            identifierLock = lockAdm.getLock();
        }
        log.trace( String.format( "lock, Thread '%s' trying to get lock on identifier :'%s'", Thread.currentThread().getId() ,identifier  ) );
        identifierLock.lock();
        log.trace( String.format( "lock, Thread '%s' got lock on identifier: '%s'", Thread.currentThread().getId(), identifier ) );
    }

    /**
     * Releases a lock on an identifier.
     *
     * @param identifier A {@link String} containing the identifier you want to unlock.
     *
     * @throws IllegalStateException if the {@code identifier} is not
     * associated with a lock, i.e. {@link StringLock#lock( String )} has not been called
     * on the {@code identifier}.
     *
     * @throws IllegalMonitorStateException if the unlocking thread is
     * not the owner of the lock.
     */
    public void unlock( String identifier )
    {
        log.info( String.format( "Thread '%s' calling StringLock.unlock with identifier: '%s'", Thread.currentThread().getId(), identifier  ) );

        synchronized(lockMap)
        {
            LockAdmin lockAdm = lockMap.get( identifier );
            if( lockAdm == null )
            {
                String msg = String.format( "unlock called and no LockAdmin corresponding to the identifier: '%s' found in the lockMap", identifier);
                log.error( msg );
                throw new IllegalStateException( msg );
            }

            lockAdm.getLock().unlock(); // try to release lock.
            lockAdm.decreaseCounter(); // can not decrease counter before we have assured we can unlock.
            log.info( String.format( "unlock, thread: '%s' released lock on identifier: '%s'", Thread.currentThread().getId(), identifier ) );

            if( lockAdm.counterIsZero() )
            {
                log.info( String.format( "unlock, removed lock associated with identifier: '%s' from the lockMap", identifier ) );
                lockMap.remove( identifier );
            }
        }
    }
}
