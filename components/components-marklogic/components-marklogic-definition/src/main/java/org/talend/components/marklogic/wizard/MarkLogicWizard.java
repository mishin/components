package org.talend.components.marklogic.wizard;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.properties.presentation.Form;

public class MarkLogicWizard extends ComponentWizard {


    public MarkLogicWizard(ComponentWizardDefinition definition, String repositoryLocation) {
        super(definition, repositoryLocation);
        MarkLogicConnectionProperties props = new MarkLogicConnectionProperties("props");
        props.setRepositoryLocation(repositoryLocation);
        setupProperties(props);
        addForm(props.getForm("wizardForm"));
    }

    public void setupProperties(MarkLogicConnectionProperties properties) {
        properties.init();
    }

    public boolean supportsProperties(ComponentProperties properties) {
        return properties instanceof MarkLogicConnectionProperties;
    }


}
