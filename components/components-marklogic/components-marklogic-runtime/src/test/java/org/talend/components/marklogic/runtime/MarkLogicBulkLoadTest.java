package org.talend.components.marklogic.runtime;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.marklogic.tmarklogicbulkload.MarkLogicBulkLoadProperties;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MarkLogicBulkLoadTest {
    private MarkLogicBulkLoad runtime;
    private MarkLogicConnectionProperties connectionProperties;
    private MarkLogicBulkLoadProperties bulkLoadProperties;

    @Before
    public void setUp() {
        runtime = new MarkLogicBulkLoad();
        connectionProperties = new MarkLogicConnectionProperties("connectionProperties");
        bulkLoadProperties = new MarkLogicBulkLoadProperties("bulkLoadProperties");
    }

    @Test
    public void testI18N() {
        I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider().getI18nMessages(MarkLogicBulkLoad.class);
        assertFalse(i18nMessages.getMessage("messages.debug.command").equals("messages.debug.command"));
        assertFalse(i18nMessages.getMessage("messages.info.startBulkLoad").equals("messages.info.startBulkLoad"));
        assertFalse(i18nMessages.getMessage("messages.info.finishBulkLoad").equals("messages.info.finishBulkLoad"));
    }

    @Test
    @Ignore
    public void testIT() {
        connectionProperties.init();
        connectionProperties.host.setValue("127.0.0.1");
        connectionProperties.port.setValue(8000);
        connectionProperties.database.setValue("database");
        connectionProperties.username.setValue("root");
        connectionProperties.password.setValue("root");
        bulkLoadProperties.init();
        bulkLoadProperties.connection = connectionProperties;
        bulkLoadProperties.loadFolder.setValue("D:/data/bulk_test");
        bulkLoadProperties.docidPrefix.setValue("loaded/");

        ValidationResult vr = runtime.initialize(null, bulkLoadProperties);
        assertTrue(vr.getStatus().equals(ValidationResult.Result.OK));
        runtime.runAtDriver(null);

    }

}
