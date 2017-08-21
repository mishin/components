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
package org.talend.components.salesforce;

import static org.talend.daikon.properties.presentation.Widget.widget;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.salesforce.dataset.SalesforceModuleProperties;
import org.talend.components.salesforce.datastore.SalesforceConnectionProperties;
import org.talend.components.salesforce.tsalesforceconnection.TSalesforceConnectionDefinition;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

/**
 * Properties common to input and output Salesforce components.
 */
public abstract class SalesforceConnectionModuleProperties extends FixedConnectorsComponentProperties
        implements SalesforceProvideConnectionProperties {

    // Collections
    //
    public ComponentReferenceProperties<SalesforceConnectionProperties> referencedComponent = new ComponentReferenceProperties<>(
            "referencedComponent", TSalesforceConnectionDefinition.COMPONENT_NAME);

    public SalesforceConnectionProperties datastore = new SalesforceConnectionProperties("datastore"); //$NON-NLS-1$

    public SalesforceModuleProperties module;

    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "module.main");

    public SalesforceConnectionModuleProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // Allow for subclassing
        module = new SalesforceModuleProperties("module");
        module.setDatastoreProperties(datastore);
    }

    public Schema getSchema() {
        return module.main.schema.getValue();
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        // adding possible ref to other datastores
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        mainForm.addRow(compListWidget);
        mainForm.addRow(datastore.getForm(Form.MAIN));

        mainForm.addRow(module.getForm(Form.REFERENCE));

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(datastore.getForm(Form.ADVANCED));
    }

    @Override
    public SalesforceConnectionProperties getSalesforceDatastoreProperties() {
        return datastore;
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        for (Form childForm : datastore.getForms()) {
            datastore.refreshLayout(childForm);
        }

        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null
                && refComponentIdValue.startsWith(TSalesforceConnectionDefinition.COMPONENT_NAME);
        if (form.getName().equals(Form.MAIN)) {// TODO check about wizard// || form.getName().equals(FORM_WIZARD)) {
            if (useOtherConnection) {
                datastore.getForm(form.getName()).setHidden(true);
                // form.getWidget(OAUTH).setHidden(true);
                // form.getWidget(USERPASSWORD).setHidden(true);
            } else {
                datastore.refreshLayout(datastore.getForm(form.getName()));
            }
        }

        if (form.getName().equals(Form.ADVANCED)) {
            if (useOtherConnection) {
                form.setHidden(true);
            } else {
                datastore.refreshLayout(datastore.getForm(form.getName()));
            }
        }
    }

    public void afterReferencedComponent() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getStringValue();
    }

}
