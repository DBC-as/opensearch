package dk.dbc.opensearch.plugins;


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

import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.pluginframework.PluginType;

import org.compass.core.CompassSession;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.pdfparser.PDFParser;

public class IndexerPDF implements IIndexer
{

    Logger log = Logger.getLogger( IndexerPDF.class );

    public long getProcessTime( CargoContainer cargo, CompassSession session, String fedoraHandle ) 
    {
        CargoObject co = cargo.getFirstCargoObject( DataStreamType.OriginalData );
        byte[] data = co.getBytes();

        //10: read pdf from byte[] (pdf object from inputstream)
        COSDocument cos = null;
        try
        {
            cos = readPDF( data );
        }
        catch( IOException ioe )
        {
            //logging the error, but allowing the program to continue
            log.fatal( String.format( "Could not read byte[] from CargoObject. format=%s, submitter=%s, Exception=%s", 
                                      co.getFormat(), co.getSubmitter(), ioe.getStackTrace().toString() ) );
            //should we: throw new IOException( ioe ); from here?
        }
        //20: retrieve metadata from pdf object
        HashMap< String, String > metadata = getPDFMetaData( cos );

        //30: build index from pdf object
        


        return 0l;
    }


    /**
     * Extracts the metadata, if any, from the document.
     *
     * @param cos A COSDocument containing the pdf
     *  
     * @return A HashMap containing the <field, value> encoded as
     * strings
     *
     */
    private HashMap<String, String> getPDFMetaData( COSDocument cos ) {
        HashMap<String, String> metadata = new HashMap<String, String>();

        // extract PDF document's meta-data
        PDDocument pdDoc = new PDDocument(cos);

        PDDocumentInformation docInfo =
            pdDoc.getDocumentInformation();
        String author       = docInfo.getAuthor();
        String creator      = docInfo.getCreator();
        String title        = docInfo.getTitle();
        String keywords     = docInfo.getKeywords();
        String summary      = docInfo.getSubject();
        String pages        = Integer.toString( pdDoc.getNumberOfPages() );

        if ( ( author != null ) && ( !author.equals( "" ) ) ) {
            metadata.put("author", author );
        }
        if ( ( creator != null ) && ( !creator.equals( "" ) ) ) {
            metadata.put("creator", creator );
        }
        if ( ( title != null ) && ( !title.equals( "" ) ) ) {
            metadata.put("title", title );
        }
        if ( ( keywords != null ) && ( !keywords.equals( "" ) ) ) {
            metadata.put("keywords", keywords );
        }
        if ( ( summary != null ) && ( !summary.equals( "" ) ) ) {
            metadata.put("summary", summary );
        }
        if ( ( pages != null ) && ( !pages.equals( "" ) ) ) {
            metadata.put("pages", pages );
        }

        return metadata;
    }

    /**
     * Reads a pdf document from the byte[]
     * 
     */
    private COSDocument readPDF( byte[] pdf_data ) throws IOException
    {

        InputStream is = new ByteArrayInputStream( pdf_data );

        COSDocument cos = null;

        PDFParser parser = new PDFParser( is );
        parser.parse();
        cos =parser.getDocument();
        cos.close();

        return cos;
    }

    public PluginType getTaskName()
    {
        return PluginType.INDEX;
    }

}