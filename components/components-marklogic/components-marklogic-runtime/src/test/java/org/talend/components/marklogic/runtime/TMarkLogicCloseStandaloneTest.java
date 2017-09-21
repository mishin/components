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
package org.talend.components.marklogic.runtime;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.marklogic.connection.MarkLogicConnection;
import org.talend.components.marklogic.tmarklogicclose.MarkLogicCloseProperties;

import com.marklogic.client.DatabaseClient;

public class TMarkLogicCloseStandaloneTest {

    private TMarkLogicCloseStandalone closeStandalone;

    private RuntimeContainer container;

    private MarkLogicCloseProperties closeProperties;

    @Before
    public void setup() {
        closeStandalone = new TMarkLogicCloseStandalone();
        closeProperties = new MarkLogicCloseProperties("close");
        closeProperties.referencedComponent.componentInstanceId.setValue("referenced1");
        container = Mockito.mock(RuntimeContainer.class);
        closeStandalone.initialize(container, closeProperties);
    }

    @Test
    public void testCloseSuccess() {
        DatabaseClient client = Mockito.mock(DatabaseClient.class);
        Mockito.when(container.getComponentData(closeProperties.getReferencedComponentId(),
                MarkLogicConnection.CONNECTION)).thenReturn(client);
        closeStandalone.runAtDriver(container);

        Mockito.verify(container, Mockito.only()).getComponentData(closeProperties.getReferencedComponentId(),
                MarkLogicConnection.CONNECTION);
        Mockito.verify(client).release();
    }
}
