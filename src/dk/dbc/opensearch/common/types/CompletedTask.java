/**
 * \file CompletedTask.java
 * \brief The CompletedTask class
 * \package types;
 */
package dk.dbc.opensearch.common.types;


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


import java.util.concurrent.FutureTask;


/**
 * The purpose of the CompletedTask is to hold information about a
 * completed threadpooljob. it contains a futureTask representing the
 * job, and a float which is the return value of the job.
 */
public class CompletedTask<V> 
{ 
    private FutureTask future;
    private V result;

    
    /**
     * Constructor of the CompletedTask instance.
     * 
     * @param future the FutureTask of the completed task
     * @param result the result of the completed task
     */
    public CompletedTask( FutureTask future, V result) 
    {
        this.future = future;
        this.result = result;
    }
   
    
    /**
     * Gets the future
     * 
     * @return The future
     */
    public FutureTask getFuture()
    {
        return future;
    }

    
    /**
     * Gets the result
     * 
     * @return The result
     */
    public V getResult()
    {
        return result;
    }

    
    /**
     * Sets the future of the completedTask
     * 
     * @param The future
     */    
    public void setFuture( FutureTask future )
    {
            this.future = future;
    }

    
    /**
     * Sets the result of the completedTask
     * 
     * @param The result
     */
    public void setResult( V result )
    {
        this.result = result;
    }
}
