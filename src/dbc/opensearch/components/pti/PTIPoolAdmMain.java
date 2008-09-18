package dbc.opensearch.components.pti;

public class PTIPoolAdmMain {
    public final static void main(String[] args){
        try{
            PTIPoolAdm ptipooladm = new PTIPoolAdm();
        }catch(Exception e){
            System.out.println("caught error: "+e.getMessage() );
            e.printStackTrace();
        }
    }
}