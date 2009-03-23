/**
 * \file PrivateAccessor.java
 * \brief The PrivateAccessor class
 * \package tools
 */
package dk.dbc.opensearch.common.helpers;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;


/**
 * \ingroup tools
 * \brief The PrivateAccessor class is used for testing purposes. It
 * implements 2 methods that can give you access to private members of
 * a class for testing purposes.
 */
public final class PrivateAccessor
{
    /**
     * Retrieves the value of a private field (for unittesting purposes) 
     *
     * @param o is the object that the field resides in
     * @param fieldName is the name of the field you want access.
     *
     * @returns an object containing the fields value
     */
    public static Object getPrivateField( Object o, String fieldName )
    {
        Assert.assertNotNull( o );
        Assert.assertNotNull( fieldName );

        final Field[] fields = o.getClass().getDeclaredFields();
        for( int i = 0; i < fields.length; i++ )
        {
            if( fieldName.equals( fields[i].getName() ) )
            {
                try
                {
                    fields[i].setAccessible(true);
                    return fields[i].get( o );
                }
                catch( IllegalAccessException ex )
                {
                    Assert.fail( String.format( "IllegalAccessException accessing %s", fieldName ) );
                }
            }
        }
        
        throw new IllegalArgumentException( String.format( "Field '%s' not found", fieldName ) );
    }

    
    /**
     * Invokes a private method and returns its returnvalue (for unitesting purposes) 
     *
     * @param o is the object that the field resides in
     * @param methodName is the name of the method you want invoke.
     * @param args a list (vararg list) of arguments to the called function
     *
     * @returns an object containing the methods return value, if any.
     */
    public static Object invokePrivateMethod(Object o, String methodName, Object... args)
    {
        Assert.assertNotNull( o );
        Assert.assertNotNull( methodName );

        final Method methods[] = o.getClass().getDeclaredMethods();
        for( int i = 0; i < methods.length; i++ )
        {
            //can fail if there are overloaded methods 
            if( methodName.equals( methods[i].getName() ) )
            {
                try
                {
                    methods[i].setAccessible(true);
                    return methods[i].invoke( o, args );
                }
                catch( IllegalAccessException iae )
                {
                    Assert.fail( String.format( "IllegalAccessException accessing %s", methodName ) );
                }
                catch( InvocationTargetException ite )
                {
                        Assert.fail( String.format( "InvocationTargetException (the method has thrown an error) accessing %s", methodName ) );
                }
            }
        }
        
        throw new IllegalArgumentException( String.format( "Method '%s' not found", methodName ) );
    }
}
