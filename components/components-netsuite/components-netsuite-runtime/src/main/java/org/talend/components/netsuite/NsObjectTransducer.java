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

package org.talend.components.netsuite;

import static org.talend.components.netsuite.client.model.beans.Beans.getEnumAccessor;
import static org.talend.components.netsuite.client.model.beans.Beans.getProperty;
import static org.talend.components.netsuite.client.model.beans.Beans.getSimpleProperty;
import static org.talend.components.netsuite.client.model.beans.Beans.setSimpleProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.avro.Schema;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.netsuite.avro.converter.EnumToStringConverter;
import org.talend.components.netsuite.avro.converter.ObjectToJsonConverter;
import org.talend.components.netsuite.avro.converter.NullConverter;
import org.talend.components.netsuite.avro.converter.XMLGregorianCalendarToLongConverter;
import org.talend.components.netsuite.client.MetaDataSource;
import org.talend.components.netsuite.client.NetSuiteClientService;
import org.talend.components.netsuite.client.NsRef;
import org.talend.components.netsuite.client.model.CustomFieldDesc;
import org.talend.components.netsuite.client.model.FieldDesc;
import org.talend.components.netsuite.client.model.SimpleFieldDesc;
import org.talend.components.netsuite.client.model.TypeDesc;
import org.talend.components.netsuite.client.model.beans.BeanInfo;
import org.talend.components.netsuite.client.model.beans.Beans;
import org.talend.components.netsuite.client.model.customfield.CustomFieldRefType;
import org.talend.components.netsuite.json.NsTypeResolverBuilder;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.daikon.di.DiSchemaConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 *
 */
public abstract class NsObjectTransducer {

    protected NetSuiteClientService<?> clientService;

    protected MetaDataSource metaDataSource;

    protected final DatatypeFactory datatypeFactory;

    protected final ObjectMapper objectMapper;

    protected Map<Class<?>, AvroConverter<?, ?>> valueConverterCache = new HashMap<>();

    public NsObjectTransducer(NetSuiteClientService<?> clientService) {
        this.clientService = clientService;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ComponentException(e);
        }

        objectMapper = new ObjectMapper();

        objectMapper.setDefaultTyping(new NsTypeResolverBuilder(clientService.getBasicMetaData()));

        // Register JAXB annotation module to perform mapping of data model objects to/from JSON.
        JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule();
        objectMapper.registerModule(jaxbAnnotationModule);

