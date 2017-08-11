package org.talend.components.salesforce.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.talend.components.salesforce.SalesforceDatastoreProperties2;
import org.talend.components.salesforce.TestUtils;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;
import org.talend.daikon.serialize.SerializerDeserializer;

public class SalesforceConnectionMigrationTest {

    @Test
    public void testSalesforceConnectionPropertiesMigration() throws IOException {
        SerializerDeserializer.Deserialized<SalesforceDatastoreProperties2> deser = SerializerDeserializer.fromSerialized(
                TestUtils.getResourceAsString(getClass(),"tSalesforceConnectionProperties_621.json"), SalesforceDatastoreProperties2.class, null,
                SerializerDeserializer.PERSISTENT);
        assertTrue("should be true, but not", deser.migrated);
        SalesforceDatastoreProperties2 properties = deser.object;
        String apiVersion = properties.apiVersion.getValue();
        assertEquals("\"34.0\"", apiVersion);
    }

    @Test
    public void testSalesforceInputPropertiesMigration() throws IOException {
        SerializerDeserializer.Deserialized<TSalesforceInputProperties> deser = SerializerDeserializer.fromSerialized(
                TestUtils.getResourceAsString(getClass(),"tSalesforceInputProperties_621.json"), TSalesforceInputProperties.class, null,
                SerializerDeserializer.PERSISTENT);
        assertTrue("should be true, but not", deser.migrated);
        TSalesforceInputProperties properties = deser.object;
        String apiVersion = properties.datastore.apiVersion.getValue();
        assertEquals("\"34.0\"", apiVersion);
    }
}