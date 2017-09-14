package org.talend.components.marklogic.runtime;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;

public class MarkLogicSourceOrSink implements SourceOrSink {

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        Schema markLogicSchema = AvroUtils._string();

        // create Schema for MarkLogic
        Schema.Field docIdField = new Schema.Field("docId", markLogicSchema, null, (Object) null, Schema.Field.Order.ASCENDING);
        docIdField.addProp(SchemaConstants.TALEND_COLUMN_IS_KEY, "true");
        Schema.Field docContentField = new Schema.Field("docContent", markLogicSchema, null, (Object) null, Schema.Field.Order.IGNORE);
        List<Schema.Field> fields = new ArrayList<>();
        fields.add(docIdField);
        fields.add(docContentField);
        Schema initialSchema = Schema.createRecord("jira", null, null, false, fields);
        initialSchema.addProp(TALEND_IS_LOCKED, "true");

        return markLogicSchema;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        return ValidationResult.OK;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        return ValidationResult.OK;
    }
}
