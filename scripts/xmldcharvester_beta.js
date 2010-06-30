use ( "DcCreator" );

function buildxml_beta( xml )
{

    Log.info( "Entering javascript" );

    var dc = DcCreator.createDcFromMarc ( xml ); 

    Log.info( "Leaving javascript" );

    return dc;

}
