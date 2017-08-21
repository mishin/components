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
package org.talend.components.salesforce.dataset;

import static org.talend.components.salesforce.SalesforceDefinition.DATAPREP_SOURCE_CLASS;
import static org.talend.components.salesforce.SalesforceDefinition.getSandboxedInstance;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newStringList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.SchemaProperties;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.salesforce.common.SalesforceErrorCodes;
import org.talend.components.salesforce.common.SalesforceRuntimeSourceOrSink;
import org.talend.components.salesforce.dataprep.SalesforceInputProperties;
import org.talend.components.salesforce.datastore.SalesforceConnectionDefinition;
import org.talend.components.salesforce.datastore.SalesforceConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.StringProperty;
import org.talend.daikon.sandbox.SandboxedInstance;

public class SalesforceModuleProperties extends ComponentPropertiesImpl
        implements DatasetProperties<SalesforceConnectionProperties> {

    /**
     * 
     */
    private static final long serialVersionUID = -8035880860245867110L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesforceModuleProperties.class);

    public ReferenceProperties<SalesforceConnectionProperties> datastore = new ReferenceProperties<>("datastore",
            SalesforceConnectionDefinition.NAME);

    public Property<SourceType> sourceType = PropertyFactory.newEnum("sourceType", SourceType.class).setRequired();

    public StringProperty moduleName = PropertyFactory.newString("moduleName");

    public Property<List<String>> selectColumnIds = newStringList("selectColumnIds");

    public Property<String> query = PropertyFactory.newString("query");

    public ISchemaListener schemaListener;

    public SchemaProperties main = new SchemaProperties("main") {

        public void afterSchema() {
            if (schemaListener != null) {
                schemaListener.afterSchema();
            }
        }
    };

    public SalesforceModuleProperties(String name) {
        super(name);
    }

    private void retrieveModules() throws IOException {
        // refresh the module list
        if (sourceType.getValue() == SourceType.MODULE_SELECTION) {
            IOConsumer<SalesforceRuntimeSourceOrSink> consumer = new IOConsumer<SalesforceRuntimeSourceOrSink>() {

                @Override
                public void accept(SalesforceRuntimeSourceOrSink runtime) throws IOException {
                    SalesforceInputProperties properties = new SalesforceInputProperties("model");
                    properties.setDatasetProperties(SalesforceModuleProperties.this);
                    runtime.initialize(null, properties);
                    throwExceptionIfValidationResultIsError(runtime.validate(null));

                    List<NamedThing> moduleNames = runtime.getSchemaNames(null);
                    moduleName.setPossibleNamedThingValues(filter(moduleNames));
                }
            };
            runtimeTask(consumer);
        }
    }

    private void retrieveModuleFields() throws IOException {
        // refresh the module list
        if (sourceType.getValue() == SourceType.MODULE_SELECTION && StringUtils.isNotEmpty(moduleName.getValue())) {
            IOConsumer<SalesforceRuntimeSourceOrSink> consumer = new IOConsumer<SalesforceRuntimeSourceOrSink>() {

                @Override
                public void accept(SalesforceRuntimeSourceOrSink runtime) throws IOException {
                    SalesforceInputProperties properties = new SalesforceInputProperties("model");
                    properties.setDatasetProperties(SalesforceModuleProperties.this);

                    throwExceptionIfValidationResultIsError(runtime.initialize(null, properties));
                    throwExceptionIfValidationResultIsError(runtime.validate(null));

                    Schema schema = runtime.getEndpointSchema(null, moduleName.getValue());
                    main.schema.setValue(schema);
                    List<NamedThing> columns = new ArrayList<>();
                    for (Schema.Field field : schema.getFields()) {
                        columns.add(new SimpleNamedThing(field.name(), field.name()));
                    }
                    selectColumnIds.setPossibleValues(columns);

                }
            };
            runtimeTask(consumer);
        }
    }

    private List<NamedThing> filter(List<NamedThing> moduleNames) {
        if (moduleNames != null) {
            for (int i = 0; i < moduleNames.size(); i++) {
                if ("AcceptedEventRelation".equalsIgnoreCase(moduleNames.get(i).getName())) {
                    moduleNames.remove(i);
                }
            }
        }
        return moduleNames;
    }

    private interface IOConsumer<T> {

        void accept(T runtime) throws IOException;
    }

    private <T> void runtimeTask(IOConsumer<T> task) throws IOException {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(DATAPREP_SOURCE_CLASS)) {
            T runtime = (T) sandboxedInstance.getInstance();
            task.accept(runtime);
        }
    }

    private void throwExceptionIfValidationResultIsError(ValidationResult validationResult) {
        if (validationResult == null) {
            return;
        }

        if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
            throw TalendRuntimeException.createUnexpectedException(validationResult.getMessage());
        }
    }

    public void afterSourceType() throws IOException {
        // refresh the module list
        moduleName.setValue(null);
        selectColumnIds.setValue(null);
        query.setValue(null);
        refreshLayout(getForm(Form.CITIZEN_USER));
    }

    public void beforeModuleName() throws Exception {
        retrieveModules();
    }

    public void afterModuleName() throws IOException {
        // refresh the module list
        retrieveModuleFields();
        selectColumnIds.setValue(null);
        query.setValue(null);
        refreshLayout(getForm(Form.CITIZEN_USER));
    }

    @Override
    public void setupProperties() {
        sourceType.setValue(SourceType.MODULE_SELECTION);
    }

    @Override
    public void setupLayout() {

        Form moduleForm = Form.create(this, Form.MAIN);
        moduleForm.addRow(widget(moduleName).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        refreshLayout(moduleForm);

        Form moduleRefForm = Form.create(this, Form.REFERENCE);
        moduleRefForm.addRow(widget(moduleName).setWidgetType(Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE).setLongRunning(true));

        moduleRefForm.addRow(main.getForm(Form.REFERENCE));
        refreshLayout(moduleRefForm);

        Form citizenForm = Form.create(this, Form.CITIZEN_USER);

        citizenForm.addRow(Widget.widget(sourceType).setWidgetType(Widget.RADIO_WIDGET_TYPE));
        citizenForm.addRow(Widget.widget(moduleName).setWidgetType(Widget.DATALIST_WIDGET_TYPE));
        citizenForm.addRow(Widget.widget(query).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
        citizenForm.addRow(Widget.widget(selectColumnIds).setWidgetType(Widget.MULTIPLE_VALUE_SELECTOR_WIDGET_TYPE));
        citizenForm.getWidget(selectColumnIds).setVisible(false);
        selectColumnIds.setRequired(false);
    }

    /**
     * the method is called back at many places, even some strange places, so it should work only for basic layout, not
     * some action which need runtime support.
     */
    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        switch (form.getName()) {
        case Form.CITIZEN_USER:
            if (sourceType.getValue() == SourceType.MODULE_SELECTION) {
                form.getWidget(moduleName).setVisible(true);
                moduleName.setRequired(true);
                // We can not have a hidden field which is required
                form.getWidget(query).setVisible(false);
                query.setRequired(false);
                if (StringUtils.isNotEmpty(moduleName.getValue())) {
                    form.getWidget(selectColumnIds).setVisible();
                    selectColumnIds.setRequired(true);
                } else {
                    form.getWidget(selectColumnIds).setVisible(false);
                    selectColumnIds.setRequired(false);
                }

            } else if (sourceType.getValue() == SourceType.SOQL_QUERY) {
                form.getWidget(query).setVisible(true);
                query.setRequired();
                // We can not have a hidden field which is required
                form.getWidget(moduleName).setVisible(false);
                moduleName.setRequired(false);
                form.getWidget(selectColumnIds).setVisible(false);
                selectColumnIds.setRequired(false);
            }
            break;
        default:
            // ignor other forms
        }

    }

    @Override
    public SalesforceConnectionProperties getDatastoreProperties() {
        return datastore.getReference();
    }

    public void afterDatastore() {
        try {
            retrieveModules();
        } catch (IOException e) {
            LOGGER.error("error getting salesforce modules", e);
            throw new TalendRuntimeException(SalesforceErrorCodes.UNABLE_TO_RETRIEVE_MODULES, e);
        }
        if (StringUtils.isNotEmpty(moduleName.getValue())) {
            try {
                retrieveModuleFields();
            } catch (IOException e) {
                LOGGER.error("error getting salesforce modules fields", e);
                throw new TalendRuntimeException(SalesforceErrorCodes.UNABLE_TO_RETRIEVE_MODULE_FIELDS, e);
            }
        } // else no module set so no reason to update fields

    }

    @Override
    public void setDatastoreProperties(SalesforceConnectionProperties datastoreProperties) {
        datastore.setReference(datastoreProperties);
        afterDatastore();
    }

    public enum SourceType {
        MODULE_SELECTION,
        SOQL_QUERY
    }

    public void setSchemaListener(ISchemaListener schemaListener) {
        this.schemaListener = schemaListener;
    }

}
