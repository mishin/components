package org.talend.components.marklogic;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;

public abstract class AbstractMarkLogicComponentDefinition extends AbstractComponentDefinition {

    public AbstractMarkLogicComponentDefinition(String componentName, boolean allEngines) {
        super(componentName, allEngines);
    }

    public AbstractMarkLogicComponentDefinition(String componentName, ExecutionEngine engine1, ExecutionEngine... engineOthers) {
        super(componentName, engine1, engineOthers);
    }

    /**
     * Make is possible for wizard find components when drag and drop it to pallete
     * @param properties
     * @return default supportsProperties OR properties contains only MarkLogicConnectionProperties object
     */
    @Override
    public boolean supportsProperties(ComponentProperties... properties) {
        boolean isSupportedByDefault = super.supportsProperties(properties);
        boolean isConnectionProperties = properties.length == 1 && properties[0] instanceof MarkLogicConnectionProperties;

        return isSupportedByDefault || isConnectionProperties;
    }
}
