package org.talend.components.salesforce.runtime.dataprep;

import java.util.Arrays;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Assert;
import org.junit.Test;
import org.talend.components.salesforce.dataset.SalesforceModuleProperties;
import org.talend.components.salesforce.datastore.SalesforceConnectionDefinition;
import org.talend.components.salesforce.datastore.SalesforceConnectionProperties;
import org.talend.daikon.java8.Consumer;

public class SalesforceDatasetRuntimeTestIT {

    @Test
    public void testGetSchemaForModule() {
        SalesforceModuleProperties dataset = createDatasetPropertiesForModule();
        dataset.selectColumnIds.setValue(Arrays.asList("IsDeleted", "Id"));

        SalesforceDatasetRuntime runtime = new SalesforceDatasetRuntime();
        runtime.initialize(null, dataset);
        Schema schema = runtime.getSchema();

        Assert.assertNotNull(schema);
        Assert.assertTrue("empty schema", schema.getFields().size() > 0);
    }

    @Test
    public void testGetSchemaForQuery() {
        SalesforceModuleProperties dataset = createDatasetPropertiesForQuery();

        SalesforceDatasetRuntime runtime = new SalesforceDatasetRuntime();
        runtime.initialize(null, dataset);
        Schema schema = runtime.getSchema();

        Assert.assertNotNull(schema);
        Assert.assertTrue("empty schema", schema.getFields().size() > 0);
    }

    @Test
    public void testGetSampleForModule() {
        SalesforceModuleProperties dataset = createDatasetPropertiesForModule();
        dataset.selectColumnIds.setValue(Arrays.asList("IsDeleted", "Id"));
        getSampleAction(dataset);
    }

    @Test
    public void testGetSampleForQuery() {
        SalesforceModuleProperties dataset = createDatasetPropertiesForQuery();
        getSampleAction(dataset);
    }

    private void getSampleAction(SalesforceModuleProperties dataset) {
        SalesforceDatasetRuntime runtime = new SalesforceDatasetRuntime();
        runtime.initialize(null, dataset);
        final IndexedRecord[] record = new IndexedRecord[1];
        Consumer<IndexedRecord> storeTheRecords = new Consumer<IndexedRecord>() {

            @Override
            public void accept(IndexedRecord data) {
                record[0] = data;
            }
        };

        runtime.getSample(1, storeTheRecords);
        Assert.assertTrue("empty result", record.length > 0);
    }

    private SalesforceModuleProperties createDatasetPropertiesForModule() {
        SalesforceConnectionDefinition def = new SalesforceConnectionDefinition();
        SalesforceConnectionProperties datastore = new SalesforceConnectionProperties("datastore");

        CommonTestUtils.setValueForDatastoreProperties(datastore);

        SalesforceModuleProperties dataset = (SalesforceModuleProperties) def.createDatasetProperties(datastore);
        dataset.moduleName.setValue("Account");

        return dataset;
    }

    private SalesforceModuleProperties createDatasetPropertiesForQuery() {
        SalesforceConnectionDefinition def = new SalesforceConnectionDefinition();
        SalesforceConnectionProperties datastore = new SalesforceConnectionProperties("datastore");

        CommonTestUtils.setValueForDatastoreProperties(datastore);

        SalesforceModuleProperties dataset = (SalesforceModuleProperties) def.createDatasetProperties(datastore);
        dataset.sourceType.setValue(SalesforceModuleProperties.SourceType.SOQL_QUERY);
        dataset.query.setValue("SELECT Id, Name FROM Account");

        return dataset;
    }
}
