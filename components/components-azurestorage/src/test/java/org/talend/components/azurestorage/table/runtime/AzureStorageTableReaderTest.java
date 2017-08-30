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
package org.talend.components.azurestorage.table.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.*;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.talend.components.api.test.runtime.reader.SourceReaderTest;
import org.talend.components.azurestorage.AzureBaseTest;
import org.talend.components.azurestorage.table.AzureStorageTableService;
import org.talend.components.azurestorage.table.helpers.Comparison;
import org.talend.components.azurestorage.table.helpers.Predicate;
import org.talend.components.azurestorage.table.helpers.SupportedFieldType;
import org.talend.components.azurestorage.table.tazurestorageinputtable.TAzureStorageInputTableProperties;
import org.talend.daikon.properties.ValidationResult;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.TableQuery;

public class AzureStorageTableReaderTest extends AzureBaseTest implements SourceReaderTest {

    private TAzureStorageInputTableProperties properties;

    private AzureStorageTableSource source;

    @Mock
    private AzureStorageTableService tableService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {

        properties = new TAzureStorageInputTableProperties("InputTable");
        properties.setupProperties();
        properties.connection = getValidFakeConnection();
        properties.tableName.setValue("testTable");

        properties.filterExpression.column.setValue(new ArrayList<String>());
        properties.filterExpression.fieldType.setValue(new ArrayList<String>());
        properties.filterExpression.function.setValue(new ArrayList<String>());
        properties.filterExpression.operand.setValue(new ArrayList<String>());
        properties.filterExpression.predicate.setValue(new ArrayList<String>());

        properties.filterExpression.column.getValue().add("PartitionKey");
        properties.filterExpression.fieldType.getValue().add(SupportedFieldType.STRING.name());
        properties.filterExpression.function.getValue().add(Comparison.EQUAL.name());
        properties.filterExpression.operand.getValue().add("Departement");
        properties.filterExpression.predicate.getValue().add(Predicate.AND.name());

        source = new AzureStorageTableSource();

    }

    /**
     * Test the reader behavior when the data source is empty
     */
    @Test
    @Override
    public void testReadSourceEmpty() {

        // setup
        properties.dieOnError.setValue(false);

        assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
        AzureStorageTableReader reader = (AzureStorageTableReader) source.createReader(getDummyRuntimeContiner());

        // mock
        final List<DynamicTableEntity> records = new ArrayList<>();
        try {
            reader.tableService = tableService;
            when(tableService.executeQuery(anyString(), any(TableQuery.class))).thenReturn(new Iterable<DynamicTableEntity>() {

                @Override
                public Iterator<DynamicTableEntity> iterator() {
                    return records.iterator();
                }
            });

            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    /**
     * Test the reader behavior when the data source contains only one element
     */
    @Override
    public void testReadSourceWithOnly1Element() {

        // setup
        properties.dieOnError.setValue(false);

        assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
        AzureStorageTableReader reader = (AzureStorageTableReader) source.createReader(getDummyRuntimeContiner());

        // mock
        final List<DynamicTableEntity> records = new ArrayList<>();
        records.add(new DynamicTableEntity("Departement", "1"));
        try {
            reader.tableService = tableService;
            when(tableService.executeQuery(anyString(), any(TableQuery.class))).thenReturn(new Iterable<DynamicTableEntity>() {

                @Override
                public Iterator<DynamicTableEntity> iterator() {
                    return records.iterator();
                }
            });

            assertThat(reader, canStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    /**
     * Test the reader behavior when the data source contains many elements
     */
    @Override
    @Test
    public void testReadSourceWithManyElements() {

        // setup
        properties.dieOnError.setValue(false);

        assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
        AzureStorageTableReader reader = (AzureStorageTableReader) source.createReader(getDummyRuntimeContiner());

        // mock
        final List<DynamicTableEntity> records = new ArrayList<>();
        records.add(new DynamicTableEntity("Departement", "1"));
        records.add(new DynamicTableEntity("Departement", "2"));
        records.add(new DynamicTableEntity("Departement", "3"));
        try {
            reader.tableService = tableService;
            when(tableService.executeQuery(anyString(), any(TableQuery.class))).thenReturn(new Iterable<DynamicTableEntity>() {

                @Override
                public Iterator<DynamicTableEntity> iterator() {
                    return records.iterator();
                }
            });

            assertThat(reader, canStart());
            assertThat(reader, canAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    /**
     * Test the reader behavior when the data source is unavailable reader stop
     */
    @Test
    @Override
    public void testReadSourceUnavailableDieOnError() {

        // setup
        properties.dieOnError.setValue(true);

        assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
        AzureStorageTableReader reader = (AzureStorageTableReader) source.createReader(getDummyRuntimeContiner());

        // mock
        try {
            reader.tableService = tableService;
            when(tableService.executeQuery(anyString(), any(TableQuery.class)))
                    .thenThrow(new StorageException("500", "Storage unavailable", new RuntimeException("")));

            assertThat(reader, startAndDieOnError());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    /**
     * Test the reader behavior when the data source is unavailable reader handle error
     */
    @Override
    @Test
    public void testReadSourceUnavailableHandleError() {

        properties.dieOnError.setValue(false);

        assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
        AzureStorageTableReader reader = (AzureStorageTableReader) source.createReader(getDummyRuntimeContiner());

        try {
            reader.tableService = tableService;
            when(tableService.executeQuery(anyString(), any(TableQuery.class)))
                    .thenThrow(new StorageException("500", "Storage unavailable", new RuntimeException("")));

            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }
    }

    /**
     * Test reader close
     */
    @Override
    @Test
    public void testClose() {

    }

}
