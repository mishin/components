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

import java.io.IOException;

import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.marklogic.connection.MarkLogicConnection;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.exception.ExceptionContext.ExceptionContextBuilder;
import org.talend.daikon.exception.error.DefaultErrorCode;
import org.talend.daikon.exception.error.ErrorCode;
import org.talend.daikon.properties.ValidationResult;

import com.marklogic.client.FailedRequestException;

/**
 * Implementation of runtime part for tMarkLogicConnection component.
 *
 */
public class TMarkLogicConnectionStandalone extends MarkLogicConnection
        implements ComponentDriverInitialization<MarkLogicConnectionProperties> {

    private static final long serialVersionUID = -40535886003777462L;

    private static final String ERROR_KEY = "message";

    private MarkLogicConnectionProperties properties;

    @Override
    public void runAtDriver(RuntimeContainer container) {

        try {
            connect(container);
        } catch (IOException e) {
            ErrorCode errorCode;
            String message;
            if (e.getCause() instanceof FailedRequestException) {
                errorCode = new DefaultErrorCode(403, ERROR_KEY);
                message = "Invalid credentials.";
            } else {
                errorCode = new DefaultErrorCode(400,ERROR_KEY);
                message = "Cannot connect to MarkLogic database. Check your database connectivity.";
            }
            throw new ComponentException(errorCode, e, new ExceptionContextBuilder().put(ERROR_KEY, message).build());
        }
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, MarkLogicConnectionProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    @Override
    protected MarkLogicConnectionProperties getMarkLogicConnectionProperties() {
        return properties;
    }
}
