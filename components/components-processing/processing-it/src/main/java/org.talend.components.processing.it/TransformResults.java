package org.talend.components.processing.it;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;

public class TransformResults extends DoFn<KV<IndexedRecord, Long>, IndexedRecord> {

    public static final Schema countSchema = SchemaBuilder.record("countRecord").fields() //
            .name("key").type(ExtractKey.keySchema).noDefault() //
            .requiredLong("count") //
            .endRecord();

    @ProcessElement
    public void processElement(ProcessContext c) {
        KV<IndexedRecord, Long> in = c.element();
        IndexedRecord out = new GenericData.Record(countSchema);
        out.put(0, in.getKey());
        out.put(1, in.getValue());
        c.output(out);
    }
}
