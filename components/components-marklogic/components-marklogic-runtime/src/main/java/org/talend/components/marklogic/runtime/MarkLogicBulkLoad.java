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

import com.marklogic.contentpump.ContentPump;
import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.marklogic.tmarklogicbulkload.MarkLogicBulkLoadProperties;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MarkLogicBulkLoad implements ComponentDriverInitialization {
    private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(MarkLogicBulkLoad.class);

    private MarkLogicBulkLoadProperties bulkLoadProperties;
    @Override
    public void runAtDriver(RuntimeContainer container) {
        try {
            ContentPump.runCommand(new String[] {"import",
                    "-username", bulkLoadProperties.connection.username.getStringValue(),
                    "-password", bulkLoadProperties.connection.password.getStringValue(),
                    "-host", bulkLoadProperties.connection.host.getStringValue(),
                    "-port", String.valueOf(bulkLoadProperties.connection.port.getValue()),
                    "-database", bulkLoadProperties.connection.database.getStringValue(),
                    "-output_uri_replace", "\"D:/data/bulk_test/,'" + bulkLoadProperties.docidPrefix.getStringValue() + "'\"",
                    "-input_file_path", bulkLoadProperties.loadFolder.getStringValue()});
        } catch (Exception e) {
            e.printStackTrace();
            //TODO do here smth
        }
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, Properties properties) {
        ValidationResultMutable validationResult = new ValidationResultMutable();
        if (properties instanceof MarkLogicBulkLoadProperties) {
            bulkLoadProperties = (MarkLogicBulkLoadProperties) properties;
            validationResult.setStatus(ValidationResult.Result.OK);
        } else {
            validationResult.setStatus(ValidationResult.Result.ERROR);
            validationResult.setMessage(i18nMessages.getMessage("error.wrongProperties"));

        }
        return validationResult;
    }
}
