// xml namespace wrapper

EXPORTED_SYMBOLS=[ 'XmlNamespaces' ];

const XmlNamespaces = function(){

    var that = {};
    that.ubf = new Namespace( "ubf", "http://www.dbc.dk/ubf" ); 
    that.holdings = new Namespace("hs", 'http://oss.dbc.dk/ns/holdings');
    that.ill5 = new Namespace ("ill5", "http://www.loc.gov/z3950/agency/defns/ill5" );
    that.zigholdings = new Namespace("n", 'http://www.loc.gov/z3950/agency/defns/HoldingsSchema8');
    that.openagency = new Namespace ("oa", "http://oss.dbc.dk/ns/openagency");
    that.ting = new Namespace ( "ting", "http://www.dbc.dk/ting" );
    that.es = new Namespace ( "es", "http://oss.dbc.dk/ns/es" );
    that.dkabm = new Namespace ( "dkabm", "http://biblstandard.dk/abm/namespace/dkabm/" );
    that.ac = new Namespace ( "ac", "http://biblstandard.dk/ac/namespace/" );
    that.dkdcplus = new Namespace ( "dkdcplus", "http://biblstandard.dk/abm/namespace/dkdcplus/" );
    that.oss = new Namespace ( "oss", "http://oss.dbc.dk/ns/osstypes" );
    that.dc = new Namespace ( "dc", "http://purl.org/dc/elements/1.1/" );
    that.dcterms = new Namespace ( "dcterms", "http://purl.org/dc/terms/" );
    that.xsi = new Namespace ( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );
    that.marcx = new Namespace ( "marcx", "http://www.bs.dk/standards/MarcXchange" );
    that.docbook = new Namespace ( "docbook", "http://docbook.org/ns/docbook" );
    that.oai_dc = new Namespace( "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/" );
    that.oso = new Namespace ( "oso", "http://oss.dbc.dk/ns/opensearchobjects");

    const const_that = that;
    return const_that;
}();


