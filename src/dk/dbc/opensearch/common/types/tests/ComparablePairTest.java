/** \brief UnitTest for ComparablePair **/
package dk.dbc.opensearch.common.types.tests;


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


import dk.dbc.opensearch.common.types.ComparablePair;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * 
 */
public class ComparablePairTest {

    /**
     * 
     */
    @Test public void testSortableOnFirst(){
        ArrayList< ComparablePair > apairlist = 
            new ArrayList< ComparablePair >();
        apairlist.add( new ComparablePair< String, Integer > ( "b", 1 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "c", 2 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "a", 3 ) );

        Collections.sort( apairlist );

        assertEquals( apairlist.get( 0 ).getFirst(), "a" );
        assertEquals( apairlist.get( 1 ).getFirst(), "b" );
        assertEquals( apairlist.get( 2 ).getFirst(), "c" );

        assertEquals( apairlist.get( 0 ).getSecond(), 3 );
        assertEquals( apairlist.get( 1 ).getSecond(), 1 );
        assertEquals( apairlist.get( 2 ).getSecond(), 2 );
    }

    @Test public void testSortableOnSecond(){
        ArrayList< ComparablePair > apairlist = 
            new ArrayList< ComparablePair >();
        apairlist.add( new ComparablePair< String, Integer > ( "a", 2 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "a", 1 ) );

        Collections.sort( apairlist );

        assertEquals( apairlist.get( 0 ).getFirst(), "a" );
      
        assertEquals( apairlist.get( 0 ).getSecond(), 1 );
        assertEquals( apairlist.get( 1 ).getSecond(), 2 );
    }

    /**
     * Verify that null values are not comparable
     */
    @Test public void testNullDoesNotEqual(){
        ComparablePair<String, String> one = new ComparablePair<String, String>( "a", null );
        ComparablePair<String, String> two = null;//new ComparablePair<String, String>( "a", null );

        assertTrue( ! one.equals( two ) );
    }

    @Test public void testEqualsWorkWithValidArg(){
        ComparablePair<String, String> one = new ComparablePair<String, String>( "a", "1" );
        ComparablePair<String, String> two = new ComparablePair<String, String>( "a", "1" );

         assertTrue( one.equals( two ) );
    } 
    
    @Test public void testEqualsRejectsInvalidArg(){
        ComparablePair<String, String> one = new ComparablePair<String, String>( "a", "1" );
        ComparablePair<String, String> two = new ComparablePair<String, String>( "b", "1" );
        ComparablePair<String, String> three = new ComparablePair<String, String>( "a", "2" );

        assertFalse( one.equals( two ) );
        assertFalse( one.equals( three ) );
    
    }

   @Test public void testEqualsRejectsOtherClass(){
        ComparablePair<String, String> one = new ComparablePair<String, String>( "a", "1" );
        

        assertFalse( one.equals( "test" ) );
    }

    @Test public void testToString()
    {
        String test = "string";
        int testInt = 1;
        String match = String.format( "ComparablePair< %s, %s >", test.toString(), testInt );
        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, testInt );
        assertEquals( match, one.toString() );
        
    }
    
    @Test public void testHashCode()
    {
        String test = "string";
        int testInt = 1;
        Integer testInteger = testInt;
        int match = test.hashCode()^ testInteger.hashCode();
        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, testInt );
        assertTrue( match == one.hashCode() );
    }

    @Test(expected = UnsupportedOperationException.class) 
        public void testCompareToOtherClass()
    {
        String test = "string";
        int testInt = 1;
        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, testInt );
        one.compareTo( "invalid" );
    }

    /**
     * Test of the compareTo method, when the first elements of the 
     * comparablePair are unequal 
     */

    @Test public void testCompareToNonequalFirsts() throws IOException
    {
        String testSmall = "a";
        String testLarge = "b";
        int testInt = 1;

       

        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( testLarge, testInt );
        ComparablePair<String, Integer> two = new ComparablePair<String, Integer >( testSmall, testInt );
        
        assertTrue( one.compareTo( two ) > 0 );
        assertTrue( two.compareTo( one ) < 0 );

    }
  /**
     * Test of the compareTo method, when the first elements of the 
     * comparablePair are equal 
     */

    @Test public void testCompareToEqualFirsts() //throws IOException
    {
        String test = "equal";
      
        int smallInt = 1;
        int largeInt = 9;
       

        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, largeInt );
        ComparablePair<String, Integer> two = new ComparablePair<String, Integer >( test, smallInt );
        
        assertTrue( one.compareTo( two ) > 0 );
        assertTrue( two.compareTo( one ) < 0 );

    }

    /**
     * Testing that case where the first and the second element is in the two
     * pairs are equal
     */

    @Test public void testCompareToEqualPairs()
    {
        String test = "equal";
        int equal = 1;
        
        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, equal );
        ComparablePair<String, Integer> two = new ComparablePair<String, Integer>( test, equal );
    
        assertTrue( 0 == one.compareTo( two ) );
        assertTrue( 0 == two.compareTo( one ) );
    }
    
}
