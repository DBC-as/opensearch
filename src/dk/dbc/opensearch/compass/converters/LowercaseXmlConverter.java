/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * \file LowercaseXmlConverter.java
 * \brief Lowercases the string marshalled from the xml
 */

package dk.dbc.opensearch.compass.converters;


import org.apache.log4j.Logger;
import org.compass.core.Property;
import org.compass.core.Resource;
import org.compass.core.converter.ConversionException;
import org.compass.core.converter.Converter;
import org.compass.core.converter.xsem.SimpleXmlValueConverter;
import org.compass.core.engine.naming.PropertyPath;
import org.compass.core.mapping.Mapping;
import org.compass.core.mapping.xsem.XmlPropertyMapping;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.xml.XmlObject;

/**
 * Converter used to lowercase Strings. extends the Compass
 * SimpleXmlValueConverter
 */
public class LowercaseXmlConverter extends SimpleXmlValueConverter {
    
    Logger log = Logger.getLogger( LowercaseXmlConverter.class );

    /**
     * Marshalls ROOT to RESOURCE. overrides method in
     * SimpleXmlValueConverter and lowercases string, before storing
     * it.
     *
     * @param resource The resource to marhsall the object to
     * @param root     The Object to marshall to the resource
     * @param mapping  The mapping definition of how to marshall the Object to the resoruce
     * @param context  The context for the current marhslling process
     * @return true if data was saved in the the index that can be read.
     * @throws ConversionException
     */
    @Override
    public boolean marshall(Resource resource, Object root, Mapping mapping, MarshallingContext context) throws ConversionException 
    {
        
        XmlPropertyMapping xmlPropertyMapping = (XmlPropertyMapping) mapping;
        // don't save a null value if the context does not states so
        if (root == null && !handleNulls(xmlPropertyMapping, context)) {
            return false;
        }
        XmlObject xmlObject = (XmlObject) root;
        String sValue = getNullValue(xmlPropertyMapping, context);
        if (root != null) {
            sValue = toString(xmlObject, xmlPropertyMapping);
            sValue = sValue.toLowerCase(); // lowercasing string
            
        }
        PropertyPath path = xmlPropertyMapping.getPath();
        String propertyName = path == null ? null : path.getPath();
        if (propertyName == null) {
            if (xmlObject == null) {
                // nothing we can do here, no name, no nothing...
                return false;
            }
            propertyName = xmlObject.getName();
        }
        Property p = context.getResourceFactory().createProperty(propertyName, sValue, xmlPropertyMapping);
        doSetBoost(p, root, xmlPropertyMapping, context);
        resource.addProperty(p);

        return xmlPropertyMapping.getStore() != Property.Store.NO;
    }
}
