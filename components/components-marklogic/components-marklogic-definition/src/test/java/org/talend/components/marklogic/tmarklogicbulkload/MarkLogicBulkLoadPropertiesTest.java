package org.talend.components.marklogic.tmarklogicbulkload;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MarkLogicBulkLoadPropertiesTest {

    MarkLogicBulkLoadProperties bulkLoadProperties;

    @Before
    public void init() {
        bulkLoadProperties = new MarkLogicBulkLoadProperties("bulk load");
    }

    @Test
    public void testSetupProperties() {
        bulkLoadProperties.setupProperties();

        assertNotNull(bulkLoadProperties.connection);

        assertNotNull(bulkLoadProperties.loadFolder);
        assertNull(bulkLoadProperties.loadFolder.getStringValue());

        assertNotNull(bulkLoadProperties.docidPrefix);
        assertNull(bulkLoadProperties.docidPrefix.getStringValue());

        assertNotNull(bulkLoadProperties.mlcpParams);
        assertNull(bulkLoadProperties.mlcpParams.getStringValue());
    }

    @Test
    public void testSetupLayout() {
        bulkLoadProperties.init();

        Form mainForm = bulkLoadProperties.getForm(Form.MAIN);
        assertNotNull(mainForm.getWidget(bulkLoadProperties.connection));
        assertNotNull(mainForm.getChildForm(bulkLoadProperties.connection.getName())
                .getChildForm(bulkLoadProperties.connection.getName()));
    }
}
