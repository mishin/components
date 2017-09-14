package org.talend.components.marklogic.runtime;

import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.marklogic.tmarklogicinput.MarkLogicInputProperties;
import org.talend.daikon.properties.ValidationResult;

import java.util.List;

public class MarkLogicSource extends MarkLogicSourceOrSink implements BoundedSource {

    private MarkLogicInputProperties inputProperties;

    @Override
    public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor)
            throws Exception {
        return null;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {

        this.inputProperties = (MarkLogicInputProperties) properties;
        return ValidationResult.OK;
    }

    @Override
    public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
        return 0;
    }

    @Override
    public boolean producesSortedKeys(RuntimeContainer adaptor) {
        return false;
    }

    @Override
    public BoundedReader createReader(RuntimeContainer adaptor) {
        return new MarkLogicReader(this);
    }
}
