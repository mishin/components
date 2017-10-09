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
import org.talend.components.marklogic.MarkLogicProvideConnectionProperties;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

public class MarkLogicOutputProperties extends FixedConnectorsComponentProperties implements
        MarkLogicProvideConnectionProperties {

    public enum Action {
        UPSERT,
        PATCH,
        DELETE
    }

    public enum DocType {
        MIXED,
        PLAIN_TEXT,
        JSON,
        XML,
        BINARY
    }


    public MarkLogicConnectionProperties connection = new MarkLogicConnectionProperties("connection");

    public SchemaProperties schema = new SchemaProperties("schema");

    public SchemaProperties schemaReject = new SchemaProperties("schemaReject"); //$NON-NLS-1$

    public SchemaProperties schemaFlow = new SchemaProperties("schemaFlow");

    public EnumProperty<Action> action = newEnum("action", Action.class);

    public EnumProperty<DocType> docType = newEnum("docType", DocType.class);

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
        mainForm.addRow(Widget.widget(action).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(schema.getForm(Form.REFERENCE));

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(Widget.widget(docType).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        advancedForm.addRow(autoGenerateDocId);
        advancedForm.addRow(docIdPrefix);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        setupSchemas();
        connection.setupProperties();
        action.setPossibleValues(Action.UPSERT, Action.PATCH, Action.DELETE);
        action.setValue(Action.UPSERT);
        docType.setPossibleValues(DocType.MIXED, DocType.PLAIN_TEXT, DocType.JSON, DocType.XML, DocType.BINARY);
        docType.setValue(DocType.MIXED);
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
            form.getWidget(docType).setHidden(action.getValue().equals(Action.DELETE));
            if (action.getValue().equals(Action.PATCH)) {
                docType.setPossibleValues(DocType.JSON, DocType.XML);
                if (!docType.getPossibleValues().contains(docType.getValue())) {
                    docType.setValue(DocType.JSON);
                }
            }
            else {
                docType.setPossibleValues(DocType.values());
            }

            form.getWidget(autoGenerateDocId).setVisible((action.getValue().equals(Action.UPSERT)) && !docType.getValue().equals(DocType.MIXED));
            form.getWidget(docIdPrefix).setVisible((action.getValue().equals(Action.UPSERT)) && autoGenerateDocId.getValue() && (!docType.getValue().equals(DocType.MIXED)));

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

    void setupSchemas() {
        setupMainAndFlowSchemas();
        setupRejectSchema();
    }

    private void setupMainAndFlowSchemas() {
        Schema stringSchema = AvroUtils._string();
        Schema objectSchema = AvroUtils._bytes();

        // Talend type should be Object (but avro type is bytes[])
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

        schema.schema.setValue(initialSchema);
        schemaFlow.schema.setValue(initialSchema);
    }

    private void setupRejectSchema() {
        Schema.Field errMessageField = new Schema.Field("errMesage", AvroUtils._string(), null, (Object) null,
                Schema.Field.Order.IGNORE);
        List<Schema.Field> fields = new ArrayList<>();
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

    @Override
    public MarkLogicConnectionProperties getConnectionProperties() {
        return connection;
    }
}
