package org.talend.components.processing.typeconverter;

import org.junit.Test;
import org.talend.components.processing.window.WindowProperties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class TypeConverterPropertiesTest {

    @Test
    public void testDefaultProperties() {
        TypeConverterProperties properties = new TypeConverterProperties("test");
        assertThat(properties.converters.getDefaultProperties(), notNullValue());
        assertThat(properties.getAllSchemaPropertiesConnectors(true), contains(nullValue()));
        assertThat(properties.getAllSchemaPropertiesConnectors(false), contains(nullValue()));
    }
}