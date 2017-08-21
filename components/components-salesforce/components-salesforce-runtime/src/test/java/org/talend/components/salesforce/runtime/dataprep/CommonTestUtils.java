package org.talend.components.salesforce.runtime.dataprep;

import org.talend.components.salesforce.datastore.SalesforceConnectionProperties;

public class CommonTestUtils {

    public static void setValueForDatastoreProperties(SalesforceConnectionProperties datastore) {
        datastore.userId.setValue(System.getProperty("salesforce.user"));
        datastore.password.setValue(System.getProperty("salesforce.password"));
        datastore.securityKey.setValue(System.getProperty("salesforce.key"));
    }
    
}
