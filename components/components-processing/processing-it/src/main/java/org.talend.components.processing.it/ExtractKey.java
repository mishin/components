package org.talend.components.processing.it;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;

public class ExtractKey extends DoFn<GenericRecord, IndexedRecord> {

    public static final Schema keySchema = SchemaBuilder.record("keyRecord").fields() //
            .requiredString("col1") //
            .endRecord();

    @ProcessElement
    public void processElement(ProcessContext c) {
        IndexedRecord in = c.element();
        String datum = String.valueOf(in.get(1));
        datum = datum.length() > 0 ? datum.substring(0, 1) : datum;

        IndexedRecord out = new GenericData.Record(keySchema);
        out.put(0, datum);
        c.output(out);
    }
}
