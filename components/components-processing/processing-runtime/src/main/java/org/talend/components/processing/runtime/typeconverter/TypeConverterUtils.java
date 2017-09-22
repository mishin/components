package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class TypeConverterUtils {

    public static Schema getUnwrappedSchema(Schema.Field field) {
        return AvroUtils.unwrapIfNullable(field.schema());
    }

    /**
     * Transform input schema to a new schema.
     *
     * The schema of the array field `pathToConvert` will be modified to the schema of its fields.
     */
    public static Schema transformSchema(Schema inputSchema, String[] pathToConvertArray, int pathIterator) {
        List<Schema.Field> fieldList = new ArrayList<>();
        for (Schema.Field field : inputSchema.getFields()) {
            Schema unwrappedSchema = getUnwrappedSchema(field);
            if ((pathIterator < pathToConvertArray.length) && (field.name().equals(pathToConvertArray[pathIterator]))
                    && (unwrappedSchema.getType().equals(Schema.Type.ARRAY))) {
                fieldList.add(new Schema.Field(field.name(), unwrappedSchema.getElementType(), field.doc(), field.defaultVal()));
            } else if (unwrappedSchema.getType().equals(Schema.Type.RECORD)) {
                if ((pathIterator < pathToConvertArray.length) && (field.name().equals(pathToConvertArray[pathIterator]))) {
                    Schema subElementSchema = transformSchema(unwrappedSchema, pathToConvertArray, ++pathIterator);
                    fieldList.add(new Schema.Field(field.name(), subElementSchema, null, null));
                } else {
                    // if we are outside of the pathToConvert, set the pathIterator at something that cannot be used
                    // again
                    Schema subElementSchema = transformSchema(unwrappedSchema, pathToConvertArray, pathToConvertArray.length);
                    fieldList.add(new Schema.Field(field.name(), subElementSchema, null, null));
                }
            } else {
                // element add it directly
                fieldList.add(new Schema.Field(field.name(), field.schema(), field.doc(), field.defaultVal()));
            }
        }
        return Schema.createRecord(inputSchema.getName(), inputSchema.getDoc(), inputSchema.getNamespace(), inputSchema.isError(),
                fieldList);

    }
}
