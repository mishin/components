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
package org.talend.components.processing.definition.pocaggregate;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;


public class PocAggregatePropertiesTest {

    /**
     * Checks {@link PocAggregateProperties} sets correctly initial schema property
     */
    @Test
    public void testDefaultProperties() {
        PocAggregateProperties properties = new PocAggregateProperties("test");
    }

    /**
     * Checks {@link PocAggregateProperties} sets correctly initial layout properties
     */
    @Test
    public void testSetupLayout() {
        PocAggregateProperties properties = new PocAggregateProperties("test");

        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, Matchers.notNullValue());
        /* Example of tests
        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(2));
        Widget recordCount = main.getWidget("recordCount");
        assertThat(recordCount, notNullValue());
        Widget showCountRecord = main.getWidget("showCountRecord");
        assertThat(showCountRecord, notNullValue());
         */
    }

    /**
     * Checks {@link PocAggregateProperties#refreshLayout(Form)}
     */
    @Test
    public void testRefreshLayout() {
        PocAggregateProperties properties = new PocAggregateProperties("test");
        properties.init();
        properties.refreshLayout(properties.getForm(Form.MAIN));
        /* Example of  test
        properties.showCountRecord.setValue(false);
        properties.refreshLayout(properties.getForm(Form.MAIN));
        assertTrue(properties.getForm(Form.MAIN).getWidget("recordCount").isVisible());

        properties.showCountRecord.setValue(true);
        properties.refreshLayout(properties.getForm(Form.MAIN));
        assertTrue(properties.getForm(Form.MAIN).getWidget("recordCount").isHidden());
         */
    }
}
