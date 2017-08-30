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
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.canStart;
import static org.talend.components.api.test.runtime.reader.ReaderMatchers.startAndDieOnError;

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
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.test.runtime.reader.SourceReaderTest;
import org.talend.components.azurestorage.AzureBaseTest;
import org.talend.components.azurestorage.RuntimeContainerMock;
import org.talend.components.azurestorage.queue.AzureStorageQueueService;
import org.talend.components.azurestorage.queue.tazurestoragequeueinputloop.TAzureStorageQueueInputLoopProperties;
import org.talend.daikon.properties.ValidationResult;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public class AzureStorageQueueInputLoopReaderTest extends AzureBaseTest implements SourceReaderTest {

    private TAzureStorageQueueInputLoopProperties properties;

    private AzureStorageQueueSource source;

    private RuntimeContainer runtimeContainer;

    @Mock
    private AzureStorageQueueService queueService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws IOException {
        properties = new TAzureStorageQueueInputLoopProperties("QueueInputLoopReader");
        properties.setupProperties();
        properties.connection = getValidFakeConnection();
        properties.queueName.setValue("some-queue-name");

        source = new AzureStorageQueueSource();
        runtimeContainer = new RuntimeContainerMock();
    }

    @Test
    @Override
    public void testReadSourceEmpty() {
        // Can't test that as the reader loop indefinitely
    }

    @Test
    @Override
    public void testReadSourceWithOnly1Element() {
        try {

            assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());

            AzureStorageQueueInputLoopReader reader = (AzureStorageQueueInputLoopReader) source
                    .createReader(getDummyRuntimeContiner());

            final List<CloudQueueMessage> messages = new ArrayList<>();
            messages.add(new CloudQueueMessage("message-1"));
            when(queueService.retrieveMessages(anyString(), anyInt())).thenReturn(new Iterable<CloudQueueMessage>() {

                @Override
                public Iterator<CloudQueueMessage> iterator() {
                    return new DummyCloudQueueMessageIterator(messages);
                }
            });
            doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    return null;
                }
            }).when(queueService).deleteMessage(anyString(), any(CloudQueueMessage.class));

            reader.queueService = queueService; // inject mocked service

            assertThat(reader, canStart());
            // assertThat(reader, cannotAdvance()); this will loop indefinitely

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceWithManyElements() {
        try {
            assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());

            AzureStorageQueueInputLoopReader reader = (AzureStorageQueueInputLoopReader) source
                    .createReader(getDummyRuntimeContiner());

            final List<CloudQueueMessage> messages = new ArrayList<>();
            messages.add(new CloudQueueMessage("message-1"));
            messages.add(new CloudQueueMessage("message-2"));
            messages.add(new CloudQueueMessage("message-3"));
            when(queueService.retrieveMessages(anyString(), anyInt())).thenReturn(new Iterable<CloudQueueMessage>() {

                @Override
                public Iterator<CloudQueueMessage> iterator() {
                    return new DummyCloudQueueMessageIterator(messages);
                }
            });
            doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    return null;
                }
            }).when(queueService).deleteMessage(anyString(), any(CloudQueueMessage.class));

            reader.queueService = queueService; // inject mocked service

            assertThat(reader, canStart());
            // assertThat(reader, canAdvance()); this will loop indefinitely

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }

    }

    @Test
    @Override
    public void testReadSourceUnavailableDieOnError() {
        try {

            properties.dieOnError.setValue(true);

            assertEquals(ValidationResult.Result.OK, source.initialize(runtimeContainer, properties).getStatus());
            assertEquals(ValidationResult.Result.OK, source.validate(runtimeContainer).getStatus());

            AzureStorageQueueInputLoopReader reader = (AzureStorageQueueInputLoopReader) source
                    .createReader(getDummyRuntimeContiner());

            when(queueService.retrieveMessages(anyString(), anyInt()))
                    .thenThrow(new StorageException("500", "Unvailable source", new RuntimeException()));

            reader.queueService = queueService; // inject mocked service

            assertThat(reader, startAndDieOnError());
            // assertThat(reader, cannotAdvance()); // this will loop indefinitely

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            fail("sould not throw " + e.getMessage());
        }
    }

    @Test
    @Override
    public void testReadSourceUnavailableHandleError() {
        // Can't test that as the reader loop indefinitely
    }

    @Override
    public void testClose() {
        //
    }

}
