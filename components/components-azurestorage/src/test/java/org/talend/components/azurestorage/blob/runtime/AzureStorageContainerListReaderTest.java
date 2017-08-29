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
import static org.mockito.Mockito.when;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.canAdvance;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.canStart;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.cannotAdvance;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.cannotStart;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.startAndDieOnError;

import java.net.URI;
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
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.test.runtime.reader.SourceReaderTest;
import org.talend.components.azurestorage.RuntimeContainerMock;
import org.talend.components.azurestorage.blob.AzureStorageBlobService;
import org.talend.components.azurestorage.blob.tazurestoragecontainerlist.TAzureStorageContainerListProperties;
import org.talend.components.azurestorage.tazurestorageconnection.TAzureStorageConnectionProperties;
import org.talend.components.azurestorage.tazurestorageconnection.TAzureStorageConnectionProperties.Protocol;
import org.talend.daikon.properties.ValidationResult;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

public class AzureStorageContainerListReaderTest implements SourceReaderTest {

    public static final String PROP_ = "PROP_";

    private RuntimeContainer runtimeContainer;

    private TAzureStorageContainerListProperties properties;

    private AzureStorageSource source;

    @Mock
    private AzureStorageBlobService blobService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {

        properties = new TAzureStorageContainerListProperties(PROP_ + "ContainerList");
        properties.connection = new TAzureStorageConnectionProperties(PROP_ + "Connection");
        properties.connection.protocol.setValue(Protocol.HTTP);
        properties.connection.accountName.setValue("fakeAccountName");
        properties.connection.accountKey.setValue("fakeAccountKey=ANBHFYRJJFHRIKKJFU");
        properties.setupProperties();

        runtimeContainer = new RuntimeContainerMock();
        source = new AzureStorageSource();
    }

    @Test
    @Override
    public void testReadSourceEmpty() {
        assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
        AzureStorageContainerListReader reader = (AzureStorageContainerListReader) source.createReader(runtimeContainer);

        // init mock
        try {
            when(blobService.listContainers()).thenReturn(new Iterable<CloudBlobContainer>() {

                @Override
                public Iterator<CloudBlobContainer> iterator() {
                    return new DummyCloudBlobContainerIterator(new ArrayList<CloudBlobContainer>());
                }
            });
            reader.blobService = blobService;

            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceWithOnly1Element() {

        assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
        assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
        AzureStorageContainerListReader reader = (AzureStorageContainerListReader) source.createReader(runtimeContainer);

        try {
            final List<CloudBlobContainer> list = new ArrayList<>();
            list.add(new CloudBlobContainer(new URI("https://fakeAccountName.blob.core.windows.net/container-1")));

            when(blobService.listContainers()).thenReturn(new Iterable<CloudBlobContainer>() {

                @Override
                public Iterator<CloudBlobContainer> iterator() {
                    return new DummyCloudBlobContainerIterator(list);
                }
            });
            reader.blobService = blobService;

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
        AzureStorageContainerListReader reader = (AzureStorageContainerListReader) source.createReader(runtimeContainer);

        try {
            final List<CloudBlobContainer> list = new ArrayList<>();
            list.add(new CloudBlobContainer(new URI("https://fakeAccountName.blob.core.windows.net/container-1")));
            list.add(new CloudBlobContainer(new URI("https://fakeAccountName.blob.core.windows.net/container-2")));
            list.add(new CloudBlobContainer(new URI("https://fakeAccountName.blob.core.windows.net/container-3")));

            when(blobService.listContainers()).thenReturn(new Iterable<CloudBlobContainer>() {

                @Override
                public Iterator<CloudBlobContainer> iterator() {
                    return new DummyCloudBlobContainerIterator(list);
                }
            });
            reader.blobService = blobService;

            assertThat(reader, canStart());
            assertThat(reader, canAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceUnavailableDieOnError() {
        try {
            properties.dieOnError.setValue(true);

            assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
            AzureStorageContainerListReader reader = (AzureStorageContainerListReader) source.createReader(runtimeContainer);

            when(blobService.listContainers()).thenThrow(new InvalidKeyException(new RuntimeException("Unvailable Source")));
            reader.blobService = blobService;

            assertThat(reader, startAndDieOnError());
            assertThat(reader, cannotAdvance());

        } catch (URISyntaxException | InvalidKeyException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceUnavailableHandleError() {
        try {

            properties.dieOnError.setValue(false);

            assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());
            AzureStorageContainerListReader reader = (AzureStorageContainerListReader) source.createReader(runtimeContainer);

            when(blobService.listContainers()).thenThrow(new InvalidKeyException(new RuntimeException("Unvailable Source")));
            reader.blobService = blobService;

            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (URISyntaxException | InvalidKeyException e) {
            fail("should not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testClose() {
        //
    }

}
