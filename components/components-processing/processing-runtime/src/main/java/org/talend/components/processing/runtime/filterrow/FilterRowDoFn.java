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
package org.talend.components.processing.runtime.filterrow;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.processing.definition.filterrow.ConditionsRowConstant;
import org.talend.components.processing.definition.filterrow.FilterRowProperties;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import scala.collection.JavaConversions;
import scala.util.Try;
import wandou.avpath.Evaluator.Ctx;

public class FilterRowDoFn extends DoFn<Object, IndexedRecord> {

    private FilterRowProperties properties = null;

    private Boolean hasOutputSchema = false;

    private Boolean hasRejectSchema = false;

    private IndexedRecordConverter converter = null;

    @Setup
    public void setup() throws Exception {
    }

    @ProcessElement
    public void processElement(ProcessContext context) {
        if (converter == null) {
            AvroRegistry registry = new AvroRegistry();
            converter = registry.createIndexedRecordConverter(context.element().getClass());
        }
        IndexedRecord inputRecord = (IndexedRecord) converter.convertToAvro(context.element());

        boolean returnedBooleanValue = true;
        String columnName = properties.columnName.getValue();

        // If there is no defined input, we filter nothing
        if (!StringUtils.isEmpty(columnName)) {
            List<Object> inputValues = getInputFields(inputRecord, columnName);
            if (inputValues.size() == 0) {
                // no valid field: reject the input
                returnedBooleanValue = false;
            }

            // TODO handle null with multiples values
            for (Object inputValue : inputValues) {
                returnedBooleanValue = returnedBooleanValue && checkCondition(inputValue, properties);
            }
        }

        if (returnedBooleanValue) {
            if (hasOutputSchema) {
                context.output(inputRecord);
            }
        } else {
            if (hasRejectSchema) {
                context.output(FilterRowRuntime.rejectOutput, inputRecord);
            }
        }
    }

    private <T extends Comparable<T>> Boolean checkCondition(Object inputValue, FilterRowProperties condition) {
        String function = condition.function.getValue();
        String conditionOperator = condition.operator.getValue();
        String referenceValue = condition.value.getValue();

        // Apply the transformation function on the input value
        inputValue = FilterRowUtils.applyFunction(inputValue, function);

        if (referenceValue != null) {
            // TODO: do not cast the reference value at each comparison
            Class<T> inputValueClass = TypeConverterUtils.getComparableClass(inputValue);
            if (inputValueClass != null) {
                T convertedReferenceValue = TypeConverterUtils.parseTo(referenceValue, inputValueClass);
                return FilterRowUtils.compare(inputValueClass.cast(inputValue), conditionOperator, convertedReferenceValue);
            } else {
                return FilterRowUtils.compare(inputValue.toString(), conditionOperator, referenceValue.toString());
            }
        } else {
            if (ConditionsRowConstant.Operator.EQUAL.equals(conditionOperator)) {
                return inputValue == null;
            } else { // Not Equals
                return inputValue != null;
            }
        }
    }

    private List<Object> getInputFields(IndexedRecord inputRecord, String columnName) {
        // Adapt non-avpath syntax to avpath.
        // TODO: This should probably not be automatic, use the actual syntax.
        if (!columnName.startsWith("."))
            columnName = "." + columnName;
        Try<scala.collection.immutable.List<Ctx>> result =  wandou.avpath.package$.MODULE$.select(inputRecord, columnName);
        List<Object> values = new ArrayList<Object>();
        if (result.isSuccess()) {
            for (Ctx ctx : JavaConversions.asJavaCollection(result.get())) {
                values.add(ctx.value());
            }
        } else
            throw TalendRuntimeException.createUnexpectedException(result.failed().get());
        return values;
    }

    public FilterRowDoFn withOutputSchema(boolean hasSchema) {
        hasOutputSchema = hasSchema;
        return this;
    }

    public FilterRowDoFn withRejectSchema(boolean hasSchema) {
        hasRejectSchema = hasSchema;
        return this;
    }

    public FilterRowDoFn withProperties(FilterRowProperties properties) {
        this.properties = properties;
        return this;
    }
}
