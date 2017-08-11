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

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.daikon.properties.presentation.Form;

/**
 * Handles the creating a connection and creating the modules associated with the connection.
 */
public class SalesforceConnectionWizard extends ComponentWizard {

    SalesforceDatastoreProperties2 cProps;

    SalesforceModuleListProperties mProps;

    SalesforceConnectionWizard(ComponentWizardDefinition def, String repositoryLocation) {
        super(def, repositoryLocation);

        cProps = new SalesforceDatastoreProperties2("connection");
        cProps.init();
        addForm(cProps.getForm(SalesforceDatastoreProperties2.FORM_WIZARD));

        mProps = new SalesforceModuleListProperties("mProps").setConnection(cProps)
                .setRepositoryLocation(getRepositoryLocation());
        mProps.init();
        addForm(mProps.getForm(Form.MAIN));
    }

    public boolean supportsProperties(ComponentProperties properties) {
        return properties instanceof SalesforceDatastoreProperties2;
    }

    public void setupProperties(SalesforceDatastoreProperties2 cPropsOther) {
        cProps.copyValuesFrom(cPropsOther);
        mProps.setConnection(cProps);
    }

}
