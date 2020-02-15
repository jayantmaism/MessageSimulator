package com.data.test.processer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessagingEngineTest {

	@InjectMocks
	MessageProcessor msgSimulator;

	@Test
	public void test1() throws Exception {
		msgSimulator.startMessageSimulator(40, 4);

		MessageProcessor processor = Mockito.mock(MessageProcessor.class);
		processor.startMessageSimulator(16, 2);
		verify(processor, times(1)).startMessageSimulator(40, 4);
	}

}
