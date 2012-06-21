use ( "DcCreator.use.js" );

function buildxml_beta( xml )
{

    Log.info( "Entering javascript" );

    var dc = DcCreator.createDcFromMarc ( xml ); 

    Log.info( "Leaving javascript" );

    return dc;

}

function dcFromMarc( xml )
{

    Log.info( "Entering javascript dcFromMarc" );

    var dc = DcCreator.createDcFromMarc ( xml ); 

    Log.info( "Leaving javascript" );

    return dc;

}

function dcFromDkabm( xml )
{

    Log.info( "Entering javascript dcFromDkabm" );

    var dc = DcCreator.createDcFromDkabm ( xml ); 

    Log.info( "Leaving javascript" );

    return dc;

}

function dcFromOso( xml )
{

    Log.info( "Entering javascript dcFromOso" );

    var dc = DcCreator.createDcFromOso ( xml ); 

    Log.info( "Leaving javascript" );

    return dc;

}
