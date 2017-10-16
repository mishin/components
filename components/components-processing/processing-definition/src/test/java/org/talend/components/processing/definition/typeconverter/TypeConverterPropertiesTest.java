package org.talend.components.processing.definition.typeconverter;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TypeConverterPropertiesTest {

    @Test
    public void testDefaultProperties() {
        TypeConverterProperties properties = new TypeConverterProperties("test");
        assertThat(properties.converters.getDefaultProperties(), notNullValue());
        assertThat(properties.getAllSchemaPropertiesConnectors(true), contains(nullValue()));
        assertThat(properties.getAllSchemaPropertiesConnectors(false), contains(nullValue()));
    }
}
