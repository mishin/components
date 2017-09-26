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
package org.talend.components.marklogic.tmarklogicoutput;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

public class MarkLogicOutputProperties extends FixedConnectorsComponentProperties {

    public MarkLogicConnectionProperties connection = new MarkLogicConnectionProperties("connection");

    public SchemaProperties schema = new SchemaProperties("schema");

    public SchemaProperties schemaReject = new SchemaProperties("schemaReject"); //$NON-NLS-1$

    public SchemaProperties schemaFlow = new SchemaProperties("schemaFlow");

    public Property<String> action = newString("action");

    public Property<String> docType = newString("docType");

    public Property<Boolean> autoGenerateDocId = newBoolean("autoGenerateDocId");

    public Property<String> docIdPrefix = newString("docIdPrefix");

    public MarkLogicOutputProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connection.getForm(Form.REFERENCE));
        mainForm.addRow(schema.getForm(Form.REFERENCE));
        mainForm.addRow(Widget.widget(action).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(Widget.widget(docType).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        advancedForm.addRow(autoGenerateDocId);
        advancedForm.addRow(docIdPrefix);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        setupSchema();
        connection.setupProperties();
        action.setPossibleValues("UPSERT", "PATCH", "DELETE");
        action.setValue("UPSERT");
        docType.setPossibleValues("MIXED", "PLAIN TEXT", "JSON", "XML", "BINARY");
        docType.setValue("MIXED");
        docIdPrefix.setValue("/");
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        if (form.getName().equals(Form.MAIN)) {
            for (Form childForm : connection.getForms()) {
                connection.refreshLayout(childForm);
            }
        } else if (form.getName().equals(Form.ADVANCED)) {
            form.getWidget(docType).setVisible(!action.getStringValue().equals("DELETE"));
            if (action.getStringValue().equals("PATCH")) {
                docType.setPossibleValues("JSON", "XML");
                docType.setValue("JSON");
            }
            else {
                docType.setPossibleValues("MIXED", "PLAIN TEXT", "JSON", "XML", "BINARY");
            }

            form.getWidget(autoGenerateDocId).setVisible((action.getStringValue().equals("UPSERT")) && !docType.getStringValue().equals("MIXED"));
            form.getWidget(docIdPrefix).setVisible((action.getStringValue().equals("UPSERT")) && autoGenerateDocId.getValue() && (!docType.getStringValue().equals("MIXED")));

        }
    }

    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schema");

    protected transient PropertyPathConnector REJECT_CONNECTOR = new PropertyPathConnector(Connector.REJECT_NAME, "schemaReject");

    protected transient PropertyPathConnector FLOW_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schemaFlow");

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (!isOutputConnection) {
            return Collections.singleton(MAIN_CONNECTOR);
        } else {
            Set<PropertyPathConnector> connectors = new LinkedHashSet<>();
            connectors.add(FLOW_CONNECTOR);
            connectors.add(REJECT_CONNECTOR);
            return connectors;
        }
    }

    void setupSchema() {
        Schema stringSchema = AvroUtils._string();
        Schema objectSchema = AvroUtils._bytes();

        // create Schema for MarkLogic
        objectSchema.addProp(SchemaConstants.JAVA_CLASS_FLAG, "java.lang.Object");
        Schema.Field docIdField = new Schema.Field("docId", stringSchema, null, (Object) null, Schema.Field.Order.ASCENDING);
        docIdField.addProp(SchemaConstants.TALEND_COLUMN_IS_KEY, "true");
        Schema.Field docContentField = new Schema.Field("docContent", objectSchema, null, (Object) null,
                Schema.Field.Order.IGNORE);
        List<Schema.Field> fields = new ArrayList<>();
        fields.add(docIdField);
        fields.add(docContentField);
        Schema initialSchema = Schema.createRecord("marklogic", null, null, false, fields);
        initialSchema.addProp(TALEND_IS_LOCKED, "true");
        fields.clear();
        schema.schema.setValue(initialSchema);

        //TODO refactor it
        schemaFlow.schema.setValue(initialSchema);

        Schema.Field errMessageField = new Schema.Field("errMesage", stringSchema, null, (Object) null,
                Schema.Field.Order.IGNORE);
        fields.add(errMessageField);
        Schema rejectSchema = Schema.createRecord("marklogicReject", null, null, false, fields);
        rejectSchema.addProp(TALEND_IS_LOCKED, "true");
        schemaReject.schema.setValue(rejectSchema);
    }

    public void afterAction() {
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterDocType() {
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterAutoGenerateDocId() {
        refreshLayout(getForm(Form.ADVANCED));
    }
}
