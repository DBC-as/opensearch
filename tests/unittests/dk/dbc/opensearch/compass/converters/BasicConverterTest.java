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
 * \file BasicConverterTest.java
 * \brief Tests the common basics for the different converters
 */

package dk.dbc.opensearch.compass.converters;

import java.util.ArrayList;
import mockit.Mockit;
import org.compass.core.Property;
import org.compass.core.Resource;
import org.compass.core.ResourceFactory;
import org.compass.core.converter.xsem.SimpleXmlValueConverter;
import org.compass.core.engine.naming.PropertyPath;
import org.compass.core.mapping.Mapping;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.mapping.xsem.XmlPropertyMapping;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.xml.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Tests the common basics for the different converters.
 * The conversions themselves are tested in classes named after the converters.
 * This test concentrates around the common framework for the converters
 */
public class BasicConverterTest {

    static ArrayList<SimpleXmlValueConverter> instances = new ArrayList<SimpleXmlValueConverter>();
    static String expectedNullValue = "NULLVALUE";
    static String strValue  = "teststring";


    public static class MockSimpleXmlValueConverterFalse {
        static boolean handle = false;
        static public boolean handleNulls(ResourcePropertyMapping mapping, MarshallingContext context)
        {
            return handle;
        }

        public String getNullValue(ResourcePropertyMapping mapping, MarshallingContext context)
        {
            return expectedNullValue;
        }
    }

    public static class MockSimpleXmlValueConverterTrue {
        static boolean handle = true;
        static public boolean handleNulls(ResourcePropertyMapping mapping, MarshallingContext context)
        {
            return handle;
        }
        static public String toString(XmlObject xmlObject, ResourcePropertyMapping mapping)
        {
            return strValue;
        }

        static public void doSetBoost(Property property, Object root, ResourcePropertyMapping mapping, MarshallingContext context){}

        public String getNullValue(ResourcePropertyMapping mapping, MarshallingContext context)
        {
            return expectedNullValue;
        }
    }

    // mockObjects
    XmlPropertyMapping mockMapping = createMock(XmlPropertyMapping.class);
    PropertyPath mockPropertyPath = createMock(PropertyPath.class);
    XmlObject mockXmlObject = createMock(XmlObject.class);
    MarshallingContext mockContext = createMock(MarshallingContext.class);
    ResourceFactory mockFactory = createMock(ResourceFactory.class);
    Property mockProperty = createMock(Property.class);
    Resource mockResource = createMock(Resource.class);
    

    @BeforeClass
    public static void setUpClass() throws Exception 
    {
        instances.add(new SortConverter());
        instances.add(new DefaultConverter());
        instances.add(new FacetConverter());
        instances.add(new LowercaseXmlConverter());
        instances.add(new PhraseConverter());
    }

    @Test
    public void testBasicConverter_rootNull_handleNullFalse()
    {
        MockSimpleXmlValueConverterFalse mockSimpleXmlValueConverter1 = new MockSimpleXmlValueConverterFalse();
        Mockit.redefineMethods(SimpleXmlValueConverter.class, mockSimpleXmlValueConverter1);
        Resource resource = null;
        Object root = null;
        Mapping mapping = null;
        MarshallingContext context = null;

        for(SimpleXmlValueConverter instance : instances)
        {
            assertFalse(instance.marshall(resource, root, mapping, context));
        }
    }

    @Test
    public void testBasicConverter_rootNull_handleNullTrue()
    {
        MockSimpleXmlValueConverterTrue mockSimpleXmlValueConverter = new MockSimpleXmlValueConverterTrue();
        Mockit.redefineMethods(SimpleXmlValueConverter.class, mockSimpleXmlValueConverter);
        Resource resource = null;
        Object root = null;
        MarshallingContext context = null;

        for(SimpleXmlValueConverter instance : instances)
        {
            expect(mockMapping.getPath()).andReturn(mockPropertyPath);
            replay(mockMapping);

            expect(mockPropertyPath.getPath()).andReturn(null);
            replay(mockPropertyPath);
            //SortConverter instance = new SortConverter();
            assertFalse(instance.marshall(resource, root, mockMapping, context));
            verify(mockMapping);
            verify(mockPropertyPath);

            reset(mockMapping);
            reset(mockPropertyPath);
        }
    }

