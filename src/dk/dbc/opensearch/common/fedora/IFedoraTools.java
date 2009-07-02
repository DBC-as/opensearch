/**
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


package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.xsd.DigitalObject;
import dk.dbc.opensearch.common.types.CargoContainer;


/** 
 *  The FedoraTools API specifies the operations that can be
 *  performed in and with Digital Objects. A Digital Object consists
 *  of one or more Data Streams, some of which fulfill special roles
 *  w.r.t. the digital repository, the Fedora Commons base.
 * 
 * The primary actions that can be performed includes:
 *    - the construction of Digital Objects from the opensearch datatype CargoContainer
 *    - adding 'special roles' Data Streams, such as RELS-EXT or DC to a Digital Object
 * 
 * From the Fedora Commons explanation of RELS-EXT: 
 * 
 * The creation of Fedora digital object relationship metadata is the
 * basis for enabling advanced access and management functionality
 * driven from metadata that is managed within the
 * repository. Examples of the uses of relationship metadata include:
 *
 *    - Organize objects into collections to support management, OAI
 *      harvesting, and user search/browse
 *    - Define bibliographic relationships among objects such as those
 *      defined in Functional Requirements for Bibliographic Reecords
 *    - Define semantic relationships among resources to record how
 *      objects relate to some external taxonomy or set of standards
 *    - Model a network overlay where resources are linked together based
 *      on contextual information (for example citation links or
 *      collaborative annotations)
 *    - Encode natural hierarchies of objects
 *    - Make cross-collection linkages among objects (for example show
 *      that a particular document in one collection can also be
 *      considered part another collection)
 */
public interface IFedoraTools
{

    /** 
     * Given a CargoContainer, this method examines the contained
     * CargoObjects and constructs a DigitalObject with each
     * CargoObject represented as a DataStream. This method also
     * constructs a minimal RELS-EXT, defining the object itself
     * through the Fedora pid ID. The Fedora documentation specifices
     * that a RELS-EXT datastream, if contructed, must contain an
     * rdf:Description elment, ant that there must be only one
     * rdf:Description element in the RELS-EXT datastream. An example
     * minimal RELS-EXT (RDF/XML) looks like this:
     * @code
     * <rdf:RDF
     * ...[namespace declarations]...
     * >
     *   <rdf:Description rdf:about="">
     *   </rdf:Description>
     * </rdf:RDf>
     * @endcode
     *
     * A DC DataStream is automatically created if none is
     * specified. This is done in the Fedora layer, typically through
     * the ingest method. An example of a default DC DataStream looks
     * like this:
     * @code
     * <oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" 
     *            xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
     *            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
     *            xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd"
     * >
     *   <dc:title>test object</dc:title>
     *   <dc:identifier>test:1</dc:identifier>
     * </oai_dc:dc>
     * @endcode
     * 
     * @param cargo The CargoContainer from which the data to
     * construct the DigitalObject is retrieved.
     * 
     * @return A DigitalObject containing the CargoObjects from the
     * CargoContainer as DataStreams.
     */
    public DigitalObject constructDigitalObject( CargoContainer cargo );

    public DigitalObject constructDigitalObject( CargoContainer cargo, String state );

    public DigitalObject addDublinCore( DigitalObject digo );

    public DigitalObject addRELSEXT( DigitalObject digo );





  
}