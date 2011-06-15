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


package dk.dbc.commons.types;


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
 * evident that the identifier is needed both for locking and
 * unlocking. This enables a thread to lock on more than one
 * identifier at a time
 * <p/>
 * The internal lock is based on a reentrant lock type, and as such a
 * thread can lock the same identifier severeal times without
 * unlocking it. Please notice, that the thread must perform as many
 * unlocks as it performs locks in order to truly release the lock for
 * use by another thread. It is the responisibilty of the thread to
 * ensure that as many unlocks as locks are performed.
 * Please see {@link java.util.concurrent.locks.ReentrantLock} for reference.
 * <p/>
 * Internally the identifiers are connected each to a different
 * lock. A lock will only be kept internally as long as it is in use.
 */
public class StringLock
{
    /*
      We need to maintain a relationship between a Reentrant lock and
      a String (the identifier) on which the lock is associated.  This
      relationship have decided to maintain in a map (HashMap), with
      the String as Key and the ReentrantLock as Value, thereby
      ensuring that at most one ReentrantLock can exist for a given
      String.

      We also strive to meet the goal of not keeping unused locks in
      the map.  We do this by maintaining a counter, which tracks how
      many threads have called lock() on the ReentrantLock
      
      Internally in this class, we therefore need to maintain a
      relation between the String, the ReentrantLock and the counter.
      We have decided, instead of having the ReentrantLock as the
      Value of the HashMap we use a Pair-type as Value, which contains
      a ReentrantLock and an Integer.  Notice, that we cannot use the
      primitive 'int' in a Generic, and therefore we need to use the
      boxed primitive Integer. An Integer-type is Immutable, and
      therefore, each time we need to increment or decrement the
      Integer, we must associate a new Pair-object with the String in
      the HashMap. This choice do clutter the following code a little,
      but we think it better to use a well tested type (Pair) over creating
      an internal class which in order needs to be well tested in
      order to ensure correctness of the overall algorithm.
    */

    private static Logger log = Logger.getLogger( StringLock.class );
    private Map< String, Pair< ReentrantLock, Integer > > lockMap;

    /**
     * Constructor for the StringLock class. 
     */
    public StringLock()
    {
        lockMap = new HashMap< String, Pair< ReentrantLock, Integer > >();
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

	// The call til identifierLock.lock() below must be outside
	// the synchronized block.  
	// Otherwise a deadlock may occur:
	// Assume 'thread 1' locks on identifer 'a', and then 'thread
	// 2' locks on identifier 'a'.  If identifierLock.lock is
	// inside the synchronized block, then the second call to
	// lock() will halt - since 'thread 1' has the lock - inside
	// the synchronized block, effectivly ensuring that no-one
	// can access lockMap - not even for unlocking. Hence the
	// deadlock.

        ReentrantLock identifierLock = null;
        synchronized(lockMap)
        {
            Pair< ReentrantLock, Integer > pair = lockMap.get( identifier );
            if( pair == null )
            {
		log.info( String.format( "Creating new Lock for identifier: %s", identifier ) );
		pair = new Pair< ReentrantLock, Integer >( new ReentrantLock(), new Integer(0) );
            }
	    // At this point, we are certain that 'pair' contains a sensible value.

	    // Increment:
	    int cur = pair.getSecond().intValue(); // current counter
	    Integer inc = new Integer( cur + 1 ); // incremented counter
	    log.debug( String.format( "Counter for %s is now: %s", identifier, inc ) );

            identifierLock = pair.getFirst();

	    // Update the map with a pair containing the incremented counter:
	    lockMap.put( identifier, new Pair< ReentrantLock, Integer >( identifierLock, inc ) );
        }

        log.trace( String.format( "lock, Thread '%s' trying to get lock on identifier :'%s'", Thread.currentThread().getId() ,identifier ) );
        identifierLock.lock(); // See explanation above for reason why this must be outside synchronized block.
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
        log.info( String.format( "Thread '%s' calling StringLock.unlock with identifier: '%s'", Thread.currentThread().getId(), identifier ) );

        synchronized(lockMap)
        {
            Pair< ReentrantLock, Integer > pair = lockMap.get( identifier );
            if( pair == null )
            {
                String msg = String.format( "unlock called and no lock and counter corresponding to the identifier: '%s' found in the lockMap", identifier );
                log.error( msg );
                throw new IllegalStateException( msg );
            }
	    
	    // If we decrease counter before we unlock, and the thread calling unlock()
	    // do not have the lock, then we first decrease the counter and then throws an exception.
	    // The counter will then be out of sync.
	    // Not a good idea! We therefore do it the other way around.
            // We are in the synchronized block on lockMap, so no new threads can get a lock.
	    
            pair.getFirst().unlock(); // try to release lock. Throws IllegalMonitorStateException if not owner of lock.

            // We can now decrease counter since we have unlocked.
	    int cur = pair.getSecond().intValue(); // current counter
	    Integer dec = new Integer( cur - 1 );  // decreased counter
	    log.debug( String.format( "Counter for %s is now: %s", identifier, dec ) );

            log.info( String.format( "unlock, thread: '%s' released lock on identifier: '%s'", Thread.currentThread().getId(), identifier ) );

            if( dec.intValue() == 0 )
            {
                log.info( String.format( "unlock, removed lock associated with identifier: '%s' from the lockMap", identifier ) );
                lockMap.remove( identifier );
            }
	    else
	    {
		// Replace the pair in the Map with a pair containing a decremented counter:
		lockMap.put( identifier, new Pair< ReentrantLock, Integer >( pair.getFirst(), dec ) );
	    }
        }
    }
}
