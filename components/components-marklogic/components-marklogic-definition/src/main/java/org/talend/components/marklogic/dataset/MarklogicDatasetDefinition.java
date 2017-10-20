package org.talend.components.marklogic.dataset;

import org.talend.components.common.dataset.DatasetDefinition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.runtime.RuntimeInfo;


public class MarklogicDatasetDefinition extends I18nDefinition implements DatasetDefinition<MarklogicDatasetProperties> {

    public static final String NAME = "MarklogicDataset";


    public MarklogicDatasetDefinition() {
        super(NAME);
    }

    @Override
    public Class<MarklogicDatasetProperties> getPropertiesClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getImagePath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getImagePath(DefinitionImageType type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIconKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDisplayName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setI18nMessageFormatter(I18nMessages i18nMessages) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getI18nMessage(String key, Object... arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(MarklogicDatasetProperties properties) {
        return null;
    }

}
