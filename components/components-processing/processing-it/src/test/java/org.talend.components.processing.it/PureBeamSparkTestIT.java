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
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.talend.daikon.avro.SampleSchemas;

/**
 * Unit tests using the Spark runner.
 *
 * This loads jars from the /tmp/xxx directory -- you have to do this before running.
 *
 * <pre>
 * mvn install -DskipTests
 * mvn dependency:copy-dependencies -DoutputDirectory=/tmp/xxx
 * cp target/processing-it-0.20.0-SNAPSHOT.jar /tmp/xxx
 * </pre>
 */
public class PureBeamSparkTestIT {

    /** Resource that provides a {@link Pipeline} configured for Spark. */
    @Rule
    public SparkIntegrationTestResource spark = SparkIntegrationTestResource.ofDefault();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /**
     * Basic unit test using all default values (except for the path) on an in-memory DFS cluster.
     */
    @Category(ValidatesRunner.class)
    @Test
    public void testBasicDefaults() throws IOException, URISyntaxException {

        // Use the resource to create the pipeline.
        final Pipeline p = spark.createPipeline();

        final String pathIn = "hdfs://talend-cdh580.weave.local:8020/user/talend/recordSimple/*";
        final String pathOut = "hdfs://talend-cdh580.weave.local:8020/user/" + System.getProperty("user.name")
                + "/recordSimpleOut";

        // Upload the jars.
        Set<String> toUpload = new HashSet<>();

        // Additional jars that are required for beam spark runner.
        toUpload.add("beam-runners-spark-2.0.0.jar");
        toUpload.add("beam-runners-core-java-2.0.0.jar");
        toUpload.add("beam-sdks-java-core-2.0.0.jar");

        // base
        toUpload.add("beam-sdks-java-io-hadoop-file-system-2.0.0.jar");

        // All of the distributed tasks that this test uses (but not this driver).
        toUpload.add("processing-it-0.20.0-SNAPSHOT.jar");

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
        // cp target/processing-it-0.20.0-SNAPSHOT.jar /tmp/xxx
        for (String s : toUpload) {
            System.out.println("Uploading " + s + " to the SparkContext...");
            jsc.addJar("/tmp/xxx/" + s);

        }

        // Set the schema registry before starting to compile components into the pipeline.
        // TODO
        // ServerSocketAvroSchemaRegistry ssasr = new ServerSocketAvroSchemaRegistry();
        // LazyAvroCoder.setSchemaRegistry(ssasr);

        // Configure the input.
        final PCollection<GenericRecord> in = p.apply(AvroIO.readGenericRecords(SampleSchemas.recordSimple()).from(pathIn));

        PCollection<IndexedRecord> pocAggregate;
        {
            PCollection<IndexedRecord> keys = in.apply("ExtractKeys", ParDo.of(new ExtractKey()));
            keys.setCoder((Coder) AvroCoder.of(ExtractKey.keySchema));
            // keys = keys.setCoder(LazyAvroCoder.<IndexedRecord> of());
            PCollection<KV<IndexedRecord, Long>> counts = keys.apply(Count.<IndexedRecord> perElement());
            pocAggregate = counts.apply("TransformResults", ParDo.of(new TransformResults()));
            // pocAggregate = pocAggregate.setCoder(LazyAvroCoder.<IndexedRecord> of());
            pocAggregate = pocAggregate.setCoder((Coder) AvroCoder.of(TransformResults.countSchema));
        }

        // Configure the output
        {
            pocAggregate.apply((PTransform) AvroIO.writeGenericRecords(TransformResults.countSchema).to(pathOut));
        }

        // SCHEMA REGISTRY RUN ==================================
        // ssasr.run();

        // And run the test.
        p.run().waitUntilFinish();

        // SCHEMA REGISTRY SHUTDOWN ==================================
        // ssasr.shutdown();
        // LazyAvroCoder.resetSchemaRegistry();

        // Check the expected values.
        List<IndexedRecord> found = new ArrayList<>();
        {
            FileSystem fs = FileSystem.get(new URI(pathOut), spark.createHadoopConfiguration());
            for (FileStatus fstatus : fs.listStatus(new Path(pathOut), new PathFilter() {

                @Override
                public boolean accept(Path path) {
                    String name = path.getName();
                    return !name.startsWith("_") && !name.startsWith(".");
                }
            })) {
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
