// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.simplefileio.runtime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.talend.components.adapter.beam.coders.LazyAvroCoder;
import org.talend.components.processing.definition.pocaggregate.PocAggregateProperties;
import org.talend.components.processing.runtime.pocaggregate.PocAggregateRuntime;
import org.talend.components.simplefileio.SimpleFileIODatasetProperties;
import org.talend.components.simplefileio.SimpleFileIODatastoreProperties;
import org.talend.components.simplefileio.SimpleFileIOFormat;
import org.talend.components.simplefileio.input.SimpleFileIOInputProperties;
import org.talend.components.simplefileio.output.SimpleFileIOOutputProperties;
import org.talend.components.test.SparkIntegrationTestResource;

/**
 * Unit tests for {@link SimpleFileIOOutputRuntime} using the Spark runner.
 */
public class PocAggregateTestIT {

    /** Resource that provides a {@link Pipeline} configured for Spark. */
    @Rule
    public SparkIntegrationTestResource spark = SparkIntegrationTestResource.ofDefault();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /**
     * @return the properties for this datastore, fully initialized with the default values.
     */
    public static SimpleFileIODatastoreProperties createDatastoreProperties() {
        // Configure the datastore.
        SimpleFileIODatastoreProperties datastoreProps = new SimpleFileIODatastoreProperties(null);
        datastoreProps.init();
        return datastoreProps;
    }

    /**
     * @return the properties for this dataset, fully initialized with the default values.
     */
    public static SimpleFileIODatasetProperties createDatasetProperties() {
        // Configure the dataset.
        SimpleFileIODatasetProperties datasetProps = new SimpleFileIODatasetProperties(null);
        datasetProps.init();
        datasetProps.setDatastoreProperties(createDatastoreProperties());
        return datasetProps;
    }

    /**
     * @return the properties for this component, fully initialized with the default values.
     */
    public static SimpleFileIOInputProperties createInputComponentProperties() {
        // Configure the component.
        SimpleFileIOInputProperties inputProps = new SimpleFileIOInputProperties(null);
        inputProps.init();
        inputProps.setDatasetProperties(createDatasetProperties());
        return inputProps;
    }

    /**
     * @return the properties for this component, fully initialized with the default values.
     */
    public static SimpleFileIOOutputProperties createOutputComponentProperties() {
        // Configure the component.
        SimpleFileIOOutputProperties outputProps = new SimpleFileIOOutputProperties(null);
        outputProps.init();
        outputProps.setDatasetProperties(createDatasetProperties());
        return outputProps;
    }

    /**
     * Basic unit test using all default values (except for the path) on an in-memory DFS cluster.
     */
    @Category(ValidatesRunner.class)
    @Test
    public void testBasicDefaults() throws IOException {
        // Use the resource to create the pipeline.
        final Pipeline p = spark.createPipeline();

        final FileSystem fs = FileSystem.get(spark.createHadoopConfiguration());

        // ==================================
        // LazyAvroCoder.setSchemaRegistry();

        // Configure the input.
        final PCollection<IndexedRecord> in;
        {
            SimpleFileIOInputProperties props = createInputComponentProperties();
            props.getDatasetProperties().path.setValue("/user/talend/recordSimpleIn");
            props.getDatasetProperties().format.setValue(SimpleFileIOFormat.AVRO);

            // Create the runtime.
            SimpleFileIOInputRuntime runtime = new SimpleFileIOInputRuntime();
            runtime.initialize(null, props);
            in = p.apply(runtime);
        }

        final PCollection<IndexedRecord> pocAggregate;
        {
            PocAggregateProperties props = new PocAggregateProperties(null);
            props.init();
            PocAggregateRuntime runtime = new PocAggregateRuntime();
            runtime.initialize(null, props);
            // pocAggregate = p.apply(runtime);
            pocAggregate = in;
        }

        // Configure the output
        {
            SimpleFileIOOutputProperties props = createOutputComponentProperties();
            props.overwrite.setValue(true);
            props.getDatasetProperties().path.setValue("/user/talend/recordSimpleIn");
            props.getDatasetProperties().format.setValue(SimpleFileIOFormat.CSV);

            // Create the runtime.
            SimpleFileIOOutputRuntime runtime = new SimpleFileIOOutputRuntime();
            runtime.initialize(null, props);
            pocAggregate.apply(runtime);
        }

        // SCHEMA REGISTRY RUN ==================================

        // And run the test.
        p.run().waitUntilFinish();

        // SCHEMA REGISTRY RUN ==================================

        // ==================================
        // LazyAvroCoder.resetSchemaRegistry();


        // TODO: Check the expected values.
        String path = "/user/talend/recordsSimpleOut/part-00000-of-00000";
        List<IndexedRecord> found = new ArrayList<>();
        try (DataFileStream<GenericRecord> reader = new DataFileStream<GenericRecord>(
                new BufferedInputStream(fs.open(new Path(path))), new GenericDatumReader<GenericRecord>())) {
            while (reader.hasNext()) {
                found.add(reader.iterator().next());
            }
        }
        assertThat(found, not(hasSize(0)));
    }

}
