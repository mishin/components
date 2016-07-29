// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.s3.tawss3get;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.s3.AwsS3ConnectionProperties;
import org.talend.components.s3.AwsS3ConnectionPropertiesProvider;
import org.talend.components.s3.AwsS3FileBucketKeyProperties;
import org.talend.daikon.properties.presentation.Form;

/**
 * created by dmytro.chmyga on Jul 27, 2016
 */
public class TAwsS3GetProperties extends FixedConnectorsComponentProperties implements AwsS3ConnectionPropertiesProvider {

    public AwsS3ConnectionProperties connectionProperties = new AwsS3ConnectionProperties("connectionProperties");

    public AwsS3FileBucketKeyProperties fileBucketKeyProperties = new AwsS3FileBucketKeyProperties("fileBucketKeyProperties");

    public SchemaProperties reject = new SchemaProperties("reject");

    protected transient PropertyPathConnector REJECT_CONNECTOR = new PropertyPathConnector(Connector.REJECT_NAME, "reject");

    /**
     * DOC dmytro.chmyga TAwsS3GetProperties constructor comment.
     * 
     * @param name
     */
    public TAwsS3GetProperties(String name) {
        super(name);
    }

    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connectionProperties.getForm(Form.REFERENCE));
        mainForm.addRow(fileBucketKeyProperties.getForm(Form.MAIN));

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(connectionProperties.getForm(Form.ADVANCED));
    }

    @Override
    public AwsS3ConnectionProperties getConnectionProperties() {
        return connectionProperties;
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnection) {
            connectors.add(REJECT_CONNECTOR);
            return connectors;
        }
        return Collections.emptySet();
    }

    public Schema getSchema() {
        return reject.schema.getValue();
    }

}