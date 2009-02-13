package info.fedora.www.definitions._1._0.api;

public class FedoraAPIMProxy implements info.fedora.www.definitions._1._0.api.FedoraAPIM {
  private String _endpoint = null;
  private info.fedora.www.definitions._1._0.api.FedoraAPIM fedoraAPIM = null;
  
  public FedoraAPIMProxy() {
    _initFedoraAPIMProxy();
  }
  
  public FedoraAPIMProxy(String endpoint) {
    _endpoint = endpoint;
    _initFedoraAPIMProxy();
  }
  
  private void _initFedoraAPIMProxy() {
    try {
      fedoraAPIM = (new info.fedora.www.definitions._1._0.api.FedoraAPIMServiceLocator()).getFedoraAPIMServiceHTTPPort();
      if (fedoraAPIM != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)fedoraAPIM)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)fedoraAPIM)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (fedoraAPIM != null)
      ((javax.xml.rpc.Stub)fedoraAPIM)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public info.fedora.www.definitions._1._0.api.FedoraAPIM getFedoraAPIM() {
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM;
  }
  
  public java.lang.String ingest(byte[] objectXML, java.lang.String format, java.lang.String logMessage) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.ingest(objectXML, format, logMessage);
  }
  
  public java.lang.String modifyObject(java.lang.String pid, java.lang.String state, java.lang.String label, java.lang.String ownerId, java.lang.String logMessage) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.modifyObject(pid, state, label, ownerId, logMessage);
  }
  
  public byte[] getObjectXML(java.lang.String pid) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.getObjectXML(pid);
  }
  
  public byte[] export(java.lang.String pid, java.lang.String format, java.lang.String context) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.export(pid, format, context);
  }
  
  public java.lang.String purgeObject(java.lang.String pid, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.purgeObject(pid, logMessage, force);
  }
  
  public java.lang.String addDatastream(java.lang.String pid, java.lang.String dsID, java.lang.String[] altIDs, java.lang.String dsLabel, boolean versionable, java.lang.String MIMEType, java.lang.String formatURI, java.lang.String dsLocation, java.lang.String controlGroup, java.lang.String dsState, java.lang.String checksumType, java.lang.String checksum, java.lang.String logMessage) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.addDatastream(pid, dsID, altIDs, dsLabel, versionable, MIMEType, formatURI, dsLocation, controlGroup, dsState, checksumType, checksum, logMessage);
  }
  
  public java.lang.String modifyDatastreamByReference(java.lang.String pid, java.lang.String dsID, java.lang.String[] altIDs, java.lang.String dsLabel, java.lang.String MIMEType, java.lang.String formatURI, java.lang.String dsLocation, java.lang.String checksumType, java.lang.String checksum, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.modifyDatastreamByReference(pid, dsID, altIDs, dsLabel, MIMEType, formatURI, dsLocation, checksumType, checksum, logMessage, force);
  }
  
  public java.lang.String modifyDatastreamByValue(java.lang.String pid, java.lang.String dsID, java.lang.String[] altIDs, java.lang.String dsLabel, java.lang.String MIMEType, java.lang.String formatURI, byte[] dsContent, java.lang.String checksumType, java.lang.String checksum, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.modifyDatastreamByValue(pid, dsID, altIDs, dsLabel, MIMEType, formatURI, dsContent, checksumType, checksum, logMessage, force);
  }
  
  public java.lang.String setDatastreamState(java.lang.String pid, java.lang.String dsID, java.lang.String dsState, java.lang.String logMessage) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.setDatastreamState(pid, dsID, dsState, logMessage);
  }
  
  public java.lang.String setDatastreamVersionable(java.lang.String pid, java.lang.String dsID, boolean versionable, java.lang.String logMessage) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.setDatastreamVersionable(pid, dsID, versionable, logMessage);
  }
  
  public java.lang.String compareDatastreamChecksum(java.lang.String pid, java.lang.String dsID, java.lang.String versionDate) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.compareDatastreamChecksum(pid, dsID, versionDate);
  }
  
  public info.fedora.www.definitions._1._0.types.Datastream getDatastream(java.lang.String pid, java.lang.String dsID, java.lang.String asOfDateTime) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.getDatastream(pid, dsID, asOfDateTime);
  }
  
  public info.fedora.www.definitions._1._0.types.Datastream[] getDatastreams(java.lang.String pid, java.lang.String asOfDateTime, java.lang.String dsState) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.getDatastreams(pid, asOfDateTime, dsState);
  }
  
  public info.fedora.www.definitions._1._0.types.Datastream[] getDatastreamHistory(java.lang.String pid, java.lang.String dsID) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.getDatastreamHistory(pid, dsID);
  }
  
  public java.lang.String[] purgeDatastream(java.lang.String pid, java.lang.String dsID, java.lang.String startDT, java.lang.String endDT, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.purgeDatastream(pid, dsID, startDT, endDT, logMessage, force);
  }
  
  public java.lang.String[] getNextPID(org.apache.axis.types.NonNegativeInteger numPIDs, java.lang.String pidNamespace) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.getNextPID(numPIDs, pidNamespace);
  }
  
  public info.fedora.www.definitions._1._0.types.RelationshipTuple[] getRelationships(java.lang.String pid, java.lang.String relationship) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.getRelationships(pid, relationship);
  }
  
  public boolean addRelationship(java.lang.String pid, java.lang.String relationship, java.lang.String object, boolean isLiteral, java.lang.String datatype) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.addRelationship(pid, relationship, object, isLiteral, datatype);
  }
  
  public boolean purgeRelationship(java.lang.String pid, java.lang.String relationship, java.lang.String object, boolean isLiteral, java.lang.String datatype) throws java.rmi.RemoteException{
    if (fedoraAPIM == null)
      _initFedoraAPIMProxy();
    return fedoraAPIM.purgeRelationship(pid, relationship, object, isLiteral, datatype);
  }
  
  
}