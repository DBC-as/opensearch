/**
 * FedoraAPIMServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package info.fedora.www.definitions._1._0.api;

public class FedoraAPIMServiceLocator extends org.apache.axis.client.Service implements info.fedora.www.definitions._1._0.api.FedoraAPIMService {

    public FedoraAPIMServiceLocator() {
    }


    public FedoraAPIMServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FedoraAPIMServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FedoraAPIMServiceHTTPPort
    private java.lang.String FedoraAPIMServiceHTTPPort_address = "http://sempu:8080/fedora/services/management";

    public java.lang.String getFedoraAPIMServiceHTTPPortAddress() {
        return FedoraAPIMServiceHTTPPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FedoraAPIMServiceHTTPPortWSDDServiceName = "Fedora-API-M-Service-HTTP-Port";

    public java.lang.String getFedoraAPIMServiceHTTPPortWSDDServiceName() {
        return FedoraAPIMServiceHTTPPortWSDDServiceName;
    }

    public void setFedoraAPIMServiceHTTPPortWSDDServiceName(java.lang.String name) {
        FedoraAPIMServiceHTTPPortWSDDServiceName = name;
    }

    public info.fedora.www.definitions._1._0.api.FedoraAPIM getFedoraAPIMServiceHTTPPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FedoraAPIMServiceHTTPPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFedoraAPIMServiceHTTPPort(endpoint);
    }

    public info.fedora.www.definitions._1._0.api.FedoraAPIM getFedoraAPIMServiceHTTPPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            info.fedora.www.definitions._1._0.api.FedoraAPIMBindingSOAPHTTPStub _stub = new info.fedora.www.definitions._1._0.api.FedoraAPIMBindingSOAPHTTPStub(portAddress, this);
            _stub.setPortName(getFedoraAPIMServiceHTTPPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFedoraAPIMServiceHTTPPortEndpointAddress(java.lang.String address) {
        FedoraAPIMServiceHTTPPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (info.fedora.www.definitions._1._0.api.FedoraAPIM.class.isAssignableFrom(serviceEndpointInterface)) {
                info.fedora.www.definitions._1._0.api.FedoraAPIMBindingSOAPHTTPStub _stub = new info.fedora.www.definitions._1._0.api.FedoraAPIMBindingSOAPHTTPStub(new java.net.URL(FedoraAPIMServiceHTTPPort_address), this);
                _stub.setPortName(getFedoraAPIMServiceHTTPPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Fedora-API-M-Service-HTTP-Port".equals(inputPortName)) {
            return getFedoraAPIMServiceHTTPPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.fedora.info/definitions/1/0/api/", "Fedora-API-M-Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.fedora.info/definitions/1/0/api/", "Fedora-API-M-Service-HTTP-Port"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FedoraAPIMServiceHTTPPort".equals(portName)) {
            setFedoraAPIMServiceHTTPPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
