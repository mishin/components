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
package org.talend.components.salesforce;

import org.talend.components.salesforce.datastore.SalesforceDatastoreProperties2;

public interface SalesforceProvideDatastoreProperties {

    /**
     *
     * @return the {@link SalesforceDatastoreProperties2} associated with this
     * {@link org.talend.components.api.properties.ComponentProperties} object.
     */
    SalesforceDatastoreProperties2 getSalesforceDatastoreProperties();
}
