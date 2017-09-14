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
import org.joda.time.Instant;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

public class MarkLogicReader extends AbstractBoundedReader<IndexedRecord> {

    public MarkLogicReader(BoundedSource source) {
        super(source);
    }

    @Override
    public boolean start() throws IOException {
        return false;
    }

    @Override
    public boolean advance() throws IOException {
        return false;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        return null;
    }

    @Override
    public Instant getCurrentTimestamp() throws NoSuchElementException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public BoundedSource getCurrentSource() {
        return super.getCurrentSource();
    }

    @Override
    public Map<String, Object> getReturnValues() {
        return null;
    }
}
