// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.processing.definition;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.ErrorCode;

/**
 * Error codes for the {@link ProcessingFamilyDefinition}.
 */
public enum ProcessingErrorCode implements ErrorCode {

    /** The user attempted to access a field that was not found in the record. */
    FIELD_NOT_FOUND("FIELD_NOT_FOUND", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "field"),

    AVPATH_SYNTAX_ERROR("AVPATH_SYNTAX_ERROR", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "query", "position");

    private final String code;

    private final int httpStatus;

    private final Collection<String> contextEntries;

    private ProcessingErrorCode(String code, int httpStatus, String... contextEntries) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.contextEntries = Arrays.asList(contextEntries);
    }

    @Override
    public String getProduct() {
        return "Talend";
    }

    @Override
    public String getGroup() {
        return ProcessingFamilyDefinition.NAME;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public Collection<String> getExpectedContextEntries() {
        return contextEntries;
    }

    @Override
    public String getCode() {
        return code;
    }

    /**
     * Create an exception with the error code and context for {@link #FIELD_NOT_FOUND}.
     *
     * @param cause The technical exception that was caught when the error occurred.
     * @param field The field that the user was attempting to write to.
     * @return An exception corresponding to the error code.
     */
    public static TalendRuntimeException createFieldNotFoundException(Throwable cause, String field) {
        return new TalendMsgRuntimeException(cause, FIELD_NOT_FOUND, ExceptionContext.withBuilder().put("field", field).build(),
                "The field '" + field + "' was not found.");
    }

    /**
     * Create a {@link #AVPATH_SYNTAX_ERROR} exception for when the user entered a bad avpath expression.
     *
     * @param cause The technical exception that was caught when the error occurred.
     * @param query The avpath query that failed.
     * @return An exception corresponding to the error code.
     */
    public static TalendRuntimeException createAvpathSyntaxError(Throwable cause, String query, int position) {
        String msg;
        if (position == -1) {
            msg = "The avpath query '" + query + "' is invalid.";
        } else {
            msg = "The avpath '" + query + "' is invalid at position " + position + ".";
        }
        return new TalendMsgRuntimeException(cause, AVPATH_SYNTAX_ERROR,
                ExceptionContext.withBuilder().put("query", query).put("position", position).build(), msg);
    }

    /**
     * {@link TalendRuntimeException} with a reasonable user-friendly message in English.
     */
    private static class TalendMsgRuntimeException extends TalendRuntimeException {

        private final String localizedMessage;

        public TalendMsgRuntimeException(Throwable cause, ErrorCode code, ExceptionContext context, String localizedMessage) {
            super(code, cause, context);
            this.localizedMessage = localizedMessage;
        }

        @Override
        public String getMessage() {
            return getLocalizedMessage();
        }

        @Override
        public String getLocalizedMessage() {
            return localizedMessage;
        }
    }
}
