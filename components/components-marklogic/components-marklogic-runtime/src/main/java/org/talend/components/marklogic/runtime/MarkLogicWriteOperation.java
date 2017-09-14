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

import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

import java.util.Map;

public class MarkLogicWriteOperation implements WriteOperation<IndexedRecord> {

    @Override
    public void initialize(RuntimeContainer adaptor) {

    }

    @Override
    public Map<String, Object> finalize(Iterable<IndexedRecord> writerResults, RuntimeContainer adaptor) {
        return null;
    }

    @Override
    public Writer<IndexedRecord> createWriter(RuntimeContainer adaptor) {
        return null;
    }

    @Override
    public Sink getSink() {
        return null;
    }
}
