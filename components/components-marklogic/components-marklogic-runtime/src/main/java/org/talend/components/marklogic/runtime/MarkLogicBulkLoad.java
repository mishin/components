package org.talend.components.marklogic.runtime;

import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

public class MarkLogicBulkLoad implements ComponentDriverInitialization {

    @Override
    public void runAtDriver(RuntimeContainer container) {

    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, Properties properties) {
        return ValidationResult.OK;
    }
}
