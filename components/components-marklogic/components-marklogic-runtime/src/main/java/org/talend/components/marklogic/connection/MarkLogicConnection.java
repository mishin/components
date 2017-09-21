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
package org.talend.components.marklogic.connection;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;

/**
 * Common implementation for creating or getting connection to MarkLogic database.
 *
 */
public abstract class MarkLogicConnection {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(MarkLogicConnection.class);

    public static final String CONNECTION = "connection";

    /**
     * Creates new {@link DatabaseClient} connection or gets referenced one from container.
     *
     * @param container
     * @return connection to MarkLogic database.
     * @throws IOException thrown if referenced connection is not connected.
     */
    public DatabaseClient connect(RuntimeContainer container) throws IOException {
        MarkLogicConnectionProperties properties = getMarkLogicConnectionProperties();
        if (properties.getReferencedComponentId() != null) {
            if (container != null) {
                DatabaseClient client = (DatabaseClient) container.getComponentData(container.getCurrentComponentId(),
                        CONNECTION);
                if (client != null) {
                    return client;
                }
                throw new IOException("Referenced component: " + properties.getReferencedComponentId() + " not connected");
            }
            properties = properties.referencedComponent.getReference();
        }

        SecurityContext context = "BASIC".equals(properties.authentication.getValue())
                ? new DatabaseClientFactory.BasicAuthContext(properties.username.getValue(), properties.password.getValue())
                : new DatabaseClientFactory.DigestAuthContext(properties.username.getValue(), properties.password.getValue());
        DatabaseClient client = DatabaseClientFactory.newClient(properties.host.getValue(), properties.port.getValue(),
                properties.database.getValue(), context);
        LOGGER.info("Connected to MarkLogic server");
        if (container != null) {
            container.setComponentData(container.getCurrentComponentId(), CONNECTION, client);
            LOGGER.info("Connection stored in container");
        }
        return client;
    }

    protected abstract MarkLogicConnectionProperties getMarkLogicConnectionProperties();

}
