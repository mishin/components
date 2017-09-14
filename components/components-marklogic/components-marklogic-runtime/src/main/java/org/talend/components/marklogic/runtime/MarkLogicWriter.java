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
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;

import java.io.IOException;

public class MarkLogicWriter implements Writer<IndexedRecord> {

    @Override
    public void open(String uId) throws IOException {

    }

    @Override
    public void write(Object object) throws IOException {

    }

    @Override
    public IndexedRecord close() throws IOException {
        return null;
    }

    @Override
    public WriteOperation<IndexedRecord> getWriteOperation() {
        return null;
    }
}
