/**
 * \file PrivateAccessor.java
 * \brief The PrivateAccessor class
 * \package tools
 */
package dk.dbc.opensearch.common.helpers;


/*
*GNU, General Public License Version 3. If any software components linked 
*together in this library have legal conflicts with distribution under GNU 3 it 
*will apply to the original license type.
*
*Software distributed under the License is distributed on an "AS IS" basis,
*WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
*for the specific language governing rights and limitations under the
*License.
*
*Around this software library an Open Source Community is established. Please 
*leave back code based upon our software back to this community in accordance to 
*the concept behind GNU. 
*
*You should have received a copy of the GNU Lesser General Public
*License along with this library; if not, write to the Free Software
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
***** END LICENSE BLOCK ***** */


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
