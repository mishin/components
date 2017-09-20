// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.processing.definition.typeconverter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.PropertiesList;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class TypeConverterProperties extends FixedConnectorsComponentProperties implements Serializable {

    public enum TypeConverterOutputTypes {
        Boolean,
        Decimal,
        Double,
        Float,
        Integer,
        Long,
        Short,
        String,
        Character,
        Date
    }

    public static class TypeConverterPropertiesInner extends PropertiesImpl {

        public Property<String> field = PropertyFactory.newString("field").setRequired().setValue("");

        public Property<TypeConverterOutputTypes> outputType = PropertyFactory
                .newEnum("outputType", TypeConverterOutputTypes.class).setRequired();

        public Property<String> outputFormat = PropertyFactory.newString("outputFormat");

        public TypeConverterPropertiesInner(String name) {
            super(name);
        }
    }

    public PropertiesList<TypeConverterPropertiesInner> converters = new PropertiesList<>("converters",
            new PropertiesList.NestedPropertiesFactory() {

                @Override
                public Properties createAndInit(String name) {
                    return new TypeConverterPropertiesInner(name).init();
                }
            });

    public transient PropertyPathConnector INCOMING_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "incoming");

    public transient PropertyPathConnector OUTGOING_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "outgoing");

    public TypeConverterProperties(String name) {
        super(name);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new HashSet<PropertyPathConnector>();
        if (isOutputConnection) {
            connectors.add(OUTGOING_CONNECTOR);
        } else {
            // input schema
            connectors.add(INCOMING_CONNECTOR);
        }
        return connectors;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(converters);
    }
}
