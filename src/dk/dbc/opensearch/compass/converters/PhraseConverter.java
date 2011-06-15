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
 * \file PhraseConverter.java
 * \brief Converts phrase fields
 */

package dk.dbc.opensearch.compass.converters;

import dk.dbc.commons.string.StringUtils;
import java.util.HashMap;

import org.compass.core.Property;
import org.compass.core.Resource;
import org.compass.core.converter.ConversionException;
import org.compass.core.converter.xsem.SimpleXmlValueConverter;
import org.compass.core.engine.naming.PropertyPath;
import org.compass.core.mapping.Mapping;
import org.compass.core.mapping.xsem.XmlPropertyMapping;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.xml.XmlObject;

/**
 * Normalizes phrase fields. it replaces "\uA732" with "Å" and "\uA733" with "å", and lowercases the string
 */
public class PhraseConverter extends SimpleXmlValueConverter
{
    /**
     * Marshalls root to resource. replaces characters and lowercases the string
     * @param resource The resource to marhsall the object to
     * @param root The Object to marshall to the resource
     * @param mapping The mapping definition of how to marshall the Object to the resoruce
     * @param context The context for the current marshalling process
     * @return true if data was saved
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
        String sValue = getNullValue( xmlPropertyMapping, context );
        if (root != null)
        {   // normalize string
            sValue = toString( xmlObject, xmlPropertyMapping );
            HashMap<String, String> replaceMap = new HashMap<String, String>();
            replaceMap.put("\uA732", "Å");
            replaceMap.put("\uA733", "å");
            sValue = StringUtils.replace(sValue, replaceMap);
            sValue = sValue.toLowerCase();
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
