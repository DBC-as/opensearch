use ( "DcCreator.use.js" );

function buildxml_beta( xml )
{

    Log.info( "RLO: Entering javascript" );

    var dc = DcCreator.createDcFromMarc ( xml ); 

    Log.info( "RLO: Leaving javascript" );

    return dc;

}

function dcFromMarc( xml )
{

    Log.info( "RLO: Entering javascript dcFromMarc" );

    var dc = DcCreator.createDcFromMarc ( xml ); 

    Log.info( "RLO: Leaving javascript" );

    return dc;

}

function dcFromDkabm( xml )
{

    Log.info( "RLO: Entering javascript dcFromDkabm" );

    var dc = DcCreator.createDcFromDkabm ( xml ); 

    Log.info( "RLO: Leaving javascript" );

    return dc;

}

function dcFromOso( xml )
{

    Log.info( "RLO: Entering javascript dcFromOso" );

    var dc = DcCreator.createDcFromOso ( xml ); 

    Log.info( "RLO: Leaving javascript" );

    return dc;

}