        setMetaDataSource(clientService.getMetaDataSource());
    }

    public MetaDataSource getMetaDataSource() {
        return metaDataSource;
    }

    public void setMetaDataSource(MetaDataSource metaDataSource) {
        this.metaDataSource = metaDataSource;
    }

    public NetSuiteClientService<?> getClientService() {
        return clientService;
    }

    public DatatypeFactory getDatatypeFactory() {
        return datatypeFactory;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected Schema getDynamicSchema(TypeDesc typeDesc, Schema designSchema, String targetSchemaName) {
        Map<String, FieldDesc> fieldMap = typeDesc.getFieldMap();

        String dynamicPosProp = designSchema.getProp(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION);
        List<Schema.Field> fields = new ArrayList<>();

        if (dynamicPosProp != null) {
            Set<String> designFieldNames = new HashSet<>(designSchema.getFields().size());
            for (Schema.Field field : designSchema.getFields()) {
                String fieldName = NetSuiteDatasetRuntimeImpl.getNsFieldName(field);
                designFieldNames.add(fieldName);
            }

            int dynPos = Integer.parseInt(dynamicPosProp);
            int dynamicColumnSize = fieldMap.size() - designSchema.getFields().size();

            List<FieldDesc> dynaFieldDescList = new ArrayList<>(dynamicColumnSize);
            for (FieldDesc fieldDesc : fieldMap.values()) {
                String fieldName = fieldDesc.getName();
                if (!designFieldNames.contains(fieldName)) {
                    dynaFieldDescList.add(fieldDesc);
                }
            }

            if (designSchema.getFields().size() > 0) {
                for (Schema.Field field : designSchema.getFields()) {
                    // Dynamic column is first or middle column in design schema
                    if (dynPos == field.pos()) {
                        for (int i = 0; i < dynamicColumnSize; i++) {
                            // Add dynamic schema fields
                            FieldDesc fieldDesc = dynaFieldDescList.get(i);
                            fields.add(createSchemaField(fieldDesc));
                        }
                    }

                    // Add fields of design schema
                    Schema.Field avroField = new Schema.Field(
                            field.name(), field.schema(), null, field.defaultVal());
                    Map<String, Object> fieldProps = field.getObjectProps();
                    for (String propName : fieldProps.keySet()) {
                        Object propValue = fieldProps.get(propName);
                        if (propValue != null) {
                            avroField.addProp(propName, propValue);
                        }
                    }

                    fields.add(avroField);

                    // Dynamic column is last column in design schema
                    if (field.pos() == (designSchema.getFields().size() - 1) && dynPos == (field.pos() + 1)) {
                        for (int i = 0; i < dynamicColumnSize; i++) {
                            // Add dynamic schema fields
                            FieldDesc fieldDesc = dynaFieldDescList.get(i);
                            fields.add(createSchemaField(fieldDesc));
                        }
                    }
                }
            } else {
                // All fields are included in dynamic schema
                for (String fieldName : fieldMap.keySet()) {
                    FieldDesc fieldDesc = fieldMap.get(fieldName);
                    fields.add(createSchemaField(fieldDesc));
                }
            }
        } else {
            // All fields are included in dynamic schema
            for (String fieldName : fieldMap.keySet()) {
                FieldDesc fieldDesc = fieldMap.get(fieldName);
                fields.add(createSchemaField(fieldDesc));
            }
        }

        Schema schema = Schema.createRecord(targetSchemaName, null, null, false, fields);
        return schema;
    }

    protected Schema.Field createSchemaField(FieldDesc fieldDesc) {
        Schema avroFieldType = NetSuiteDatasetRuntimeImpl.inferSchemaForField(fieldDesc);
        Schema.Field avroField = new Schema.Field(fieldDesc.getName(), avroFieldType, null, (Object) null);
        return avroField;
    }

    protected Map<String, Object> getMapView(Object nsObject, Schema schema, TypeDesc typeDesc) {
        Map<String, Object> valueMap = new HashMap<>();

        BeanInfo beanInfo = Beans.getBeanInfo(typeDesc.getTypeClass());
        Map<String, FieldDesc> fieldMap = typeDesc.getFieldMap();

        Map<String, CustomFieldDesc> customFieldMap = new HashMap<>();
        for (Schema.Field field : schema.getFields()) {
            String nsFieldName = NetSuiteDatasetRuntimeImpl.getNsFieldName(field);
            FieldDesc fieldDesc = fieldMap.get(nsFieldName);

            if (fieldDesc == null) {
                continue;
            }

            if (fieldDesc instanceof CustomFieldDesc) {
                customFieldMap.put(nsFieldName, (CustomFieldDesc) fieldDesc);
            } else {
                Object value = getSimpleProperty(nsObject, fieldDesc.getName());
                valueMap.put(nsFieldName, value);
            }
        }

        if (!customFieldMap.isEmpty() &&
                beanInfo.getProperty("customFieldList") != null) {
            List<?> customFieldList = (List<?>) getProperty(nsObject, "customFieldList.customField");
            if (customFieldList != null && !customFieldList.isEmpty()) {
                for (Object customField : customFieldList) {
                    String scriptId = (String) getSimpleProperty(customField, "scriptId");
                    CustomFieldDesc customFieldInfo = customFieldMap.get(scriptId);
                    String fieldName = customFieldInfo.getName();
                    if (customFieldInfo != null) {
                        valueMap.put(fieldName, customField);
                    }
                }
            }
        }

        return valueMap;
    }

    /**
     * Read a value from a field.
     *
     * @param valueMap map containing raw values by names
     * @param fieldDesc field descriptor
     * @return value of a field or <code>null</code>
     */
    protected Object readField(Map<String, Object> valueMap, FieldDesc fieldDesc) {
        String fieldName = fieldDesc.getName();
        AvroConverter valueConverter = getValueConverter(fieldDesc);
        if (fieldDesc instanceof CustomFieldDesc) {
            Object customField = valueMap.get(fieldName);
            if (customField != null) {
                Object value = getSimpleProperty(customField, "value");
                return valueConverter.convertToAvro(value);
            }
            return null;
        } else {
            Object value = valueMap.get(fieldName);
            return valueConverter.convertToAvro(value);
        }
    }

    /**
     * Write a value to a field.
     *
     * @param nsObject target NetSuite data model object which to write field value to
     * @param fieldDesc field descriptor
     * @param customFieldMap map of native custom field objects by names
     * @param nullFieldNames collection to register null'ed fields
     * @param value value to be written, can be <code>null</code>
     */
    protected void writeField(Object nsObject, FieldDesc fieldDesc, Map<String, Object> customFieldMap,
            Collection<String> nullFieldNames, Object value) {
        writeField(nsObject, fieldDesc, customFieldMap, true, nullFieldNames, value);
    }

    /**
     * Write a value to a field.
     *
     * @param nsObject target NetSuite data model object which to write field value to
     * @param fieldDesc field descriptor
     * @param customFieldMap map of native custom field objects by names
     * @param replace specifies whether to forcibly replace a field's value
     * @param nullFieldNames collection to register null'ed fields
     * @param value value to be written, can be <code>null</code>
     */
    protected void writeField(Object nsObject, FieldDesc fieldDesc, Map<String, Object> customFieldMap,
            boolean replace, Collection<String> nullFieldNames, Object value) {
        if (fieldDesc instanceof CustomFieldDesc) {
            writeCustomField(nsObject, fieldDesc.asCustom(), customFieldMap, replace, nullFieldNames, value);
        } else {
            writeSimpleField(nsObject, fieldDesc.asSimple(), replace, nullFieldNames, value);
        }
    }

    /**
     * Write a custom field which is not defined by NetSuite standard data model.
     *
     * @param nsObject target NetSuite data model object which to write field value to
     * @param fieldDesc field descriptor
     * @param customFieldMap map of native custom field objects by names
     * @param replace specifies whether to forcibly replace a field's value
     * @param nullFieldNames collection to register null'ed fields
     * @param value value to be written, can be <code>null</code>
     */
    protected void writeCustomField(Object nsObject, CustomFieldDesc fieldDesc, Map<String, Object> customFieldMap,
            boolean replace, Collection<String> nullFieldNames, Object value) {

        NsRef ref = fieldDesc.getRef();
        CustomFieldRefType customFieldRefType = fieldDesc.getCustomFieldType();

        Object customFieldListWrapper = getSimpleProperty(nsObject, "customFieldList");
        if (customFieldListWrapper == null) {
            customFieldListWrapper = clientService.getBasicMetaData().createInstance("CustomFieldList");
            setSimpleProperty(nsObject, "customFieldList", customFieldListWrapper);
        }
        List<Object> customFieldList = (List<Object>) getSimpleProperty(customFieldListWrapper, "customField");

        Object customField = customFieldMap.get(ref.getScriptId());
        AvroConverter valueConverter = getValueConverter(fieldDesc);

        Object targetValue = valueConverter.convertToDatum(value);

        if (targetValue == null) {
            if (replace && customField != null && customFieldList != null) {
                customFieldList.remove(customField);
                nullFieldNames.add(fieldDesc.getName());
            }
        } else {
            if (customField == null) {
                customField = clientService.getBasicMetaData().createInstance(customFieldRefType.getTypeName());

                setSimpleProperty(customField, "scriptId", ref.getScriptId());
                setSimpleProperty(customField, "internalId", ref.getInternalId());

                customFieldList.add(customField);
                customFieldMap.put(ref.getScriptId(), customField);
            }

            setSimpleProperty(customField, "value", targetValue);
        }
    }

    /**
     * Write a value to a simple field which is defined by NetSuite standard data model.
     *
     * @param nsObject target NetSuite data model object which to write field value to
     * @param fieldDesc field descriptor
     * @param replace specifies whether to forcibly replace a field's value
     * @param nullFieldNames collection to register null'ed fields
     * @param value value to be written, can be <code>null</code>
     */
    protected void writeSimpleField(Object nsObject, SimpleFieldDesc fieldDesc,
            boolean replace, Collection<String> nullFieldNames, Object value) {

        AvroConverter valueConverter = getValueConverter(fieldDesc);

        Object targetValue = valueConverter.convertToDatum(value);

        if (targetValue == null) {
            if (replace) {
                setSimpleProperty(nsObject, fieldDesc.getPropertyName(), null);
                nullFieldNames.add(fieldDesc.getName());
            }
        } else {
            setSimpleProperty(nsObject, fieldDesc.getPropertyName(), targetValue);
        }
    }

    protected Class<?> getCustomFieldValueConverterTargetClass(CustomFieldRefType customFieldRefType) {
        Class<?> valueClass;
        switch (customFieldRefType) {
        case BOOLEAN:
            valueClass = Boolean.class;
            break;
        case STRING:
            valueClass = String.class;
            break;
        case LONG:
            valueClass = Long.class;
            break;
        case DOUBLE:
            valueClass = Double.class;
            break;
        case DATE:
            valueClass = XMLGregorianCalendar.class;
            break;
        case SELECT:
        case MULTI_SELECT:
        default:
            valueClass = null;
            break;
        }
        return valueClass;
    }

    public AvroConverter<?, ?> getValueConverter(FieldDesc fieldDesc) {
        Class<?> valueClass = null;
        if (fieldDesc instanceof CustomFieldDesc) {
            CustomFieldDesc customFieldDesc = (CustomFieldDesc) fieldDesc;
            CustomFieldRefType customFieldRefType = customFieldDesc.getCustomFieldType();
            valueClass = getCustomFieldValueConverterTargetClass(customFieldRefType);
        } else {
            valueClass = fieldDesc.getValueType();
        }

        AvroConverter<?, ?> converter = null;
        if (valueClass != null) {
            converter = getValueConverter(valueClass);
        }
        if (converter == null) {
            converter = new NullConverter(valueClass, null);
        }
        return converter;
    }

    public AvroConverter<?, ?> getValueConverter(Class<?> valueClass) {
        AvroConverter<?, ?> converter = valueConverterCache.get(valueClass);
        if (converter == null) {
            converter = createValueConverter(valueClass);
            if (converter != null) {
                valueConverterCache.put(valueClass, converter);
            }
        }
        return converter;
    }

    protected AvroConverter<?, ?> createValueConverter(Class<?> valueClass) {
        if (valueClass == Boolean.TYPE || valueClass == Boolean.class ||
                valueClass == Integer.TYPE || valueClass == Integer.class ||
                valueClass == Long.TYPE || valueClass == Long.class ||
                valueClass == Double.TYPE || valueClass == Double.class ||
                valueClass == String.class) {
            return new AvroRegistry.Unconverted<>(valueClass, null);
        } else if (valueClass == XMLGregorianCalendar.class) {
            return new XMLGregorianCalendarToLongConverter(datatypeFactory);
        } else if (valueClass.isEnum()) {
            Class<Enum> enumClass = (Class<Enum>) valueClass;
            return new EnumToStringConverter<>(enumClass, getEnumAccessor(enumClass));
        } else if (!valueClass.isPrimitive()) {
            return new ObjectToJsonConverter<>(valueClass, objectMapper);
        }
        return null;
    }

}