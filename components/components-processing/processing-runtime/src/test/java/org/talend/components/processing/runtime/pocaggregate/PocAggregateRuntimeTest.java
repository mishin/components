package org.talend.components.processing.runtime.pocaggregate;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.talend.components.adapter.beam.coders.LazyAvroCoder;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.definition.pocaggregate.PocAggregateProperties;
import org.talend.daikon.avro.GenericDataRecordHelper;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

public class PocAggregateRuntimeTest {

    private final PocAggregateRuntime pocaggregateRuntime = new PocAggregateRuntime();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /**
     * Check {@link PocAggregateRuntime#initialize(RuntimeContainer, Properties)}
     */
    @Test
    public void testInitialize() {
        ValidationResult result = pocaggregateRuntime.initialize(null, null);
        assertEquals(ValidationResult.OK, result);
    }

    @Test
    public void testBasic() throws IOException {
        // The output folder on the local filesystem
        Path folder = tmp.getRoot().toPath().resolve("output");

        // Create pipeline
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);
        final Pipeline p = Pipeline.create(options);

        // Create PCollection for test
        Schema a = GenericDataRecordHelper.createSchemaFromObject("a", new Object[] { "a" });
        IndexedRecord irA = GenericDataRecordHelper.createRecord(a, new Object[] { "a" });
        IndexedRecord irB = GenericDataRecordHelper.createRecord(a, new Object[] { "b" });
        IndexedRecord irC = GenericDataRecordHelper.createRecord(a, new Object[] { "c" });

        List<IndexedRecord> data = Arrays.asList(irA, irB, irC, irA, irA, irC);

        PCollection<IndexedRecord> input = (PCollection<IndexedRecord>) p.apply(Create.of(data).withCoder(LazyAvroCoder.of()));

        PocAggregateProperties pocaggregateProperties = new PocAggregateProperties("test");
        pocaggregateRuntime.initialize(null, pocaggregateProperties);

        PCollection<IndexedRecord> output = input.apply(pocaggregateRuntime);

        // Write the results as JSON to a file.
        output.apply("", ParDo.of(new ToStringFn<>())).apply(TextIO.write().to(folder.resolve("part").toString()).withNumShards(1));
        p.run().waitUntilFinish();
        assertThat("Folder exists: " + folder, Files.exists(folder), is(true));

        // Read the file into an array
        Path file = folder.resolve("part-00000-of-00001");
        assertThat("File exists: " + folder, Files.exists(folder), is(true));
        assertThat(Files.readAllLines(file, Charset.forName("UTF8")), containsInAnyOrder( //
                "{'key': {'field': 'a'}, 'count': 3}".replace('\'', '"'), //
                "{'key': {'field': 'b'}, 'count': 1}".replace('\'', '"'), //
                "{'key': {'field': 'c'}, 'count': 2}".replace('\'', '"')));
    }

    public static class ToStringFn<T> extends DoFn<T, String> {

        @DoFn.ProcessElement
        public void processElement(ProcessContext c) {
            c.output(c.element().toString());
        }
    }
}