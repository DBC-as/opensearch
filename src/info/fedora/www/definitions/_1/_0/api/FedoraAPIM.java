/**
 * FedoraAPIM.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package info.fedora.www.definitions._1._0.api;

public interface FedoraAPIM extends java.rmi.Remote {
    public java.lang.String ingest(byte[] objectXML, java.lang.String format, java.lang.String logMessage) throws java.rmi.RemoteException;
    public java.lang.String modifyObject(java.lang.String pid, java.lang.String state, java.lang.String label, java.lang.String ownerId, java.lang.String logMessage) throws java.rmi.RemoteException;
    public byte[] getObjectXML(java.lang.String pid) throws java.rmi.RemoteException;
    public byte[] export(java.lang.String pid, java.lang.String format, java.lang.String context) throws java.rmi.RemoteException;
    public java.lang.String purgeObject(java.lang.String pid, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException;
    public java.lang.String addDatastream(java.lang.String pid, java.lang.String dsID, java.lang.String[] altIDs, java.lang.String dsLabel, boolean versionable, java.lang.String MIMEType, java.lang.String formatURI, java.lang.String dsLocation, java.lang.String controlGroup, java.lang.String dsState, java.lang.String checksumType, java.lang.String checksum, java.lang.String logMessage) throws java.rmi.RemoteException;
    public java.lang.String modifyDatastreamByReference(java.lang.String pid, java.lang.String dsID, java.lang.String[] altIDs, java.lang.String dsLabel, java.lang.String MIMEType, java.lang.String formatURI, java.lang.String dsLocation, java.lang.String checksumType, java.lang.String checksum, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException;
    public java.lang.String modifyDatastreamByValue(java.lang.String pid, java.lang.String dsID, java.lang.String[] altIDs, java.lang.String dsLabel, java.lang.String MIMEType, java.lang.String formatURI, byte[] dsContent, java.lang.String checksumType, java.lang.String checksum, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException;
    public java.lang.String setDatastreamState(java.lang.String pid, java.lang.String dsID, java.lang.String dsState, java.lang.String logMessage) throws java.rmi.RemoteException;
    public java.lang.String setDatastreamVersionable(java.lang.String pid, java.lang.String dsID, boolean versionable, java.lang.String logMessage) throws java.rmi.RemoteException;
    public java.lang.String compareDatastreamChecksum(java.lang.String pid, java.lang.String dsID, java.lang.String versionDate) throws java.rmi.RemoteException;
    public info.fedora.www.definitions._1._0.types.Datastream getDatastream(java.lang.String pid, java.lang.String dsID, java.lang.String asOfDateTime) throws java.rmi.RemoteException;
    public info.fedora.www.definitions._1._0.types.Datastream[] getDatastreams(java.lang.String pid, java.lang.String asOfDateTime, java.lang.String dsState) throws java.rmi.RemoteException;
    public info.fedora.www.definitions._1._0.types.Datastream[] getDatastreamHistory(java.lang.String pid, java.lang.String dsID) throws java.rmi.RemoteException;
    public java.lang.String[] purgeDatastream(java.lang.String pid, java.lang.String dsID, java.lang.String startDT, java.lang.String endDT, java.lang.String logMessage, boolean force) throws java.rmi.RemoteException;
    public java.lang.String[] getNextPID(org.apache.axis.types.NonNegativeInteger numPIDs, java.lang.String pidNamespace) throws java.rmi.RemoteException;
    public info.fedora.www.definitions._1._0.types.RelationshipTuple[] getRelationships(java.lang.String pid, java.lang.String relationship) throws java.rmi.RemoteException;
    public boolean addRelationship(java.lang.String pid, java.lang.String relationship, java.lang.String object, boolean isLiteral, java.lang.String datatype) throws java.rmi.RemoteException;
    public boolean purgeRelationship(java.lang.String pid, java.lang.String relationship, java.lang.String object, boolean isLiteral, java.lang.String datatype) throws java.rmi.RemoteException;
}
