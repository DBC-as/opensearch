package dbc.opensearch.components.datadock.tests.datadocktest;

public class DataDockTestMain{
    public static void main(String[] args){
        System.out.print("Creating the DataDockTest object \n");
        DataDockTest ddt = new DataDockTest();
        System.out.print("******************************************* \n");
        System.out.print("*                                         * \n");
        System.out.print("* Only testing DataDock.fedoraStoreData() * \n");
        System.out.print("*                                         * \n");
        System.out.print("******************************************* \n");

        // System.out.print("Testing DataDock.estimate() \n");
        //ddt.testDataDockEstimate();

        System.out.print("Testing DataDock.fedoraStoreData() \n");
        ddt.testDataDockFedoraStoreData();

        //System.out.print("Testing DataDock.queuefedoraHandle \n");
        //ddt.testDataDockQueueFedoraHandle();

        System.out.print("Test ended \n");
        
    }
}