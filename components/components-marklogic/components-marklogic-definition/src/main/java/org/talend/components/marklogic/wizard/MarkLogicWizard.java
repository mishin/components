package org.talend.components.marklogic.wizard;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;

public class MarkLogicWizard extends ComponentWizard {

    MarkLogicConnectionProperties props;

    public MarkLogicWizard(ComponentWizardDefinition definition, String repositoryLocation) {
        super(definition, repositoryLocation);
    }

    public void setupProperties(MarkLogicConnectionProperties properties) {
        props = properties;
    }

    public boolean supportsProperties(ComponentProperties properties) {
        return properties instanceof MarkLogicConnectionProperties;
    }
}
