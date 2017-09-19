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
package org.talend.components.marketo.tmarketolistoperation;

import static org.talend.components.marketo.MarketoConstants.getListOperationFlowRESTSchema;
import static org.talend.components.marketo.MarketoConstants.getListOperationFlowSOAPSchema;
import static org.talend.components.marketo.MarketoConstants.getListOperationRejectRESTSchema;
import static org.talend.components.marketo.MarketoConstants.getListOperationRejectSOAPSchema;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;

import java.util.HashSet;
import java.util.Set;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.marketo.MarketoComponentProperties;
import org.talend.components.marketo.MarketoConstants;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties.APIMode;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class TMarketoListOperationProperties extends MarketoComponentProperties {

    public Property<Boolean> dieOnError = newBoolean("dieOnError");

    public enum ListOperation {
        addTo, // adds one or more leads to a list in the Marketo DB.
        isMemberOf, // checks the Marketo DB to judge whether the specific leads exist in the list.
        removeFrom // removes one or more leads from a list in the Marketo DB.
    }

    private transient static final Logger LOG = LoggerFactory.getLogger(TMarketoListOperationProperties.class);

    public Property<ListOperation> listOperation = PropertyFactory.newEnum("listOperation", ListOperation.class);

    public Property<Boolean> multipleOperation = PropertyFactory.newBoolean("multipleOperation");

    public TMarketoListOperationProperties(String name) {
        super(name);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        Set<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnection) {
            connectors.add(FLOW_CONNECTOR);
            connectors.add(REJECT_CONNECTOR);
        } else {
            connectors.add(MAIN_CONNECTOR);
        }
        return connectors;
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        dieOnError.setValue(true);
        listOperation.setPossibleValues(ListOperation.values());
        listOperation.setValue(ListOperation.addTo);
        multipleOperation.setValue(false);
        schemaInput.schema.setValue(MarketoConstants.getListOperationRESTSchema());
        updateOutputSchemas();
        setSchemaListener(new ISchemaListener() {

            @Override
            public void afterSchema() {
                schemaFlow.schema.setValue(null);
                schemaReject.schema.setValue(null);
                updateOutputSchemas();
                refreshLayout(getForm(Form.MAIN));
            }
        });
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(listOperation);
        mainForm.addRow(multipleOperation);
        mainForm.addRow(dieOnError);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        if (APIMode.SOAP.equals(getConnectionProperties().apiMode.getValue())) {
            schemaInput.schema.setValue(MarketoConstants.getListOperationSOAPSchema());
            updateOutputSchemas();
        } else {
            schemaInput.schema.setValue(MarketoConstants.getListOperationRESTSchema());
            updateOutputSchemas();
        }

        if (form.getName().equals(Form.MAIN)) {
            switch (listOperation.getValue()) {
            case addTo:
            case removeFrom:
                form.getWidget(multipleOperation.getName()).setVisible(true);
                break;
            default:
                form.getWidget(multipleOperation.getName()).setVisible(false);
            }
        }
    }

    public void afterListOperation() {
        updateOutputSchemas();
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterMultipleOperation() {
        updateOutputSchemas();
        refreshLayout(getForm(Form.MAIN));
    }

    public ValidationResult validateMultipleOperation() {
        ValidationResultMutable vr = new ValidationResultMutable();
        if (listOperation.getValue().equals(ListOperation.isMemberOf) && multipleOperation.getValue()) {
            vr.setStatus(Result.ERROR);
            vr.setMessage("multipleOperation flag cannot be set with operation=isMemberOf!");
            return vr;
        }
        return ValidationResult.OK;
    }

    public void updateOutputSchemas() {
        Schema inputSchema = schemaInput.schema.getValue();
        inputSchema.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
        // batch processing
        if ((listOperation.getValue().equals(ListOperation.addTo) || listOperation.getValue().equals(ListOperation.removeFrom))
                && multipleOperation.getValue()) {
            // schemaFlow.schema.setValue(MarketoConstants.getEmptySchema());
            // schemaReject.schema.setValue(MarketoConstants.getEmptySchema());
            schemaFlow.schema.setValue(inputSchema);
            schemaReject.schema.setValue(inputSchema);
            return;
        }
        if (isApiSOAP()) {
            schemaFlow.schema.setValue(getListOperationFlowSOAPSchema());
            schemaReject.schema.setValue(getListOperationRejectSOAPSchema());
            return;
        } else {
            schemaFlow.schema.setValue(getListOperationFlowRESTSchema());
            schemaReject.schema.setValue(getListOperationRejectRESTSchema());
        }
    }

}
