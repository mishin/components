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
package org.talend.components.jdbc.tjdbcrow;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.jdbc.ComponentConstants;
import org.talend.components.jdbc.JdbcRuntimeInfo;
import org.talend.components.jdbc.wizard.JDBCConnectionWizardProperties;
import org.talend.components.jdbc.wizard.JDBCSchemaWizardProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * JDBC row component
 *
 */
public class TJDBCRowDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "tJDBCRow";

    public TJDBCRowDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.DI);
        setupI18N(new Property<?>[] { RETURN_QUERY_PROP });
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TJDBCRowProperties.class;
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "Databases/DB_JDBC" };
    }

    public static final Property<String> RETURN_QUERY_PROP = PropertyFactory.newString(ComponentConstants.RETURN_QUERY);

    @SuppressWarnings("rawtypes")
    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_ERROR_MESSAGE_PROP, RETURN_QUERY_PROP };
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties,
            ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        switch (connectorTopology) {
        case OUTGOING:
            return new JdbcRuntimeInfo((TJDBCRowProperties) properties, "org.talend.components.jdbc.runtime.JDBCRowSource");
        case INCOMING:
        case INCOMING_AND_OUTGOING:
            return new JdbcRuntimeInfo((TJDBCRowProperties) properties, "org.talend.components.jdbc.runtime.JDBCRowSink");
        case NONE:
            return new JdbcRuntimeInfo((TJDBCRowProperties) properties, "org.talend.components.jdbc.runtime.JDBCRowSourceOrSink");
        default:
            return null;
        }
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.OUTGOING, ConnectorTopology.INCOMING, ConnectorTopology.INCOMING_AND_OUTGOING,
                ConnectorTopology.NONE);
    }

    @Override
    public boolean isConditionalInputs() {
        return true;
    }

    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[] { JDBCConnectionWizardProperties.class, JDBCSchemaWizardProperties.class };
    }

}
