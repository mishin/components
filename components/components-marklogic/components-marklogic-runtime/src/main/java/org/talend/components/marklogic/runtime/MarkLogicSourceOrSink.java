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

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.marklogic.MarkLogicProvideConnectionProperties;
import org.talend.components.marklogic.connection.MarkLogicConnection;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;

public class MarkLogicSourceOrSink extends MarkLogicConnection implements SourceOrSink {

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider().getI18nMessages(MarkLogicSourceOrSink.class);

    protected MarkLogicProvideConnectionProperties ioProperties;
    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        ValidationResultMutable vr = new ValidationResultMutable();
        try {
            connect(container);
        }
        catch (NullPointerException e) {
            vr.setStatus(ValidationResult.Result.ERROR);
            vr.setMessage(MESSAGES.getMessage("error.cannotCreateConnection", e.getMessage()));
        }
        catch (IOException e) {
            vr.setStatus(ValidationResult.Result.ERROR);
            vr.setMessage(MESSAGES.getMessage("error.cannotConnect", e.getMessage()));
        }

        return vr;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        ValidationResultMutable vr = new ValidationResultMutable();
        if (properties instanceof MarkLogicProvideConnectionProperties) {
            this.ioProperties = (MarkLogicProvideConnectionProperties) properties;
        }
        else {
            vr.setStatus(ValidationResult.Result.ERROR);
            vr.setMessage(MESSAGES.getMessage("error.wrongProperties"));
        }
        return vr;
    }

    @Override
    protected MarkLogicConnectionProperties getMarkLogicConnectionProperties() {
        return ioProperties.getConnectionProperties();
    }
}
