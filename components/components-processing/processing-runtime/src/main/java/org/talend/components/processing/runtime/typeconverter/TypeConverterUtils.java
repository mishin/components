package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.processing.definition.typeconverter.TypeConverterProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.converter.Converter;
import org.talend.daikon.properties.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 */
public class TypeConverterUtils {

    public static Schema getUnwrappedSchema(Schema.Field field) {
        return AvroUtils.unwrapIfNullable(field.schema());
    }

    /**
     * Transform input schema to a new schema.
     * <p>
     * The schema of the array field `pathToConvert` will be modified to the schema of its fields.
     */
    public static Schema convertSchema(Schema inputSchema, Stack<String> converterPath, TypeConverterProperties.TypeConverterOutputTypes targetType) {
        List<Schema.Field> fieldList = new ArrayList<>();
        String currentStep = converterPath.pop();
        for (Schema.Field field : inputSchema.getFields()) {
            Schema unwrappedSchema = getUnwrappedSchema(field);
            if (field.name().equals(currentStep)) {
                // We are on the path to be converted
                if (converterPath.size() == 0) {
                    // We are on the exact element to convert
                    fieldList.add(new Schema.Field(field.name(), Schema.create(targetType.getTargetType()), field.doc(), field.defaultVal()));
                } else {
                    // Going down in the hierarchy
                    fieldList.add(new Schema.Field(field.name(), TypeConverterUtils.convertSchema(unwrappedSchema, converterPath, targetType), field.doc(), field.defaultVal()));
                }
            } else {
                // We are not on the path to convert, just recopying schema
                fieldList.add(new Schema.Field(field.name(), field.schema(), field.doc(), field.defaultVal()));
            }
        }
        return Schema.createRecord(inputSchema.getName(), inputSchema.getDoc(), inputSchema.getNamespace(), inputSchema.isError(),
                fieldList);

    }

    /**
     * Copy fields value from inputRecord to outputRecordBuilder
     *
     * @param inputRecord
     * @param outputRecordBuilder
     */
    public static void copyFieldsValues(IndexedRecord inputRecord, GenericRecordBuilder outputRecordBuilder) {
        List<Schema.Field> fields = inputRecord.getSchema().getFields();
        for (Schema.Field field : fields) {
            outputRecordBuilder.set(field.name(), inputRecord.get(field.pos()));
        }
    }

    /**
     * Convert value of outputRecordBuilder according to converterPath, outputType and outputFormat
     *
     * @param outputRecordBuilder
     * @param converterPath
     * @param outputType
     * @param outputFormat
     */
    public static void convertValue(GenericRecordBuilder outputRecordBuilder, Stack<String> converterPath, TypeConverterProperties.TypeConverterOutputTypes outputType, String outputFormat) {
        String fieldName = converterPath.pop();
        Object value = outputRecordBuilder.get(fieldName);
        if (converterPath.size() == 0){
            Converter converter = outputType.getConverter();
            outputRecordBuilder.set(fieldName, converter.convert(value));
        } else {
            TypeConverterUtils.convertValue((GenericRecordBuilder) value, converterPath, outputType, outputFormat);
        }
    }
}
