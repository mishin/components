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
package org.talend.components.google.drive.copy;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;

public class GoogleDriveCopyDefinitionTest {

    GoogleDriveCopyDefinition def;

    @Before
    public void setUp() throws Exception {
        def = new GoogleDriveCopyDefinition();
    }

    @Test
    public void testGetRuntimeInfo() throws Exception {
        assertNotNull(def.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.OUTGOING));
    }

    @Test
    public void testGetSupportedConnectorTopologies() throws Exception {
        assertThat(EnumSet.of(ConnectorTopology.OUTGOING, ConnectorTopology.NONE), equalTo(def.getSupportedConnectorTopologies()));
    }

    @Test
    public void testIsStartable() throws Exception {
        assertTrue(def.isStartable());
    }

}
