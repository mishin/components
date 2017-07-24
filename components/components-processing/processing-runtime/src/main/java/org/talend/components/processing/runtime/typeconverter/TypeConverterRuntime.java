package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.PCollection;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.adapter.beam.BeamJobBuilder;
import org.talend.components.adapter.beam.BeamJobContext;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.pythonrow.PythonRowProperties;
import org.talend.components.processing.replicate.ReplicateProperties;
import org.talend.components.processing.typeconverter.TypeConverterProperties;
import org.talend.daikon.properties.ValidationResult;

public class TypeConverterRuntime extends DoFn<IndexedRecord, IndexedRecord>
        implements RuntimableRuntime<TypeConverterProperties> {

    private TypeConverterProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, TypeConverterProperties componentProperties) {
        this.properties = componentProperties;
        return ValidationResult.OK;
    }

    @ProcessElement
    public void processElement(ProcessContext context) {
        // TODO(rskraba): Actually implement the conversion. This currently only passes the input to the output.
        IndexedRecord in = context.element();
        IndexedRecord out = in;
        context.output(out);
    }
}