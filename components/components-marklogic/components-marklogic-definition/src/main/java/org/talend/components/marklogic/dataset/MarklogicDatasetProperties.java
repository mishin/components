package org.talend.components.marklogic.dataset;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;


public class MarklogicDatasetProperties extends ComponentPropertiesImpl implements DatasetProperties<MarkLogicConnectionProperties> {

    public MarklogicDatasetProperties(String name) {
        super(name);
    }

    @Override
    public MarkLogicConnectionProperties getDatastoreProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDatastoreProperties(MarkLogicConnectionProperties datastoreProperties) {
        // TODO Auto-generated method stub

    }


}
