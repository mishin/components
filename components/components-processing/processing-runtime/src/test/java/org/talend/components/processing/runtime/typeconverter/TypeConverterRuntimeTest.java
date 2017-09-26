package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.junit.Assert;
import org.junit.Test;
import org.talend.components.processing.definition.filterrow.ConditionsRowConstant;
import org.talend.components.processing.definition.filterrow.FilterRowCriteriaProperties;
import org.talend.components.processing.definition.filterrow.LogicalOpType;
import org.talend.components.processing.definition.typeconverter.TypeConverterProperties;

import java.util.List;

public class TypeConverterRuntimeTest {

    @Test
    public void testDoFn() throws Exception {
        Schema inputSchema1 = SchemaBuilder.record("inputSchema1") //
                .fields() //
                .name("stringToInt").type().optional().stringType() //
                .name("stringToBool").type().optional().booleanType() //
                //.name("b").type(SchemaBuilder.record("nestedSchema").fields().name("b1").type().bytesType().noDefault().endRecord()).noDefault()
                .endRecord();

        GenericRecordBuilder recordBuilder1 = new GenericRecordBuilder(inputSchema1);
        recordBuilder1.set("stringToInt", "1");
        recordBuilder1.set("stringToBool", "false");
        TypeConverterProperties properties = new TypeConverterProperties("test");

        TypeConverterProperties.TypeConverterPropertiesInner converter1 = new TypeConverterProperties.TypeConverterPropertiesInner("converter1");
        converter1.init();
        converter1.field.setValue("stringToInt");
        converter1.outputType.setValue(TypeConverterProperties.TypeConverterOutputTypes.Integer);
        properties.converters.addRow(converter1);

        TypeConverterProperties.TypeConverterPropertiesInner converter2 = new TypeConverterProperties.TypeConverterPropertiesInner("converter2");
        converter2.init();
        converter2.field.setValue("stringToBool");
        converter2.outputType.setValue(TypeConverterProperties.TypeConverterOutputTypes.Boolean);
        properties.converters.addRow(converter2);

        TypeConverterRuntime runtime = new TypeConverterRuntime().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(runtime);

        List<IndexedRecord> outputs = fnTester.processBundle(recordBuilder1.build());

        Assert.assertEquals(Integer.class, outputs.get(0).get(0).getClass());
        Assert.assertEquals(Boolean.class, outputs.get(0).get(1).getClass());
        Assert.assertFalse((Boolean) outputs.get(0).get(1));
    }
}