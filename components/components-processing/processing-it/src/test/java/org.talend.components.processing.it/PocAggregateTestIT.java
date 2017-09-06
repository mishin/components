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
package org.talend.components.processing.it;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.api.java.JavaSparkContext;
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
import org.talend.components.simplefileio.runtime.SimpleFileIOInputRuntime;
import org.talend.components.simplefileio.runtime.SimpleFileIOOutputRuntime;
import org.talend.components.simplefileio.runtime.utils.FileSystemUtil;
import org.talend.datastreams.beam.compiler.runtimeflow.ServerSocketAvroSchemaRegistry;

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
    public void testBasicDefaults() throws IOException, URISyntaxException {

        // Use the resource to create the pipeline.
        final Pipeline p = spark.createPipeline();

        final String pathIn = "hdfs://talend-cdh580.weave.local:8020/user/talend/recordSimple/";
        final String pathOut = "hdfs://talend-cdh580.weave.local:8020/user/" + System.getProperty("user.name")
                + "/recordSimpleOut";

        // Upload the jars.
        Set<String> toUpload = new HashSet<>();
        toUpload.add("processing-definition-0.20.0-SNAPSHOT.jar");
        toUpload.add("processing-runtime-0.20.0-SNAPSHOT.jar");
        toUpload.add("simplefileio-definition-0.20.0-SNAPSHOT.jar");
        toUpload.add("simplefileio-runtime-0.20.0-SNAPSHOT.jar");
        toUpload.add("daikon-0.18.0-SNAPSHOT.jar");
        toUpload.add("beam-runners-spark-2.0.0.jar");
        toUpload.add("beam-sdks-java-core-2.0.0.jar");
        toUpload.add("beam-sdks-java-io-hadoop-common-2.0.0.jar");
        toUpload.add("beam-runners-core-java-2.0.0.jar");
        toUpload.add("components-adapter-beam-0.20.0-SNAPSHOT.jar");
        toUpload.add("avro-mapred-1.8.1-hadoop2.jar");
        toUpload.add("commons-csv-1.4.jar");
        toUpload.add("data-streams-beamcompiler-0.6.0-SNAPSHOT.jar");

        final JavaSparkContext jsc = spark.getOptions().getProvidedSparkContext();
        // for (String s : System.getProperty("java.class.path").split(File.pathSeparator)) {
        // File f = new File(s);
        // String fn = f.getName();
        // if (toUpload.contains(fn)) {
        // System.out.println("Uploading " + s + " to the SparkContext...");
        // jsc.addJar(s);
        // }
        // }

        // mvn dependency:copy-dependencies -DoutputDirectory=/tmp/xxx
        for (String s : toUpload) {
            System.out.println("Uploading " + s + " to the SparkContext...");
            jsc.addJar("/tmp/xxx/" + s);

        }

        // Set the schema registry before starting to compile components into the pipeline.
        // TODO
        ServerSocketAvroSchemaRegistry ssasr = new ServerSocketAvroSchemaRegistry();
        LazyAvroCoder.setSchemaRegistry(ssasr);

        // Configure the input.
        final PCollection<IndexedRecord> in;
        {
            SimpleFileIOInputProperties props = createInputComponentProperties();
            props.getDatasetProperties().path.setValue(pathIn);
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
            pocAggregate = in.apply(runtime);
            // pocAggregate = in;
        }

        // Configure the output
        {
            SimpleFileIOOutputProperties props = createOutputComponentProperties();
            props.overwrite.setValue(true);
            props.getDatasetProperties().path.setValue(pathOut);
            props.getDatasetProperties().format.setValue(SimpleFileIOFormat.AVRO);

            // Create the runtime.
            SimpleFileIOOutputRuntime runtime = new SimpleFileIOOutputRuntime();
            runtime.initialize(null, props);
            pocAggregate.apply(runtime);
        }

        // SCHEMA REGISTRY RUN ==================================
        ssasr.run();

        // And run the test.
        p.run().waitUntilFinish();

        // SCHEMA REGISTRY SHUTDOWN ==================================
        ssasr.shutdown();
        LazyAvroCoder.resetSchemaRegistry();

        // Check the expected values.
        List<IndexedRecord> found = new ArrayList<>();
        {
            FileSystem fs = FileSystem.get(new URI(pathOut), spark.createHadoopConfiguration());
            for (FileStatus fstatus : FileSystemUtil.listSubFiles(fs, pathOut)) {
                try (DataFileStream<GenericRecord> reader = new DataFileStream<>(
                        new BufferedInputStream(fs.open(fstatus.getPath())), new GenericDatumReader<GenericRecord>())) {
                    while (reader.hasNext()) {
                        IndexedRecord r = reader.next();
                        System.out.println(r);
                        found.add(r);
                    }
                }
            }
        }
        assertThat(found, hasSize(1000));
    }

}
