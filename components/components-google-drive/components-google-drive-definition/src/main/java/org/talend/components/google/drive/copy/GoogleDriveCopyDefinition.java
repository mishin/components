package org.talend.components.google.drive.copy;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.google.drive.GoogleDriveComponentDefinition;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class GoogleDriveCopyDefinition extends GoogleDriveComponentDefinition {

    public static final String COMPONENT_NAME = "tGoogleDriveCopy";//$NON-NLS-1$

    public static final String RETURN_SOURCEID = "sourceID"; //$NON-NLS-1$

    public static final Property<String> RETURN_SOURCEID_PROP = PropertyFactory.newString(RETURN_SOURCEID);

    public static final String RETURN_DESTINATIONID = "destinationID"; //$NON-NLS-1$

    public static final Property<String> RETURN_DESTINATIONID_PROP = PropertyFactory.newString(RETURN_DESTINATIONID);

    public GoogleDriveCopyDefinition() {
        super(COMPONENT_NAME);
        setupI18N(new Property<?>[] { RETURN_SOURCEID_PROP, RETURN_DESTINATIONID_PROP });
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_ERROR_MESSAGE_PROP, RETURN_SOURCEID_PROP, RETURN_DESTINATIONID_PROP };
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return GoogleDriveCopyProperties.class;
    }

}
