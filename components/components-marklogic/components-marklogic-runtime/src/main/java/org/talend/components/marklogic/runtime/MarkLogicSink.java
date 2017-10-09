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

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.marklogic.exceptions.MarkLogicErrorCode;
import org.talend.components.marklogic.exceptions.MarkLogicException;
import org.talend.components.marklogic.tmarklogicoutput.MarkLogicOutputProperties;

public class MarkLogicSink extends MarkLogicSourceOrSink implements Sink {

    @Override
    public MarkLogicWriteOperation createWriteOperation() {
        if (ioProperties instanceof MarkLogicOutputProperties) {
            return new MarkLogicWriteOperation(this, (MarkLogicOutputProperties) ioProperties);
        }
        else {
            throw new MarkLogicException(new MarkLogicErrorCode(MESSAGES.getMessage("error.wrongProperties")));
        }
    }

}
