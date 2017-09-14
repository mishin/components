package org.talend.components.google.drive.put;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.google.drive.put.GoogleDrivePutProperties.UploadMode;
import org.talend.daikon.properties.presentation.Form;

public class GoogleDrivePutPropertiesTest {

    GoogleDrivePutProperties properties;

    @Before
    public void setUp() throws Exception {
        properties = new GoogleDrivePutProperties("test");
        properties.connection.setupProperties();
        properties.connection.setupLayout();
        properties.schemaMain.setupProperties();
        properties.schemaMain.setupLayout();
        properties.setupProperties();
        properties.setupLayout();
    }

    @Test
    public void testUploadMode() throws Exception {
        assertEquals("UPLOAD_LOCAL_FILE", UploadMode.UPLOAD_LOCAL_FILE.name());
        assertEquals(UploadMode.UPLOAD_LOCAL_FILE, UploadMode.valueOf("UPLOAD_LOCAL_FILE"));
        assertEquals("READ_CONTENT_FROM_INPUT", UploadMode.READ_CONTENT_FROM_INPUT.name());
        assertEquals(UploadMode.READ_CONTENT_FROM_INPUT, UploadMode.valueOf("READ_CONTENT_FROM_INPUT"));
        assertEquals("EXPOSE_OUTPUT_STREAM", UploadMode.EXPOSE_OUTPUT_STREAM.name());
        assertEquals(UploadMode.EXPOSE_OUTPUT_STREAM, UploadMode.valueOf("EXPOSE_OUTPUT_STREAM"));
    }

    @Test
    public void testGetAllSchemaPropertiesConnectors() throws Exception {
        assertThat(properties.getAllSchemaPropertiesConnectors(false), hasSize(1));
        assertThat(properties.getAllSchemaPropertiesConnectors(false), contains(properties.MAIN_CONNECTOR));
        assertThat(properties.getAllSchemaPropertiesConnectors(true), hasSize(1));
        assertThat(properties.getAllSchemaPropertiesConnectors(true), contains(properties.MAIN_CONNECTOR));
    }

    @Test
    public void testAfterUploadMode() throws Exception {
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.localFilePath.getName()).isVisible());
        properties.uploadMode.setValue(UploadMode.READ_CONTENT_FROM_INPUT);
        properties.afterUploadMode();
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.localFilePath.getName()).isVisible());
    }

}