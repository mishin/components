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
package org.talend.components.azurestorage.blob.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.test.runtime.reader.SourceReaderTest;
import org.talend.components.azurestorage.RuntimeContainerMock;
import org.talend.components.azurestorage.blob.AzureStorageBlobService;
import org.talend.components.azurestorage.blob.helpers.RemoteBlobsTable;
import org.talend.components.azurestorage.blob.tazurestoragelist.TAzureStorageListProperties;
import org.talend.components.azurestorage.tazurestorageconnection.TAzureStorageConnectionProperties;
import org.talend.components.azurestorage.tazurestorageconnection.TAzureStorageConnectionProperties.Protocol;
import org.talend.daikon.properties.ValidationResult;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class AzureStorageListReaderTest implements SourceReaderTest {

    private TAzureStorageListProperties properties;

    public static final String PROP_ = "PROP_";

    private AzureStorageSource source;

    private RuntimeContainer runtimeContainer;

    @Mock
    private AzureStorageBlobService blobService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {

        runtimeContainer = new RuntimeContainerMock();
        source = new AzureStorageSource();

        properties = new TAzureStorageListProperties(PROP_ + "List");
        properties.setupProperties();
        properties.connection = new TAzureStorageConnectionProperties(PROP_ + "Connection");
        properties.connection.protocol.setValue(Protocol.HTTP);
        properties.connection.accountName.setValue("fakeAccountName");
        properties.connection.accountKey.setValue("fakeAccountKey=ANBHFYRJJFHRIKKJFU");
        properties.remoteBlobs = new RemoteBlobsTable("RemoteBlobsTable");
        properties.remoteBlobs.include.setValue(Arrays.asList(true));
        properties.remoteBlobs.prefix.setValue(Arrays.asList("someFilter"));


    }

    @Test
    @Override
    public void testReadSourceEmpty() {

        assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
        AzureStorageListReader reader = (AzureStorageListReader) source.createReader(runtimeContainer);

        try {

            when(blobService.listBlobs(anyString(), anyString(), anyBoolean())).thenReturn(new Iterable<ListBlobItem>() {

                @Override
                public Iterator<ListBlobItem> iterator() {
                    return new DummyListBlobItemIterator(new ArrayList<CloudBlockBlob>());
                }
            });
            reader.azureStorageBlobService = blobService;

            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceWithOnly1Element() {

        assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
        AzureStorageListReader reader = (AzureStorageListReader) source.createReader(runtimeContainer);

        try {
            final List<CloudBlockBlob> list = new ArrayList<>();
            list.add(new CloudBlockBlob(new URI("https://storagesample.blob.core.windows.net/mycontainer/blob1.txt")));
            when(blobService.listBlobs(anyString(), anyString(), anyBoolean())).thenReturn(new Iterable<ListBlobItem>() {

                @Override
                public Iterator<ListBlobItem> iterator() {
                    return new DummyListBlobItemIterator(list);
                }
            });
            reader.azureStorageBlobService = blobService;

            assertThat(reader, canStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceWithManyElements() {

        assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
        AzureStorageListReader reader = (AzureStorageListReader) source.createReader(runtimeContainer);

        try {
            final List<CloudBlockBlob> list = new ArrayList<>();
            list.add(new CloudBlockBlob(new URI("https://storagesample.blob.core.windows.net/mycontainer/blob1.txt")));
            list.add(new CloudBlockBlob(new URI("https://storagesample.blob.core.windows.net/mycontainer/blob2.txt")));
            list.add(new CloudBlockBlob(new URI("https://storagesample.blob.core.windows.net/mycontainer/blob3.txt")));
            when(blobService.listBlobs(anyString(), anyString(), anyBoolean())).thenReturn(new Iterable<ListBlobItem>() {

                @Override
                public Iterator<ListBlobItem> iterator() {
                    return new DummyListBlobItemIterator(list);
                }
            });
            reader.azureStorageBlobService = blobService;

            assertThat(reader, canStart());
            assertThat(reader, canAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }
    }

    @Test
    @Override
    public void testReadSourceUnavailableDieOnError() {

        properties.dieOnError.setValue(true);

        assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
        AzureStorageListReader reader = (AzureStorageListReader) source.createReader(runtimeContainer);

        try {
            when(blobService.listBlobs(anyString(), anyString(), anyBoolean()))
                    .thenThrow(new StorageException("500", "Unvailable source", new RuntimeException("Unvailable source")));
            reader.azureStorageBlobService = blobService;

            assertThat(reader, startAndDieOnError());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceUnavailableHandleError() {

        properties.dieOnError.setValue(false);

        assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
        AzureStorageListReader reader = (AzureStorageListReader) source.createReader(runtimeContainer);

        try {
            when(blobService.listBlobs(anyString(), anyString(), anyBoolean()))
                    .thenThrow(new StorageException("500", "Unvailable source", new RuntimeException("Unvailable source")));
            reader.azureStorageBlobService = blobService;

            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Override
    public void testClose() {
        //
    }

}
