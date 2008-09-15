/** \file */
package dbc.opensearch.components.datadock;
/**
 * Enum to control the possible values of mimetypes that we can
 * handle. This is a subset of the official mimetypes as listed in
 * /etc/mime.types. 
 */
public enum CargoMimeType{
    /** represents known mimetypes. All handler registrations must use
     * mimetypes defined here. Mimetypes from /etc/mime.types
     */
    TEXT_HTML( "text/html", "HTML Document" ),
    TEXT_XML( "text/xml", "XML Document"),
    APPLICATION_PDF( "application/pdf", "PDF Document" ),
    APPLICATION_PS( "application/postscript", "PostScript Document");

    private final String mimetype;
    private final String description;

    CargoMimeType( String mimetype, String description ){
        this.mimetype    = mimetype;
        this.description = description;
    }

    /**
     * use instanceOfCargoMimeType.getMimeType() to get the (official)
     * name of the mimetype
     */
    String getMimeType(){
        return this.mimetype;
    }

    String getDescription(){
        return this.description;
    }
}