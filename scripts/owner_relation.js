subject_prefix="info:fedora/";
/*
 *
 * handling of subjects 
 *
 */

var dbcmap = new Object;

dbcmap["anmeldelser"] = "free" ;
dbcmap["anmeld"] = "free" ;
dbcmap["materialevurderinger"] = "materialevurderinger" ;
dbcmap["matvurd"] = "materialevurderinger" ;
dbcmap["forfatterweb"] = "forfatterweb" ;
dbcmap["forfatterw"] = "forfatterweb" ;
dbcmap["faktalink"] = "faktalink" ;
dbcmap["dr_forfatteratlas"] = "free" ;
dbcmap["dr_atlas"] = "free" ;
dbcmap["dr_bonanza"] = "free" ;
dbcmap["louisiana"] = "louisiana" ;
dbcmap["artikler"] = "artikler" ;
dbcmap["dsd"] = "free" ;
dbcmap["pg"] = "pg";

function lookup_dbcmap( format )
{
    if( dbcmap[ format ] == undefined ) 
    {
        Log.error("Unknown folkebib format :" + format );
        throw new PluginException("Unknown folkebib format :" + format );
    } 
    else 
    {
        return subject_prefix + dbcmap[ format ];
    }
}


var _150014map = new Object; 

_150014map["nmalbum"] = "netmusik_album";
_150014map["nmtrack"] = "netmusik_track";

function lookup_150014( format ) 
{
    if( _150014map[ format ] == undefined ) 
    {
        Log.error("Unknown 150014 format :" + format );
	throw new PluginException( "Unknown 150014 format :" + format );
    }

    return _150014map[ format ];
};

/*
 * 
 * Folkebib actions
 * 
 */
var folkebibmap = new Object; 

folkebibmap["775100"] = "aakb_";
folkebibmap["710100"] = "kkb_";

function lookup_folkebib( submitter ) 
{
    if( folkebibmap[ submitter ] == undefined ) 
    {
        Log.error("Unknown folkebib submitter :" );
	throw new PluginException("Unknown folkebib submitter :" + submitter);
    }

    return folkebibmap[ submitter ];
};


function doit_folkebib_getsubject( prefix,  format ) 
{
    if( format == "katalog") 
    {
	return prefix + "catalog";
    } 
    else
    {				
	return prefix + format;
    }
};


function addFolkebibRelation( rels_ext, submitter, format )
{
    ownerpid = doit_folkebib_getsubject( lookup_folkebib( submitter ) , format );
    
    rels_ext.addRelationship( IS_OWNED_BY, ownerpid );
    
    return rels_ext;
}

function addDbcRelation( rels_ext, submitter, format )
{
    ownerpid = lookup_dbcmap( format );

    rels_ext.addRelationship( IS_OWNED_BY, ownerpid );
    
    // add ekstra pg data.
    if( "pg" == format ) 
    {
        rels_ext.addRelationship( IS_AFFILIATED_WITH, "Children" );                                                     
        rels_ext.addRelationship( IS_AFFILIATED_WITH, "free" );       
    }

    return rels_ext;
}

function add150014Relation( rels_ext, submitter, format )
{
    owner_pid = lookup_150014( format );

    rels_ext.addRelationship( IS_OWNED_BY, owner_pid );

    return rels_ext;
}

/* 
 * Main function
 * 
 * @param rels_ext A datastructure representing the digital object rels-ext stream
 * @param submitter A string representing the submitter
 * @param format A string representing the format
 * 
 * @return a modified rels_ext datastructure
 */
function addOwnerRelation( rels_ext, submitter, format )
{
    if( submitter.charAt(0) == "7") 
    {
        rels_ext = addFolkebibRelation( rels_ext, submitter, format );
        return rels_ext;
    } 
    else if( submitter == "dbc" ) 
    {
        rels_ext = addDbcRelation( rels_ext, submitter, format );
        return rels_ext;
    }
    else if ( submitter == "150014" )
    {
        rels_ext = add150014Relation( rels_ext, submitter, format );
        return rels_ext;
    }
    else 
    {
        Log.warn("Unknown submitter - no owner relations set. ");
        return rels_ext;
    }
}

