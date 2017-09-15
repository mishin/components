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
package org.talend.components.marklogic.tmarklogicinput;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.daikon.properties.presentation.Form;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;

public class MarkLogicInputPropertiesTest {

    MarkLogicInputProperties testInputProperties;

    @Before
    public void init() {
        testInputProperties = new MarkLogicInputProperties("testInputProperties");
        testInputProperties.connection.init();
    }

    /**
     * Checks forms are filled with required widgets
     */
    @Test
    public void testSetupLayout() {
        testInputProperties.schema.init();

        testInputProperties.setupLayout();
        Form main = testInputProperties.getForm(Form.MAIN);
        assertNotNull(main.getWidget(testInputProperties.connection));
        assertNotNull(main.getWidget(testInputProperties.criteria));
        //should not be on main form
        assertNull(main.getWidget(testInputProperties.maxRetrieve));
        assertNull(main.getWidget(testInputProperties.pageSize));
        assertNull(main.getWidget(testInputProperties.useQueryOption));
        assertNull(main.getWidget(testInputProperties.queryLiteralType));
        assertNull(main.getWidget(testInputProperties.queryOptionName));
        assertNull(main.getWidget(testInputProperties.queryOptionLiterals));

        Form advanced = testInputProperties.getForm(Form.ADVANCED);
        assertNotNull(advanced.getWidget(testInputProperties.maxRetrieve));
        assertNotNull(advanced.getWidget(testInputProperties.pageSize));
        assertNotNull(advanced.getWidget(testInputProperties.useQueryOption));
        assertNotNull(advanced.getWidget(testInputProperties.queryLiteralType));
        assertNotNull(advanced.getWidget(testInputProperties.queryOptionName));
        assertNotNull(advanced.getWidget(testInputProperties.queryOptionLiterals));
    }

    /**
     * Checks default values are set correctly
     */
    @Test
    public void testSetupProperties() {
        String expectedDefaultHost = "127.0.0.1";
        Integer expectedDefaultPort = 8000;
        String expectedDefaultDataBase = "Documents";
        Integer expectedDefaultMaxRetrieveNumber = -1;
        Integer expectedDefaultPageSize = 10;
        Boolean expectedDefaultUseQueryOption = false;
        String expectedDefaultQueryLiteralType = "XML";

        testInputProperties.setupProperties();

        assertEquals(expectedDefaultHost, testInputProperties.connection.host.getValue());
        assertEquals(expectedDefaultPort, testInputProperties.connection.port.getValue());
        assertEquals(expectedDefaultDataBase, testInputProperties.connection.database.getValue());
        assertNull(testInputProperties.connection.username.getValue());
        assertNull(testInputProperties.connection.password.getValue());
        assertNull(testInputProperties.criteria.getValue());
        assertNull(testInputProperties.criteria.getValue());
        assertEquals(expectedDefaultMaxRetrieveNumber, testInputProperties.maxRetrieve.getValue());
        assertEquals(expectedDefaultPageSize, testInputProperties.pageSize.getValue());
        assertEquals(expectedDefaultUseQueryOption, testInputProperties.useQueryOption.getValue());
        assertEquals(expectedDefaultQueryLiteralType, testInputProperties.queryLiteralType.getValue());
        assertNull(testInputProperties.queryOptionName.getValue());
        assertNull(testInputProperties.queryOptionLiterals.getValue());
    }

    @Test
    public void testSchemaIsLocked() {
        testInputProperties.setupSchema();
        assertEquals("true", testInputProperties.schema.schema.getValue().getProp(TALEND_IS_LOCKED));
    }

    @Test
    public void testGetAllSchemaPropertiesConnectors() {
        Set<PropertyPathConnector> actualConnectors = testInputProperties.getAllSchemaPropertiesConnectors(true);
    }

    /**
     * Checks initial layout
     */
    @Test
    @Ignore
    public void testRefreshLayout() {
        MarkLogicInputProperties properties = new MarkLogicInputProperties("root");
        properties.init();

        properties.refreshLayout(properties.getForm(Form.MAIN));

        boolean schemaHidden = properties.getForm(Form.MAIN).getWidget("schema").isHidden();
        assertFalse(schemaHidden);

        boolean filenameHidden = properties.getForm(Form.MAIN).getWidget("filename").isHidden();
        assertFalse(filenameHidden);

        boolean useCustomDelimiterHidden = properties.getForm(Form.MAIN).getWidget("useCustomDelimiter").isHidden();
        assertFalse(useCustomDelimiterHidden);

        boolean delimiterHidden = properties.getForm(Form.MAIN).getWidget("delimiter").isHidden();
        assertFalse(delimiterHidden);

        boolean customDelimiterHidden = properties.getForm(Form.MAIN).getWidget("customDelimiter").isHidden();
        assertTrue(customDelimiterHidden);

        boolean guessSchemaHidden = properties.getForm(Form.MAIN).getWidget("guessSchema").isHidden();
        assertFalse(guessSchemaHidden);
    }
}
