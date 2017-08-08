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
package org.talend.components.processing.definition.filterrow;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class FilterRowCriteriaPropertiesTest {

    /**
     * Checks {@link FilterRowCriteriaProperties} sets correctly initial schema property
     */
    @Test
    public void testDefaultProperties() {
        FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("test");
        assertEquals("", properties.columnName.getValue());
        assertEquals("EMPTY", properties.function.getValue());
        assertEquals("==", properties.operator.getValue());
        assertNull(properties.value.getValue());
    }

    /**
     * Checks {@link FilterRowCriteriaProperties} update correctly * schema property
     */
    @Ignore("Need to be able to have a schema in order to provide a column name checking.")
    @Test
    public void testConditions() {
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("test");
        // properties.init();
        // AvroRegistry registry = new AvroRegistry();
        // Schema stringSchema = registry.getConverter(String.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", stringSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", stringSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        // properties.main.schema.setValue(inputSchema);
        //
        // // default value, "columName" will change
        // properties.schemaListener.afterSchema();
        //
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals("EMPTY", properties.function.getValue());
        // assertEquals("==", properties.operator.getValue());
        // assertNull(properties.value.getValue());
        //
        // // specific value, "function" will change cause inputValue1's type is
        // // not a compatible with "ABS_VALUE"
        // properties.columnName.setValue("inputValue1");
        // properties.function.setValue("ABS_VALUE");
        // properties.operator.setValue("!=");
        // properties.value.setValue("1111");
        // properties.schemaListener.afterSchema();
        //
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals("EMPTY", properties.function.getValue());
        // assertEquals("!=", properties.operator.getValue());
        // assertEquals("1111", properties.value.getValue());
        //
        // // specific value, will not change
        // properties.columnName.setValue("inputValue2");
        // properties.function.setValue("LC");
        // properties.operator.setValue("==");
        // properties.value.setValue("2222");
        // properties.schemaListener.afterSchema();
        //
        // assertEquals("inputValue2", properties.columnName.getValue());
        // assertEquals("LC", properties.function.getValue());
        // assertEquals("==", properties.operator.getValue());
        // assertEquals("2222", properties.value.getValue());
        //
        // // specific value, "operator" will change cause the function "MATCH" is
        // // not a compatible with "<"
        // properties.columnName.setValue("inputValue1");
        // properties.function.setValue("MATCH");
        // properties.operator.setValue("<");
        // properties.value.setValue("3333");
        // properties.schemaListener.afterSchema();
        //
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals("MATCH", properties.function.getValue());
        // assertEquals("==", properties.operator.getValue());
        // assertEquals("3333", properties.value.getValue());
        //
        // // specific value, "operator" will change cause the function "CONTAINS" is
        // // not a compatible with "<"
        // properties.columnName.setValue("inputValue1");
        // properties.function.setValue("CONTAINS");
        // properties.operator.setValue("<");
        // properties.value.setValue("4444");
        // properties.schemaListener.afterSchema();
        //
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals("CONTAINS", properties.function.getValue());
        // assertEquals("==", properties.operator.getValue());
        // assertEquals("4444", properties.value.getValue());
    }

    /**
     * Checks {@link FilterRowCriteriaProperties#refreshLayout(Form)}
     */
    @Ignore("Need to be able to have a schema in order to provide a column name checking.")
    @Test
    public void testRefreshLayoutMainInitial() {
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("test");
        // properties.init();
        // AvroRegistry registry = new AvroRegistry();
        // Schema stringSchema = registry.getConverter(String.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", stringSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", stringSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        // properties.main.schema.setValue(inputSchema);
        //
        // properties.refreshLayout(properties.getForm(Form.MAIN));
        // assertTrue(properties.getForm(Form.MAIN).getWidget("columnName").isVisible());
        // assertTrue(properties.getForm(Form.MAIN).getWidget("function").isVisible());
        // assertTrue(properties.getForm(Form.MAIN).getWidget("operator").isVisible());
        // assertTrue(properties.getForm(Form.MAIN).getWidget("value").isVisible());
        // // The refreshLayout will change the columnName
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals("EMPTY", properties.function.getValue());
        // assertEquals("==", properties.operator.getValue());
        // assertNull(properties.value.getValue());
        //
        // properties.refreshLayout(properties.getForm(Form.MAIN));
        // assertTrue(properties.getForm(Form.MAIN).getWidget("columnName").isVisible());
        // assertTrue(properties.getForm(Form.MAIN).getWidget("function").isVisible());
        // assertTrue(properties.getForm(Form.MAIN).getWidget("operator").isVisible());
        // assertTrue(properties.getForm(Form.MAIN).getWidget("value").isVisible());
        // // The refreshLayout will change the columnName
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals("EMPTY", properties.function.getValue());
        // assertEquals("==", properties.operator.getValue());
        // assertNull(properties.value.getValue());
    }

    @Test
    public void testSetupLayout() {
        FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("test");

        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(4));
        Widget columnNameWidget = main.getWidget("columnName");
        assertThat(columnNameWidget, notNullValue());
        Widget function = main.getWidget("function");
        assertThat(function, notNullValue());
        Widget operator = main.getWidget("operator");
        assertThat(operator, notNullValue());
        Widget value = main.getWidget("value");
        assertThat(value, notNullValue());
    }

    // testing conditions update specific case

    @Ignore("Need to be able to have a schema in order to provide a column name checking.")
    @Test
    public void testUpdateDefaultConditions() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema stringSchema = registry.getConverter(String.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", stringSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", stringSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // default value, "columName" will change
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition0");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.EMPTY, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.EQUAL, properties.operator.getValue());
        // assertNull(properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.STRING_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

    @Ignore("Need to be able to check the column type in order to provide a function checking.")
    @Test
    public void testUpdateConditions_wrongfunction() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema stringSchema = registry.getConverter(String.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", stringSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", stringSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // specific value, "function" will change cause inputValue1's type is
        // // not a compatible with ConditionsRow.ABS_VALUE
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition1");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.columnName.setValue("inputValue1");
        // properties.function.setValue(ConditionsRowConstant.Function.ABS_VALUE);
        // properties.operator.setValue(ConditionsRowConstant.Operator.NOT_EQUAL);
        // properties.value.setValue("1111");
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue1", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.EMPTY, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.NOT_EQUAL, properties.operator.getValue());
        // assertEquals("1111", properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.STRING_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

    @Ignore("Need to be able to have a schema in order to provide a column name checking.")
    @Test
    public void testUpdateConditions_ok() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema stringSchema = registry.getConverter(String.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", stringSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", stringSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // specific value, will not change
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition2");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.columnName.setValue("inputValue2");
        // properties.function.setValue(ConditionsRowConstant.Function.LOWER_CASE);
        // properties.operator.setValue(ConditionsRowConstant.Operator.EQUAL);
        // properties.value.setValue("2222");
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue2", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.LOWER_CASE, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.EQUAL, properties.operator.getValue());
        // assertEquals("2222", properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.STRING_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

    @Test
    @Ignore("Need to be able to check the column type in order to provide a function checking.")
    public void testUpdateConditions_integerSchema() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema integerSchema = registry.getConverter(Integer.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", integerSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", integerSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // specific value, will change due to type compatibility
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition4");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.columnName.setValue("inputValue2");
        // properties.function.setValue(ConditionsRowConstant.Function.LOWER_CASE);
        // properties.operator.setValue(ConditionsRowConstant.Operator.EQUAL);
        // properties.value.setValue("2222");
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue2", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.EMPTY, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.EQUAL, properties.operator.getValue());
        // assertEquals("2222", properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.NUMERICAL_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

    @Ignore("Need to be able to check the column type in order to provide a function checking.")
    @Test
    public void testUpdateConditions_longSchema() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema longSchema = registry.getConverter(Long.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", longSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", longSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // specific value, will change due to type compatibility
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition4");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.columnName.setValue("inputValue2");
        // properties.function.setValue(ConditionsRowConstant.Function.LOWER_CASE);
        // properties.operator.setValue(ConditionsRowConstant.Operator.EQUAL);
        // properties.value.setValue("2222");
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue2", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.EMPTY, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.EQUAL, properties.operator.getValue());
        // assertEquals("2222", properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.NUMERICAL_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

    @Ignore("Need to be able to check the column type in order to provide a function checking.")
    @Test
    public void testUpdateConditions_floatSchema() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema floatSchema = registry.getConverter(Float.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", floatSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", floatSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // specific value, will change due to type compatibility
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition4");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.columnName.setValue("inputValue2");
        // properties.function.setValue(ConditionsRowConstant.Function.LOWER_CASE);
        // properties.operator.setValue(ConditionsRowConstant.Operator.EQUAL);
        // properties.value.setValue("2222");
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue2", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.EMPTY, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.EQUAL, properties.operator.getValue());
        // assertEquals("2222", properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.NUMERICAL_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

    @Ignore("Need to be able to check the column type in order to provide a function checking.")
    @Test
    public void testUpdateConditions_doubleSchema() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema doubleSchema = registry.getConverter(Double.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", doubleSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", doubleSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // specific value, will change due to type compatibility
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition4");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.columnName.setValue("inputValue2");
        // properties.function.setValue(ConditionsRowConstant.Function.LOWER_CASE);
        // properties.operator.setValue(ConditionsRowConstant.Operator.EQUAL);
        // properties.value.setValue("2222");
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue2", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.EMPTY, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.EQUAL, properties.operator.getValue());
        // assertEquals("2222", properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.NUMERICAL_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

    @Ignore("Need to be able to check the column type in order to provide a function checking.")
    @Test
    public void testUpdateConditions_booleanSchema() {
        // AvroRegistry registry = new AvroRegistry();
        // Schema booleanSchema = registry.getConverter(Boolean.class).getSchema();
        // Schema.Field inputValue1Field = new Schema.Field("inputValue1", booleanSchema, null, null, Order.ASCENDING);
        // Schema.Field inputValue2Field = new Schema.Field("inputValue2", booleanSchema, null, null, Order.ASCENDING);
        // Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
        // Arrays.asList(inputValue1Field, inputValue2Field));
        //
        // // specific value, will change due to type compatibility
        // FilterRowCriteriaProperties properties = new FilterRowCriteriaProperties("condition5");
        // properties.init();
        // properties.main.schema.setValue(inputSchema);
        // properties.updateOutputSchemas();
        //
        // properties.columnName.setValue("inputValue2");
        // properties.function.setValue(ConditionsRowConstant.Function.LOWER_CASE);
        // properties.operator.setValue(ConditionsRowConstant.Operator.EQUAL);
        // properties.value.setValue("2222");
        //
        // properties.updateConditionsRow();
        //
        // assertEquals("inputValue2", properties.columnName.getValue());
        // assertEquals(ConditionsRowConstant.Function.EMPTY, properties.function.getValue());
        // assertEquals(ConditionsRowConstant.Operator.EQUAL, properties.operator.getValue());
        // assertEquals("2222", properties.value.getValue());
        //
        // assertThat((List<String>) properties.columnName.getPossibleValues(), is(Arrays.asList("inputValue1",
        // "inputValue2")));
        // assertThat((List<String>) properties.function.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_FUNCTIONS));
        // assertThat((List<String>) properties.operator.getPossibleValues(),
        // is(ConditionsRowConstant.DEFAULT_OPERATORS));
    }

}
