package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.definition.typeconverter.TypeConverterProperties;
import org.talend.daikon.properties.ValidationResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        // TODO check if we have to convert to Avro
        IndexedRecord inputRecord = context.element();
        Schema inputSchema = inputRecord.getSchema();

        // Compute new schema
        Schema outputSchema = inputSchema;
        List<Schema.Field> fieldList = new ArrayList<>();
        Iterator<TypeConverterProperties.TypeConverterPropertiesInner> convertersIterator = properties.converters.subProperties.iterator();
        TypeConverterProperties.TypeConverterPropertiesInner currentConverter = null;
        while (convertersIterator.hasNext()) {
            currentConverter = convertersIterator.next();
            outputSchema = TypeConverterUtils.transformSchema(outputSchema, currentConverter.field.getValue().split("\\."),0);
        }

        // Compute new fields
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(outputSchema);
        // Loop on fields
        // outputRecord.set(field.name(), outputValue);

        //currentConverter.field;
        //currentConverter.outputType;
        //currentConverter.outputFormat;


/*
                    * Boolean
                    * Double
                    * Float
                    * Integer
                    * Long
                    * String
                    * Time
                    * DateTime (ms & ss precision)
                    * Decimal
*/



        context.output(outputRecord.build());
    }
}