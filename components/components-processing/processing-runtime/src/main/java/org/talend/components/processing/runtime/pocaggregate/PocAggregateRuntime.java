package org.talend.components.processing.runtime.pocaggregate;

// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.util.Utf8;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.adapter.beam.coders.LazyAvroCoder;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.definition.pocaggregate.PocAggregateProperties;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.avro.converter.SingleColumnIndexedRecordConverter;
import org.talend.daikon.properties.ValidationResult;

public class PocAggregateRuntime extends PTransform<PCollection<IndexedRecord>, PCollection<IndexedRecord>>
        implements RuntimableRuntime<PocAggregateProperties> {

    private PocAggregateProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, PocAggregateProperties componentProperties) {
        this.properties = componentProperties;
        return ValidationResult.OK;
    }

    @Override
    public PCollection<IndexedRecord> expand(PCollection<IndexedRecord> input) {
        PCollection<IndexedRecord> keys = input.apply("ExtractKey", ParDo.of(new ExtractKey()))
                .setCoder(LazyAvroCoder.<IndexedRecord> of());
        PCollection<KV<IndexedRecord, Long>> counts = keys.apply(Count.<IndexedRecord> perElement());
        PCollection<IndexedRecord> output = counts.apply("TransformResults", ParDo.of(new TransformResults()))
                .setCoder(LazyAvroCoder.<IndexedRecord> of());
        return output;
    }

    public static class ExtractKey extends DoFn<IndexedRecord, IndexedRecord> {

        private IndexedRecordConverter<Object, IndexedRecord> converter = null;

        @ProcessElement
        public void processElement(ProcessContext c) {
            IndexedRecord in = c.element();
            Object datum = in.get(0);
            if (converter == null) {
                Object datumSchema = in.getSchema().getFields().get(0);
                converter = (IndexedRecordConverter) new AvroRegistry().createIndexedRecordConverter(datum.getClass());
                // TODO Avro Utf8 isn't handled by AvroRegistry, probably be a bug.
                // AvroRegistry.registerSharedPrimitiveClass(Utf8.class, Schema.create(Type.STRING));
                if (datum instanceof Utf8) {
                    converter = new SingleColumnIndexedRecordConverter(Utf8.class, Schema.create(Schema.Type.STRING));
                }
                if (converter == null) {
                    throw new Pipeline.PipelineExecutionException(
                            new RuntimeException("Cannot convert " + datum.getClass() + " to IndexedRecord."));
                }
            }
            IndexedRecord key = converter.convertToAvro(datum);
            c.output(key);
        }
    }

    public static class TransformResults extends DoFn<KV<IndexedRecord, Long>, IndexedRecord> {

        private Schema schema = null;

        @ProcessElement
        public void processElement(ProcessContext c) {
            KV<IndexedRecord, Long> in = c.element();
            if (schema == null) {
                schema = SchemaBuilder.builder().record("pocaggregate").fields() //
                        .name("key").type(in.getKey().getSchema()).noDefault() //
                        .requiredLong("count") //
                        .endRecord();
            }
            IndexedRecord out = new GenericData.Record(schema);
            out.put(0, in.getKey());
            out.put(1, in.getValue());

            c.output(out);
        }
    }
}
