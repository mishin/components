package org.talend.components.marklogic.wizard;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.daikon.properties.presentation.Form;

public class MarkLogicWizard extends ComponentWizard {

    MarkLogicWizardConnectionProperties props;

    public MarkLogicWizard(ComponentWizardDefinition definition, String repositoryLocation) {
        super(definition, repositoryLocation);
        props = new MarkLogicWizardConnectionProperties("props");
        props.setRepositoryLocation(repositoryLocation);
        setupProperties(props);
        addForm(props.getForm(Form.MAIN));
    }

    public void setupProperties(MarkLogicWizardConnectionProperties properties) {
        props = properties;
        props.init();
    }

    public boolean supportsProperties(ComponentProperties properties) {
        return properties instanceof MarkLogicWizardConnectionProperties;
    }
}
