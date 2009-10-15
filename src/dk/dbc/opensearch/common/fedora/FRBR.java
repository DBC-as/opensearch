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

package dk.dbc.opensearch.common.fedora;


/**
 *
 */
public enum FRBR
{
    ABRIDGEMENT( "abridgement" ),
    ABRIDGEMENTOF( "abridgementOf" ),
    ADAPTION( "adaption" ),
    ADAPTIONOF( "adaptionOf" ),
    ALTERNATE( "alternate" ),
    ALTERNATEOF( "alternateOf" ),
    ARRANGEMENT( "arrangement" ),
    ARRANGEMENTOF( "arrangementOf" ),
    CLASSICALWORK( "ClassicalWork" ),
    COMPLEMENT( "complement" ),
    COMPLEMENTOF( "complementOf" ),
    CONCEPT( "Concept" ),
    CORPORATEBODY( "CorporateBody" ),
    CREATOR( "creator" ),
    CREATOROF( "creatorOf" ),
    DATA( "Data" ),
    EMBODIMENT( "embodiment" ),
    EMBODIMENTOF( "embodimentOf" ),
    ENDEAVOUR( "Endeavour" ),
    EVENT( "Event" ),
    EXEMPLAR( "exemplar" ),
    EXEMPLAROF( "exemplarOf" ),
    EXPRESSION( "Expression" ),
    IMAGE( "Image" ),
    IMITATION( "imitation" ),
    IMITATIONOF( "imitationOf" ),
    ITEM( "Item" ),
    LEGALWORK( "LegalWork" ),
    LITERARYWORK( "LiteraryWork" ),
    MANIFESTATION( "Manifestation" ),
    MOVINGIMAGE( "MovingImage" ),
    OBJECT( "Object" ),
    OWNER( "owner" ),
    OWNEROF( "ownerOf" ),
    PART( "part" ),
    PARTOF( "partOf" ),
    PERFORMANCE( "Performance" ),
    PERSON( "Person" ),
    PLACE( "Place" ),
    PRODUCER( "producer" ),
    PRODUCEROF( "producerOf" ),
    REALIZATION( "realization" ),
    REALIZATIONOF( "realizationOf" ),
    REALIZER( "realizer" ),
    REALIZEROF( "realizerOf" ),
    RECONFIGURATION( "reconfiguration" ),
    RECONFIGURATIONOF( "reconfigurationOf" ),
    RELATEDENDEAVOUR( "relatedEndeavour" ),
    REPRODUCTION( "reproduction" ),
    REPRODUCTIONOF( "reproductionOf" ),
    RESPONSIBLEENTITY( "ResponsibleEntity" ),
    RESPONSIBLEENTITY( "responsibleEntity" ),
    RESPONSIBLEENTITYOF( "responsibleEntityOf" ),
    REVISION( "revision" ),
    REVISIONOF( "revisionOf" ),
    SCHOLARLYWORK( "ScholarlyWork" ),
    SOUND( "Sound" ),
    SUBJECT( "Subject" ),
    SUBJECT( "subject" ),
    SUCCESSOR( "successor" ),
    SUCCESSOROF( "successorOf" ),
    SUMMARIZATION( "summarization" ),
    SUMMARIZATIONOF( "summarizationOf" ),
    SUPPLEMENT( "supplement" ),
    SUPPLEMENTOF( "supplementOf" ),
    TEXT( "Text" ),
    TRANSFORMATION( "transformation" ),
    TRANSFORMATIONOF( "transformationOf" ),
    TRANSLATION( "translation" ),
    TRANSLATIONOF( "translationOf" ),
    WORK( "Work" );

    private String literal;
    private final String FRBR_NS = "http://purl.org/vocab/frbr/core#";

    public FRBR( String literal )
    {
        this.literal = literal;
    }
}
