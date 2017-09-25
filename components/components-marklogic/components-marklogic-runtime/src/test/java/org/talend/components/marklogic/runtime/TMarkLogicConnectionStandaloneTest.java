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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.marklogic.connection.MarkLogicConnection;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.Transaction;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DatabaseClientFactory.class)
public class TMarkLogicConnectionStandaloneTest {

    private TMarkLogicConnectionStandalone connectionStandalone;

    private RuntimeContainer container;

    private MarkLogicConnectionProperties connectionProperties;

    private DatabaseClient client;

    @Before
    public void setup() {
        connectionStandalone = new TMarkLogicConnectionStandalone();
        PowerMockito.mockStatic(DatabaseClientFactory.class);
        client = Mockito.mock(DatabaseClient.class);
        Mockito.when(DatabaseClientFactory.newClient(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(SecurityContext.class))).thenReturn(client);
        container = Mockito.mock(RuntimeContainer.class);
        Mockito.when(container.getCurrentComponentId()).thenReturn("connectionComponent");
        connectionProperties = new MarkLogicConnectionProperties("connection");
        connectionProperties.authentication.setValue("BASIC");
        connectionStandalone.initialize(container, connectionProperties);
    }

    @Test
    public void testConnectSuccess() {
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(client.openTransaction()).thenReturn(transaction);

        connectionStandalone.runAtDriver(container);

        Mockito.verify(transaction, Mockito.only()).commit();
        Mockito.verify(container).setComponentData("connectionComponent", MarkLogicConnection.CONNECTION, client);
    }
}
