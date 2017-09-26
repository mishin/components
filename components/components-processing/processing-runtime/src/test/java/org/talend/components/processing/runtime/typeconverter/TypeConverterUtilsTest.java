package org.talend.components.processing.runtime.typeconverter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.talend.components.processing.definition.typeconverter.TypeConverterProperties;
import org.talend.components.processing.runtime.normalize.NormalizeUtilsTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class TypeConverterUtilsTest {

    private final Schema inputSchemaL = SchemaBuilder.record("inputRowL") //
            .fields() //
            .name("l").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaListOfL = SchemaBuilder.array().items(inputSchemaL);

    private final Schema inputSchemaJK = SchemaBuilder.record("inputRowJK") //
            .fields() //
            .name("j").type(inputSchemaListOfL).noDefault() //
            .name("k").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaDE = SchemaBuilder.record("inputRowDE") //
            .fields() //
            .name("d").type(inputSchemaJK).noDefault() //
            .name("e").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaHI = SchemaBuilder.record("inputRowHI") //
            .fields() //
            .name("h").type().optional().stringType() //
            .name("i").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaListOfHI = SchemaBuilder.array().items(inputSchemaHI);

    private final Schema inputSchemaFG = SchemaBuilder.record("inputRowFG") //
            .fields() //
            .name("f").type().optional().stringType() //
            .name("g").type(inputSchemaListOfHI).noDefault() //
            .endRecord();

    private final Schema inputSchemaXY = SchemaBuilder.record("inputRowXY") //
            .fields() //
            .name("x").type().stringType().noDefault() //
            .name("y").type(inputSchemaDE).noDefault() //
            .endRecord();

    private final Schema inputSchemaListM = SchemaBuilder.array().items().stringType();

    private final Schema inputParentSchema = SchemaBuilder.record("inputParentRow") //
            .fields() //
            .name("a").type().stringType().noDefault() //
            .name("b").type(inputSchemaXY).noDefault() //
            .name("c").type(inputSchemaFG).noDefault() //
            .name("m").type(inputSchemaListM).noDefault() //
            .endRecord();

    /**
     * {"l":"l1"}
     */
    private final GenericRecord inputRecordL1 = new GenericRecordBuilder(inputSchemaL) //
            .set("l", "l1") //
            .build();

    /**
     * {"l":"l2"}
     */
    private final GenericRecord inputRecordL2 = new GenericRecordBuilder(inputSchemaL) //
            .set("l", "l2") //
            .build();

    /**
     * [{"l":"l1"},{"l":"l2"}]
     */
    private final List<GenericRecord> listInputRecordL = Arrays.asList(inputRecordL1, inputRecordL2);

    /**
     * {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}
     */
    private final GenericRecord inputRecordJK = new GenericRecordBuilder(inputSchemaJK) //
            .set("j", listInputRecordL) //
            .set("k", "k1;k2") //
            .build();

    /**
     * {"d": {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}, "e": "e"}
     */
    private final GenericRecord inputRecordDE = new GenericRecordBuilder(inputSchemaDE) //
            .set("d", inputRecordJK) //
            .set("e", "e") //
            .build();

    /**
     * {"h": "h1", "i": "i2"}
     */
    private final GenericRecord inputRecordHI1 = new GenericRecordBuilder(inputSchemaHI) //
            .set("h", "h1") //
            .set("i", "i2") //
            .build();

    /**
     * {"h": "h2", "i": "i1"}
     */
    private final GenericRecord inputRecordHI2 = new GenericRecordBuilder(inputSchemaHI) //
            .set("h", "h2") //
            .set("i", "i1") //
            .build();

    /**
     * [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]
     */
    private final List<GenericRecord> listInputRecordG = Arrays.asList(inputRecordHI1, inputRecordHI2);

    /**
     * {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}
     */
    private final GenericRecord inputRecordFG = new GenericRecordBuilder(inputSchemaFG) //
            .set("f", "f") //
            .set("g", listInputRecordG) // inputRecordHI
            .build();

    /**
     * {"x": "x1;x2", "y": {"d": {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}, "e": "e"}}
     */
    private final GenericRecord inputRecordXY = new GenericRecordBuilder(inputSchemaXY) //
            .set("x", "x1;x2") //
            .set("y", inputRecordDE) // listDE
            .build();

    /**
     * ["m1", "m2", "m3"]
     */
    private final List<String> listInputRecordM = Arrays.asList("m1", "m2", "m3");

    /**
     * { "a": "0", "b": {"x": "x1;x2", "y": {"d": {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}, "e": "e"}}, "c": {"f":
     * "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}, "m": ["m1", "m2", "m3"]}
     */
    private final GenericRecord inputParentRecord = new GenericRecordBuilder(inputParentSchema) //
            .set("a", "0") //
            .set("b", inputRecordXY) //
            .set("c", inputRecordFG) //
            .set("m", listInputRecordM) //
            .build();

    private final Schema listSchemas = SchemaBuilder.array().items(inputSchemaListM);

    private final List<List<String>> listRecords = Arrays.asList(listInputRecordM);

    private final Schema listOfListSchema = SchemaBuilder.record("listOfListRow") //
            .fields() //
            .name("parentList").type(listSchemas).noDefault() //
            .endRecord();

    private final GenericRecord listOfListRecord = new GenericRecordBuilder(listOfListSchema) //
            .set("parentList", listRecords) //
            .build();

    /**
     * Input schema: {@link NormalizeUtilsTest#inputParentSchema}
     * <p>
     * The field `a` is a string.
     * <p>
     * Expected schema: the schema of the field `a` should be modified to an int
     */
    @Test
    public void testTransformSchema() {

        // String to int
        String[] path1 = {"a"};
        Stack<String> stackPath1 = new Stack<String>();
        stackPath1.addAll(Arrays.asList(path1));
        Schema newSchema1 = TypeConverterUtils.convertSchema(inputParentSchema, stackPath1, TypeConverterProperties.TypeConverterOutputTypes.Integer);

        Schema expectedParentSchema1 = SchemaBuilder.record("inputParentRow") //
                .fields() //
                .name("a").type().intType().noDefault() //
                .name("b").type(inputSchemaXY).noDefault() //
                .name("c").type(inputSchemaFG).noDefault() //
                .name("m").type(inputSchemaListM).noDefault() //
                .endRecord();

        Assert.assertEquals(expectedParentSchema1.toString(), newSchema1.toString());

        // String to float
        String[] path2 = {"b", "x"};
        Stack<String> stackPath2 = new Stack<String>();
        List<String> pathSteps2 = Arrays.asList(path2);
        Collections.reverse(pathSteps2);
        stackPath2.addAll(pathSteps2);
        Schema newSchema2 = TypeConverterUtils.convertSchema(inputParentSchema, stackPath2, TypeConverterProperties.TypeConverterOutputTypes.Float);

        Schema expectedSchemaXY = SchemaBuilder.record("inputRowXY") //
                .fields() //
                .name("x").type().floatType().noDefault() //
                .name("y").type(inputSchemaDE).noDefault() //
                .endRecord();

        Schema expectedParentSchema2 = SchemaBuilder.record("inputParentRow") //
                .fields() //
                .name("a").type().stringType().noDefault() //
                .name("b").type(expectedSchemaXY).noDefault() //
                .name("c").type(inputSchemaFG).noDefault() //
                .name("m").type(inputSchemaListM).noDefault() //
                .endRecord();

        Assert.assertEquals(expectedParentSchema2.toString(), newSchema2.toString());
    }

    @Test
    public void testCopyFieldsValues(){
        Schema intSchema = SchemaBuilder.record("intSchema")
                .fields()
                .name("a").type().intType().noDefault()
                .endRecord();
        GenericRecord intRecord = new GenericRecordBuilder(intSchema)
                .set("a", 1)
                .build();

        Schema stringSchema = SchemaBuilder.record("intSchema")
                .fields()
                .name("a").type().stringType().noDefault()
                .endRecord();
        GenericRecordBuilder stringRecordBuilder = new GenericRecordBuilder(stringSchema)
                .set("a", "s")
                ;
        TypeConverterUtils.copyFieldsValues(intRecord,stringRecordBuilder);
        GenericRecord stringRecord = stringRecordBuilder.build();
        Assert.assertEquals(intRecord.get("a"), stringRecord.get("a"));
    }

    @Test
    public void testConvertValue(){
        GenericRecordBuilder outputRecordBuilder = new GenericRecordBuilder(inputSchemaL) //
                .set("l", "false");
        String outputFormat = null;
        TypeConverterProperties.TypeConverterOutputTypes outputType = TypeConverterProperties.TypeConverterOutputTypes.Boolean;
        Stack<String> converterPath = new Stack<String>();
        converterPath.add("l");
        TypeConverterUtils.convertValue(outputRecordBuilder, converterPath, outputType, outputFormat);
        GenericRecord outputRecord = outputRecordBuilder.build();
        Assert.assertEquals(Boolean.class, outputRecord.get(0).getClass());
    }

    @Test
    public void testGetPathSteps(){
        String pathSteps = ".a.b";
        Stack<String> result = TypeConverterUtils.getPathSteps(pathSteps);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("a", result.pop());
    }
}
