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
