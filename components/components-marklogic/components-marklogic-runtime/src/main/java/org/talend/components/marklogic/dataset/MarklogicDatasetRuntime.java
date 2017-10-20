package org.talend.components.marklogic.dataset;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.common.dataset.runtime.DatasetRuntime;
import org.talend.daikon.java8.Consumer;
import org.talend.daikon.properties.ValidationResult;

/**
 *
 *
 */
public class MarklogicDatasetRuntime implements DatasetRuntime<MarklogicDatasetProperties> {

    private MarklogicDatasetProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, MarklogicDatasetProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public void getSample(int limit, Consumer<IndexedRecord> consumer) {

    }

}
