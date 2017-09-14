package org.talend.components.google.drive.get;

import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.CSV;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.CSV_TAB;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.EPUB;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.EXCEL;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.HTML;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.HTML_ZIPPED;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.JPG;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.OO_DOCUMENT;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.OO_PRESENTATION;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.OO_SPREADSHEET;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.PDF;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.PNG;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.POWERPOINT;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.RTF;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.SVG;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.TEXT;
import static org.talend.components.google.drive.GoogleDriveMimeTypes.MimeTypes.WORD;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.components.google.drive.GoogleDriveComponentProperties;
import org.talend.components.google.drive.GoogleDriveMimeTypes;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class GoogleDriveGetProperties extends GoogleDriveComponentProperties {

    public Property<String> fileName = newString("fileName").setRequired();

    public Property<Boolean> storeToLocal = newBoolean("storeToLocal");

    public Property<String> outputFileName = newString("outputFileName").setRequired();

    // Advanced properties

    public Property<GoogleDriveMimeTypes.MimeTypes> exportDocument = newEnum("exportDocument",
            GoogleDriveMimeTypes.MimeTypes.class);

    public Property<GoogleDriveMimeTypes.MimeTypes> exportDrawing = newEnum("exportDrawing", GoogleDriveMimeTypes.MimeTypes.class);

    public Property<GoogleDriveMimeTypes.MimeTypes> exportPresentation = newEnum("exportPresentation",
            GoogleDriveMimeTypes.MimeTypes.class);

    public Property<GoogleDriveMimeTypes.MimeTypes> exportSpreadsheet = newEnum("exportSpreadsheet",
            GoogleDriveMimeTypes.MimeTypes.class);

    public Property<Boolean> setOutputExt = newBoolean("setOutputExt");

    public GoogleDriveGetProperties(String name) {
        super(name);
    }

    public void setupProperties() {
        super.setupProperties();

        Schema schema = SchemaBuilder.builder().record("GoogleDriveGet").fields() //
                .name("content").type(AvroUtils._bytes()).noDefault() //
                .endRecord();
        schema.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
        schemaMain.schema.setValue(schema);

        fileName.setValue("");
        storeToLocal.setValue(false);
        outputFileName.setValue("");
        //
        exportDocument.setPossibleValues(HTML, HTML_ZIPPED, TEXT, RTF, OO_DOCUMENT, PDF, WORD, EPUB);
        exportDocument.setValue(WORD);
        exportDrawing.setPossibleValues(JPG, PNG, SVG, PDF);
        exportDrawing.setValue(PNG);
        exportPresentation.setPossibleValues(POWERPOINT, OO_PRESENTATION, PDF, TEXT);
        exportPresentation.setValue(PDF);
        exportSpreadsheet.setPossibleValues(EXCEL, OO_SPREADSHEET, PDF, CSV, CSV_TAB, HTML_ZIPPED);
        exportSpreadsheet.setValue(EXCEL);
        setOutputExt.setValue(false);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(fileName);
        mainForm.addRow(storeToLocal);
        mainForm.addRow(widget(outputFileName).setWidgetType(Widget.FILE_WIDGET_TYPE));
        mainForm.addRow(schemaMain.getForm(Form.REFERENCE));

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(exportDocument);
        advancedForm.addRow(exportDrawing);
        advancedForm.addRow(exportPresentation);
        advancedForm.addRow(exportSpreadsheet);
        advancedForm.addRow(setOutputExt);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (Form.MAIN.equals(form.getName())) {
            if (storeToLocal.getValue()) {
                form.getWidget(outputFileName.getName()).setVisible(true);
            } else {
                form.getWidget(outputFileName.getName()).setVisible(false);
            }
        }
        if (Form.ADVANCED.equals(form.getName())) {
            form.getWidget(setOutputExt.getName()).setVisible(storeToLocal.getValue());
        }
    }

    public void afterStoreToLocal() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }
}