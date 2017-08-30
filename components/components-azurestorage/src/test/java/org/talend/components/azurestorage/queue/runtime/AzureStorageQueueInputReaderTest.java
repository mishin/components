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
package org.talend.components.azurestorage.queue.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.talend.components.api.test.runtime.reader.SourceReaderTest;
import org.talend.components.azurestorage.AzureBaseTest;
import org.talend.components.azurestorage.queue.AzureStorageQueueService;
import org.talend.components.azurestorage.queue.tazurestoragequeueinput.TAzureStorageQueueInputProperties;
import org.talend.daikon.properties.ValidationResult;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public class AzureStorageQueueInputReaderTest extends AzureBaseTest implements SourceReaderTest {

    private TAzureStorageQueueInputProperties properties;

    private AzureStorageQueueSource source;

    private AzureStorageQueueInputReader reader;

    @Mock
    private AzureStorageQueueService queueService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws IOException {

        source = new AzureStorageQueueSource();

        properties = new TAzureStorageQueueInputProperties("QueueInputReader");
        properties.setupProperties();
        properties.connection = getValidFakeConnection();
        properties.queueName.setValue("some-queue-name");
    }

    @Override
    public void testClose() {
        // TODO Auto-generated method stub

    }

    @Test
    @Override
    public void testReadSourceEmpty() {
        try {
            properties.peekMessages.setValue(true);

            assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
            AzureStorageQueueInputReader reader = (AzureStorageQueueInputReader) source.createReader(getDummyRuntimeContiner());

            when(queueService.peekMessages(anyString(), anyInt())).thenReturn(new Iterable<CloudQueueMessage>() {

                @Override
                public Iterator<CloudQueueMessage> iterator() {
                    return new DummyCloudQueueMessageIterator(new ArrayList<CloudQueueMessage>());
                }
            });
            reader.queueService = queueService; // inject mocked service

            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceUnavailableDieOnError() {
        try {
            properties.peekMessages.setValue(true);
            properties.dieOnError.setValue(true);

            assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
            AzureStorageQueueInputReader reader = (AzureStorageQueueInputReader) source.createReader(getDummyRuntimeContiner());
            reader.queueService = queueService; // inject mocked service

            when(queueService.peekMessages(anyString(), anyInt()))
                    .thenThrow(new StorageException("code", "some storage exception", new RuntimeException()));

            assertThat(reader, startAndDieOnError());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceUnavailableHandleError() {
        try {
            properties.peekMessages.setValue(true);
            properties.deleteMessages.setValue(true);
            properties.dieOnError.setValue(false);

            assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
            AzureStorageQueueInputReader reader = (AzureStorageQueueInputReader) source.createReader(getDummyRuntimeContiner());

            when(queueService.peekMessages(anyString(), anyInt()))
                    .thenThrow(new StorageException("code", "some storage exception", new RuntimeException()));

            reader.queueService = queueService; // inject mocked service
            assertThat(reader, cannotStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceWithManyElements() {
        try {
            properties.peekMessages.setValue(true);

            assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
            AzureStorageQueueInputReader reader = (AzureStorageQueueInputReader) source.createReader(getDummyRuntimeContiner());

            final List<CloudQueueMessage> messages = new ArrayList<>();
            messages.add(new CloudQueueMessage("message-1"));
            messages.add(new CloudQueueMessage("message-2"));
            messages.add(new CloudQueueMessage("message-3"));
            when(queueService.peekMessages(anyString(), anyInt())).thenReturn(new Iterable<CloudQueueMessage>() {

                @Override
                public Iterator<CloudQueueMessage> iterator() {
                    return new DummyCloudQueueMessageIterator(messages);
                }
            });

            reader.queueService = queueService; // inject mocked service

            assertThat(reader, canStart());
            assertThat(reader, canAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }
    }

    @Test
    @Override
    public void testReadSourceWithOnly1Element() {
        try {
            properties.peekMessages.setValue(true);

            assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
            AzureStorageQueueInputReader reader = (AzureStorageQueueInputReader) source.createReader(getDummyRuntimeContiner());

            final List<CloudQueueMessage> messages = new ArrayList<>();
            messages.add(new CloudQueueMessage("message-1"));
            when(queueService.peekMessages(anyString(), anyInt())).thenReturn(new Iterable<CloudQueueMessage>() {

                @Override
                public Iterator<CloudQueueMessage> iterator() {
                    return new DummyCloudQueueMessageIterator(messages);
                }
            });

            reader.queueService = queueService; // inject mocked service

            assertThat(reader, canStart());
            assertThat(reader, cannotAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }
    }

    @Test
    public void testAdvanceWithMessageDeletionHandleError() {
        try {
            properties.peekMessages.setValue(true);
            properties.deleteMessages.setValue(true);
            properties.dieOnError.setValue(false);

            assertEquals(ValidationResult.Result.OK, source.initialize(getDummyRuntimeContiner(), properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(getDummyRuntimeContiner()).getStatus());
            AzureStorageQueueInputReader reader = (AzureStorageQueueInputReader) source.createReader(getDummyRuntimeContiner());
            reader.queueService = queueService; // inject mocked service

            final List<CloudQueueMessage> messages = new ArrayList<>();
            messages.add(new CloudQueueMessage("message-1"));
            messages.add(new CloudQueueMessage("message-2"));
            messages.add(new CloudQueueMessage("message-3"));
            when(queueService.peekMessages(anyString(), anyInt())).thenReturn(new Iterable<CloudQueueMessage>() {

                @Override
                public Iterator<CloudQueueMessage> iterator() {
                    return new DummyCloudQueueMessageIterator(messages);
                }
            });

            doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    throw new StorageException("code", "message-1 can't be deleted", new RuntimeException());
                }
            }).when(queueService).deleteMessage(anyString(), any(CloudQueueMessage.class));

            assertThat(reader, canStart());
            assertThat(reader, canAdvance());

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }
    }
}
