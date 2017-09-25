package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.junit.Test;
import org.talend.components.processing.definition.typeconverter.TypeConverterProperties;

import java.util.List;

public class TypeConverterRuntimeTest {

    @Test
    public void testDoFn() throws Exception {
        Schema inputSchema = SchemaBuilder.record("inputRow") //
                .fields() //
                .name("a").type().optional().stringType() //
                .name("b").type(SchemaBuilder.record("nestedSchema").fields().name("b1").type().bytesType().noDefault().endRecord()).noDefault()
                .endRecord();

        GenericRecordBuilder recordBuilder = new GenericRecordBuilder(inputSchema);
        TypeConverterProperties properties = new TypeConverterProperties("test");
        TypeConverterRuntime runtime = new TypeConverterRuntime().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(runtime);
        List<IndexedRecord> outputs = fnTester.processBundle(recordBuilder.build());
    }
}