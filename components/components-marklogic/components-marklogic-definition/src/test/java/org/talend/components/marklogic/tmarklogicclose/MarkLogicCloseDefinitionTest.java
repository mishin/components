package org.talend.components.marklogic.tmarklogicclose;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE_PROP;

public class MarkLogicCloseDefinitionTest {

    MarkLogicCloseDefinition closeDefinition;

    @Before
    public void init() {
        closeDefinition = new MarkLogicCloseDefinition();
    }

    @Test
    public void testGetFamilies() {
        String[] expectedFamilies = new String[] {"Databases/MarkLogic", "Big Data/MarkLogic"};

        String[] actualFamilies = closeDefinition.getFamilies();

        assertArrayEquals(expectedFamilies, actualFamilies);
    }

    @Test
    public void testGetPropertyClass() {
        Class expectedPropertyClass = MarkLogicCloseProperties.class;

        assertEquals(expectedPropertyClass, closeDefinition.getPropertyClass());
    }

    @Test
    public void testGetReturnProperties() {
        Property[] expectedReturnProperties = new Property[] {RETURN_ERROR_MESSAGE_PROP};

        assertArrayEquals(expectedReturnProperties, closeDefinition.getReturnProperties());
    }

    @Test
    public void testGetRuntimeInfo() {
        RuntimeInfo runtimeInfo = closeDefinition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.NONE);

        assertEquals("org.talend.components.marklogic.connection.TMarkLogicCloseStandalone", runtimeInfo.getRuntimeClassName());
    }

    @Test
    public void testGetRuntimeInfoForWrongTopology() {
        RuntimeInfo runtimeInfo = closeDefinition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.OUTGOING);
        assertNull(runtimeInfo);
    }

    @Test
    public void testIsStartable() {
        assertTrue(closeDefinition.isStartable());
    }

    @Test
    public void testGetSupportedConnectorTopologies() {
        Set<ConnectorTopology> connectorTopologies = closeDefinition.getSupportedConnectorTopologies();

        assertThat(connectorTopologies, contains(ConnectorTopology.NONE));
        assertThat(connectorTopologies, not((contains(ConnectorTopology.INCOMING,ConnectorTopology.OUTGOING,ConnectorTopology.INCOMING_AND_OUTGOING))));
    }

}
