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
package org.talend.components.marklogic.tmarklogicconnection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.runtime.RuntimeInfo;

public class MarkLogicConnectionDefinitionTest {

    public MarkLogicConnectionDefinition definition;

    @Before
    public void init() {
        definition = new MarkLogicConnectionDefinition();
    }

    @Test
    public void testGetRuntimeInfo() {
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(null);

        assertEquals("org.talend.components.marklogic.runtime.TMarkLogicConnectionStandalone",
                runtimeInfo.getRuntimeClassName());
    }

    @Test
    public void testGetPropertyClass() {
        Class<?> expectedPropertyClass = MarkLogicConnectionProperties.class;

        assertEquals(expectedPropertyClass, definition.getPropertiesClass());
    }

}
