package org.talend.components.marklogic.tmarklogicclose;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;

import static org.junit.Assert.*;

public class MarkLogicClosePropertiesTest {

    MarkLogicCloseProperties closeProperties;

    @Before
    public void reset() {
        closeProperties = new MarkLogicCloseProperties("close");
    }

    @Test
    public void testSetupLayout() {
        assertEquals(0, closeProperties.getForms().size());
        closeProperties.setupLayout();
        assertEquals(1, closeProperties.getForms().size());
        assertNotNull(closeProperties.getForm(Form.MAIN).getWidget(closeProperties.referencedComponent.getName()));
    }

    @Test
    public void testGetReferencedComponentId() {
        String expectedStringValue;
        String referencedComponentId;

        expectedStringValue = "SomeStringValue";
        closeProperties.referencedComponent.componentInstanceId.setValue(expectedStringValue);
        referencedComponentId = closeProperties.getReferencedComponentId();

        assertEquals(referencedComponentId, expectedStringValue);
    }

}