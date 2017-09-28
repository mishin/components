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

import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.marklogic.tmarklogicbulkload.MarkLogicBulkLoadProperties;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

public class MarkLogicBulkLoad implements ComponentDriverInitialization {
    private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(MarkLogicBulkLoad.class);

    private MarkLogicBulkLoadProperties bulkLoadProperties;
    @Override
    public void runAtDriver(RuntimeContainer container) {
        try {
        } catch (Exception e) {
        }
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, Properties properties) {
        ValidationResultMutable validationResult = new ValidationResultMutable();
        validationResult.setStatus(ValidationResult.Result.OK);
        if (properties instanceof MarkLogicBulkLoadProperties) {
            bulkLoadProperties = (MarkLogicBulkLoadProperties) properties;
            boolean isRequiredPropertiesSet = !bulkLoadProperties.connection.host.getStringValue().isEmpty()
                    && bulkLoadProperties.connection.port.getValue() != null
                    && !bulkLoadProperties.connection.database.getStringValue().isEmpty()
                    && !bulkLoadProperties.connection.username.getStringValue().isEmpty()
                    && !bulkLoadProperties.connection.password.getStringValue().isEmpty()
                    && !bulkLoadProperties.loadFolder.getStringValue().isEmpty();
            if (!isRequiredPropertiesSet) {
                validationResult.setStatus(ValidationResult.Result.ERROR);
                validationResult.setMessage(i18nMessages.getMessage("error.missedProperties"));
            }
        } else {
            validationResult.setStatus(ValidationResult.Result.ERROR);
            validationResult.setMessage(i18nMessages.getMessage("error.wrongProperties"));
        }

        return validationResult;
    }
}
