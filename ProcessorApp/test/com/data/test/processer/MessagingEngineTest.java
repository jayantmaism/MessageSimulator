package com.data.test.processer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.data.test.processer.common.MessageProcessorConstants;
import com.data.test.processer.dto.FinalResultSetABCD;
import com.data.test.processer.dto.SourceABImpl;
import com.data.test.processer.message.MesssageProducer;

@RunWith(MockitoJUnitRunner.class)
public class MessagingEngineTest {

	@InjectMocks
	MessageProcessor msgSimulator;

	@Test
	public void test1() throws Exception {
		msgSimulator.startMessageSimulator(40, 4);

		MessageProcessor processor = Mockito.mock(MessageProcessor.class);
		processor.startMessageSimulator(16, 2);
		verify(processor, times(1)).startMessageSimulator(16, 2);
	}

	@Test
	public void negativeTesting() throws InterruptedException {
		java.util.concurrent.BlockingQueue mockQueue = Mockito.mock(LinkedBlockingQueue.class);
		BlockingQueue<FinalResultSetABCD> commonErrorQueue = Mockito.mock(LinkedBlockingQueue.class);
		List<FinalResultSetABCD> list = new ArrayList<FinalResultSetABCD>();
		list.add(new SourceABImpl("A1", MessageProcessorConstants.AB_MESSAGE_SOURCE));
		
		MesssageProducer producer = new MesssageProducer(mockQueue,commonErrorQueue,list);
		Mockito.doThrow(new InterruptedException()).when(mockQueue).put(Mockito.anyObject());
		
		producer.run();
		
		verify(commonErrorQueue, times(1)).put(Mockito.anyObject());
	}

}
