package dk.dbc.opensearch.common.pluginframework;


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

import java.lang.Exception;

/**
 * Exception thrown in case of plugin processing or calculation errors. All
 * exceptions that are raised during plugin execution are wrapped in a
 * PluginException. In this way the pluginframework only exposes one kind of
 * exception. The wrapped (original) exception can be accessed by the
 * getException() method.
 */
public class PluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4451067896581886657L;
	private Exception e;
	private String msg;

	/**
	 * Constructor wrapping the original exception.
	 * 
	 * Constructs the Exception with null as the message.
	 * 
	 * @param e
	 *            The originating exception
	 */
	public PluginException(Exception e) {
		this.e = e;
		this.msg = null;

	}

	/**
	 * Constructor with wrapped exception and message explaining the cause from
	 * the plugin point of view.
	 * 
	 * @param msg
	 *            The message to annotate the PluginException from the Catch of
	 *            the Exception e
	 * @param e
	 *            The original Exception that was caught
	 */
	public PluginException(String msg, Exception e) {
		this.msg = msg;
		this.e = e;
	}

	/**
	 * Constructor for creating an Exception that origins from a plugin
	 * 
	 * @param msg
	 *            The reason for the throwing of the Exception
	 */
	public PluginException(String msg) {
		this.msg = msg;
		this.e = null;
	}

	/**
	 * Returns the wrapped (original) exception that was caught inside the
	 * plugin. Returns null if the PluginException is the originating exception
	 * 
	 * @return Exception the wrapped exception
	 */
	public Exception getException() {
		return e;
	}

	/**
	 * Returns the message that was given from the plugin at the cathing of the
	 * original exception. Returns null if no message was given at the time of
	 * the catch.
	 */
	public String getMessage() {
		return msg;
	}
}
