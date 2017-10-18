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
package org.talend.components.marklogic.tmarklogicconnection;

import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.components.marklogic.RuntimeInfoProvider;
import org.talend.components.marklogic.tmarklogicinput.MarkLogicInputDefinition;
import org.talend.components.marklogic.tmarklogicoutput.MarkLogicOutputDefinition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

public class MarkLogicConnectionDefinition extends I18nDefinition implements DatastoreDefinition<MarkLogicConnectionProperties> {

    private static final long serialVersionUID = 6898364632388586813L;

    public static final String COMPONENT_NAME = "tMarkLogicNEWConnection"; //$NON-NLS-1$

    public MarkLogicConnectionDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public DatasetProperties<MarkLogicConnectionProperties> createDatasetProperties(MarkLogicConnectionProperties storeProp) {
        return null;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(MarkLogicConnectionProperties properties) {
        return RuntimeInfoProvider.getCommonRuntimeInfo("org.talend.components.marklogic.runtime.TMarkLogicConnectionStandalone");
    }

    @Override
    public String getInputCompDefinitionName() {
        return MarkLogicInputDefinition.COMPONENT_NAME;
    }

    @Override
    public String getOutputCompDefinitionName() {
        return MarkLogicOutputDefinition.COMPONENT_NAME;
    }

    @Override
    public Class<MarkLogicConnectionProperties> getPropertiesClass() {
        return MarkLogicConnectionProperties.class;
    }

    @Override
    public String getImagePath() {
        return COMPONENT_NAME + "_icon32.png";
    }

    @Override
    public String getImagePath(DefinitionImageType type) {
        switch (type) {
        case PALETTE_ICON_32X32:
            return COMPONENT_NAME + "_icon32.png";
        default:
            return null;
        }
    }

    @Override
    public String getIconKey() {
        return null;
    }

}
