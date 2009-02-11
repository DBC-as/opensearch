/**
 * \file CargoMimeType.java
 * \brief The CargoMimetype enum
 * \package datadock
 */
package dk.dbc.opensearch.common.types;

import org.apache.log4j.Logger;

/**
 * \ingroup datadock
 * \brief Enum to control the possible values of mimetypes that we can
 * handle. This is a subset of the official mimetypes as listed in
 * /etc/mime.types.
 */
public enum CargoMimeType
{
	/** represents known mimetypes. All handler registrations must use
     * mimetypes defined here. Mimetypes from /etc/mime.types
     */
    TEXT_XML( "text/xml", "XML Document"),
    APPLICATION_PDF( "application/pdf", "PDF Document" );

	static Logger log = Logger.getLogger( CargoMimeType.class );
	
    private final String mimetype;
    private final String description;


    CargoMimeType( String mimetype, String description )
    {
        this.mimetype    = mimetype;
        this.description = description;
    }


    /**
     * Returns The description of the mimetype.
     *
     * @returns The description of the mimetype.
     */
    public String getDescription()
    {
        return this.description;
    }

    
    /**
     * use instanceOfCargoMimeType.getMimeType() to get the (official)
     * name of the mimetype
     *
     * @returns The mimetype
     */
    public String getMimeType()
    {
        return this.mimetype;
    }
    
    
    public static boolean validMimetype( String mimetype )
    {
        CargoMimeType CMT = CargoMimeType.getMimeFrom( mimetype );
        log.debug( "checking mimetype" );
        
        if( CMT == null )
        	return false;
        
        return true;
    }


    /**
     * @param mime
     * @return
     */
    public static CargoMimeType getMimeFrom( String mime )
    {
        CargoMimeType CMT = null;
        for (CargoMimeType cmt : CargoMimeType.values() )
        {
            if( mime.equals( cmt.getMimeType() ) )
            {
                CMT = cmt;
            }
        }
        
        return CMT;
    }
}