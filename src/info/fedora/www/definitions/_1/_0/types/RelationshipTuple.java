/**
 * RelationshipTuple.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package info.fedora.www.definitions._1._0.types;

public class RelationshipTuple  implements java.io.Serializable {
    private java.lang.String subject;

    private java.lang.String predicate;

    private java.lang.String object;

    private boolean isLiteral;

    private java.lang.String datatype;

    public RelationshipTuple() {
    }

    public RelationshipTuple(
           java.lang.String subject,
           java.lang.String predicate,
           java.lang.String object,
           boolean isLiteral,
           java.lang.String datatype) {
           this.subject = subject;
           this.predicate = predicate;
           this.object = object;
           this.isLiteral = isLiteral;
           this.datatype = datatype;
    }


    /**
     * Gets the subject value for this RelationshipTuple.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this RelationshipTuple.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }


    /**
     * Gets the predicate value for this RelationshipTuple.
     * 
     * @return predicate
     */
    public java.lang.String getPredicate() {
        return predicate;
    }


    /**
     * Sets the predicate value for this RelationshipTuple.
     * 
     * @param predicate
     */
    public void setPredicate(java.lang.String predicate) {
        this.predicate = predicate;
    }


    /**
     * Gets the object value for this RelationshipTuple.
     * 
     * @return object
     */
    public java.lang.String getObject() {
        return object;
    }


    /**
     * Sets the object value for this RelationshipTuple.
     * 
     * @param object
     */
    public void setObject(java.lang.String object) {
        this.object = object;
    }


    /**
     * Gets the isLiteral value for this RelationshipTuple.
     * 
     * @return isLiteral
     */
    public boolean isIsLiteral() {
        return isLiteral;
    }


    /**
     * Sets the isLiteral value for this RelationshipTuple.
     * 
     * @param isLiteral
     */
    public void setIsLiteral(boolean isLiteral) {
        this.isLiteral = isLiteral;
    }


    /**
     * Gets the datatype value for this RelationshipTuple.
     * 
     * @return datatype
     */
    public java.lang.String getDatatype() {
        return datatype;
    }


    /**
     * Sets the datatype value for this RelationshipTuple.
     * 
     * @param datatype
     */
    public void setDatatype(java.lang.String datatype) {
        this.datatype = datatype;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RelationshipTuple)) return false;
        RelationshipTuple other = (RelationshipTuple) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject()))) &&
            ((this.predicate==null && other.getPredicate()==null) || 
             (this.predicate!=null &&
              this.predicate.equals(other.getPredicate()))) &&
            ((this.object==null && other.getObject()==null) || 
             (this.object!=null &&
              this.object.equals(other.getObject()))) &&
            this.isLiteral == other.isIsLiteral() &&
            ((this.datatype==null && other.getDatatype()==null) || 
             (this.datatype!=null &&
              this.datatype.equals(other.getDatatype())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        if (getPredicate() != null) {
            _hashCode += getPredicate().hashCode();
        }
        if (getObject() != null) {
            _hashCode += getObject().hashCode();
        }
        _hashCode += (isIsLiteral() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getDatatype() != null) {
            _hashCode += getDatatype().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RelationshipTuple.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.fedora.info/definitions/1/0/types/", "RelationshipTuple"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("predicate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "predicate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("object");
        elemField.setXmlName(new javax.xml.namespace.QName("", "object"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isLiteral");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isLiteral"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("datatype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "datatype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
