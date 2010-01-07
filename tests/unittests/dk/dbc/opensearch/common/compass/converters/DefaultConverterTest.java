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
 * \file DefaultConverterTest.java
 * \brief Tests the default conversion
 */


package dk.dbc.opensearch.common.compass.converters;

import mockit.Mockit;
import org.compass.core.Property;
import org.compass.core.Resource;
import org.compass.core.ResourceFactory;
import org.compass.core.converter.xsem.SimpleXmlValueConverter;
import org.compass.core.engine.naming.PropertyPath;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.mapping.xsem.XmlPropertyMapping;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.xml.XmlObject;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 *
 */
public class DefaultConverterTest {

    static String expectedNullValue = "NULLVALUE";

    // Test String for conversion
    static String inputValue  = "T\uA732\uA733eststr";
    static String expectedOutput = "TAAaaeststr TÅåeststr";

    // mockObjects
    public static class MockSimpleXmlValueConverterTrue {
        static boolean handle = true;
        static public boolean handleNulls(ResourcePropertyMapping mapping, MarshallingContext context)
        {
            return handle;
        }

        static public String toString(XmlObject xmlObject, ResourcePropertyMapping mapping)
        {
            return inputValue;
        }

        static public void doSetBoost(Property property, Object root, ResourcePropertyMapping mapping, MarshallingContext context){}

        public String getNullValue(ResourcePropertyMapping mapping, MarshallingContext context)
        {
            return expectedNullValue;
        }
    }

    XmlPropertyMapping mockMapping = createMock(XmlPropertyMapping.class);
    PropertyPath mockPropertyPath = createMock(PropertyPath.class);
    XmlObject mockXmlObject = createMock(XmlObject.class);
    MarshallingContext mockContext = createMock(MarshallingContext.class);
    ResourceFactory mockFactory = createMock(ResourceFactory.class);
    Property mockProperty = createMock(Property.class);
    Resource mockResource = createMock(Resource.class);

    /**
     * Tests the default conversion
     */
    @Test
    public void testDefaultConversion() {
     MockSimpleXmlValueConverterTrue mockSimpleXmlValueConverter = new MockSimpleXmlValueConverterTrue();
        Mockit.redefineMethods(SimpleXmlValueConverter.class, mockSimpleXmlValueConverter);
        String propertyName = "TEST";

        DefaultConverter instance = new DefaultConverter();

        expect(mockMapping.getPath()).andReturn(mockPropertyPath);
        expect(mockMapping.getStore()).andReturn(Property.Store.YES);
        replay(mockMapping);

        expect(mockPropertyPath.getPath()).andReturn(null);
        replay(mockPropertyPath);

        expect(mockXmlObject.getName()).andReturn(propertyName);
        replay(mockXmlObject);

        expect(mockContext.getResourceFactory()).andReturn(mockFactory);
        replay(mockContext);

        // this is the actual test.. This is the expected call to save
        // the output after conversion, and the code line below
        // expects the output saved matches the expectedOutput
        expect(mockFactory.createProperty(propertyName, expectedOutput, mockMapping)).andReturn(mockProperty);
        replay(mockFactory);

        expect(mockResource.addProperty(mockProperty)).andReturn(mockResource);
        replay(mockResource);

        assertTrue(instance.marshall(mockResource, mockXmlObject, mockMapping, mockContext));

        verify(mockMapping);
        verify(mockPropertyPath);
        verify(mockContext);
        verify(mockFactory);
        verify(mockResource);
        verify(mockXmlObject);

        reset(mockMapping);
        reset(mockPropertyPath);
        reset(mockContext);
        reset(mockFactory);
        reset(mockResource);
        reset(mockXmlObject);
    }
}
