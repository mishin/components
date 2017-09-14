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
package org.talend.components.marklogic.connection;

import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

public class TMarkLogicConnectionStandalone implements ComponentDriverInitialization {

    @Override
    public void runAtDriver(RuntimeContainer container) {

        System.out.println("Connected");
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, Properties properties) {
        return ValidationResult.OK;
    }
}
