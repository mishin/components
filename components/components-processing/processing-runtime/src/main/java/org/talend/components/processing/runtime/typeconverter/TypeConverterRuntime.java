package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.definition.typeconverter.TypeConverterProperties;
import org.talend.daikon.properties.ValidationResult;

import java.util.Arrays;
import java.util.Stack;

public class TypeConverterRuntime extends DoFn<IndexedRecord, IndexedRecord>
        implements RuntimableRuntime<TypeConverterProperties> {

    private TypeConverterProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, TypeConverterProperties componentProperties) {
        this.properties = componentProperties;
        return ValidationResult.OK;
    }

    public TypeConverterRuntime withProperties(TypeConverterProperties properties){
        this.properties = properties;
        return this;
    }

    @ProcessElement
    public void processElement(ProcessContext context) {

        IndexedRecord inputRecord = context.element();
        Schema inputSchema = inputRecord.getSchema();

        // Compute new schema
        Schema outputSchema = inputSchema;

        for (TypeConverterProperties.TypeConverterPropertiesInner currentConverter : properties.converters.subProperties){
            Stack<String> converterPath = new Stack<String>();
            //converterPath.addAll(Arrays.asList(currentConverter.field.getValue().split("\\.")));
            converterPath.add(currentConverter.field.getValue());
            outputSchema = TypeConverterUtils.convertSchema(outputSchema, converterPath, currentConverter.outputType.getValue());
        }

        // Compute new fields
        final GenericRecordBuilder outputRecordBuilder = new GenericRecordBuilder(outputSchema);
        // Copy original values
        TypeConverterUtils.copyFieldsValues(inputRecord, outputRecordBuilder);
        // Convert values
        for (TypeConverterProperties.TypeConverterPropertiesInner currentValueConverter : properties.converters.subProperties){
            // Loop on converters
            Stack<String> converterPath = new Stack<String>();
            //converterPath.addAll(Arrays.asList(currentValueConverter.field.getValue().split("\\.")));
            converterPath.add(currentValueConverter.field.getValue());
            TypeConverterUtils.convertValue(outputRecordBuilder, converterPath, currentValueConverter.outputType.getValue(), currentValueConverter.outputFormat.getValue());
        }

        context.output(outputRecordBuilder.build());
    }
}