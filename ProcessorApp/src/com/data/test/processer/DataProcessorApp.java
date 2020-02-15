package com.data.test.processer;

import com.data.test.processer.common.MessageProcessorUtility;

/*
 * Processor app is starting point of execution
 */
public class DataProcessorApp {

	public static void main(String[] args) throws Exception {

		MessageProcessor messageProcessor = new MessageProcessor();
		// Start message simulator to produce the messages and consume the messages	
		messageProcessor.startMessageSimulator(80, MessageProcessorUtility.getAvailableCores());

	}
}
