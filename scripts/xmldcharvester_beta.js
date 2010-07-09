use ( "DcCreator.use.js" );

function buildxml_beta( xml )
{

    Log.info( "RLO: Entering javascript" );

    var dc = DcCreator.createDcFromMarc ( xml ); 

    Log.info( "RLO: Leaving javascript" );

    return dc;

}