    @Test
    public void testBasicConverter_propertyNotNull_handleNullTrue_StoreNO()
    {
        MockSimpleXmlValueConverterTrue mockSimpleXmlValueConverter = new MockSimpleXmlValueConverterTrue();
        Mockit.redefineMethods(SimpleXmlValueConverter.class, mockSimpleXmlValueConverter);
        Object root = null;
        String propertyName = "TEST";

        for(SimpleXmlValueConverter instance : instances)
        {
            expect(mockMapping.getPath()).andReturn(mockPropertyPath);
            expect(mockMapping.getStore()).andReturn(Property.Store.NO);
            replay(mockMapping);

            expect(mockPropertyPath.getPath()).andReturn(propertyName);
            replay(mockPropertyPath);

            expect(mockContext.getResourceFactory()).andReturn(mockFactory);
            replay(mockContext);

            expect(mockFactory.createProperty(propertyName, expectedNullValue, mockMapping)).andReturn(mockProperty);
            replay(mockFactory);

            expect(mockResource.addProperty(mockProperty)).andReturn(mockResource);
            replay(mockResource);

            assertFalse(instance.marshall(mockResource, root, mockMapping, mockContext));
            verify(mockMapping);
            verify(mockPropertyPath);
            verify(mockContext);
            verify(mockFactory);
            verify(mockResource);

            reset(mockMapping);
            reset(mockPropertyPath);
            reset(mockContext);
            reset(mockFactory);
            reset(mockResource);
        }
    }

    @Test
    public void testBasicConverter_propertyNotNull_handleNullTrue_StoreYES()
    {
        MockSimpleXmlValueConverterTrue mockSimpleXmlValueConverter = new MockSimpleXmlValueConverterTrue();
        Mockit.redefineMethods(SimpleXmlValueConverter.class, mockSimpleXmlValueConverter);
        Object root = null;
        String propertyName = "TEST";

        for(SimpleXmlValueConverter instance : instances)
        {
            expect(mockMapping.getPath()).andReturn(mockPropertyPath);
            expect(mockMapping.getStore()).andReturn(Property.Store.YES);
            replay(mockMapping);

            expect(mockPropertyPath.getPath()).andReturn(propertyName);
            replay(mockPropertyPath);

            expect(mockContext.getResourceFactory()).andReturn(mockFactory);
            replay(mockContext);

            expect(mockFactory.createProperty(propertyName, expectedNullValue, mockMapping)).andReturn(mockProperty);
            replay(mockFactory);

            expect(mockResource.addProperty(mockProperty)).andReturn(mockResource);
            replay(mockResource);

            assertTrue(instance.marshall(mockResource, root, mockMapping, mockContext));
            verify(mockMapping);
            verify(mockPropertyPath);
            verify(mockContext);
            verify(mockFactory);
            verify(mockResource);

            reset(mockMapping);
            reset(mockPropertyPath);
            reset(mockContext);
            reset(mockFactory);
            reset(mockResource);
        }
    }

    @Test
    public void testBasicConverter_PropertyNameIsNull()
    {
        MockSimpleXmlValueConverterTrue mockSimpleXmlValueConverter = new MockSimpleXmlValueConverterTrue();
        Mockit.redefineMethods(SimpleXmlValueConverter.class, mockSimpleXmlValueConverter);
        Object root = null;
        MarshallingContext context = null;
        
        for(SimpleXmlValueConverter instance : instances)
        {
            expect(mockMapping.getPath()).andReturn(null);
            replay(mockMapping);
            assertFalse(instance.marshall(mockResource, root, mockMapping, mockContext));
            verify(mockMapping);
            reset(mockMapping);
        }
    }

    @Test
    public void testBasicConverter_xmlObjectNull()
    {
        MockSimpleXmlValueConverterTrue mockSimpleXmlValueConverter = new MockSimpleXmlValueConverterTrue();
        Mockit.redefineMethods(SimpleXmlValueConverter.class, mockSimpleXmlValueConverter);
        String propertyName = "TEST";

        for(SimpleXmlValueConverter instance : instances)
        {
            expect(mockMapping.getPath()).andReturn(mockPropertyPath);
            expect(mockMapping.getStore()).andReturn(Property.Store.YES);
            replay(mockMapping);

            expect(mockPropertyPath.getPath()).andReturn(null);
            replay(mockPropertyPath);

            expect(mockXmlObject.getName()).andReturn(propertyName);
            replay(mockXmlObject);

            expect(mockContext.getResourceFactory()).andReturn(mockFactory);
            replay(mockContext);

            expect(mockFactory.createProperty(propertyName, strValue, mockMapping)).andReturn(mockProperty);
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
}