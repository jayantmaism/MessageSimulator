package com.data.test.processer.message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.test.processer.common.MessageProcessorConstants;
import com.data.test.processer.dto.FinalResultSetABCD;

public class ConsumerTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerTask.class);
	
	/**
	 * queue will hold either task objects produced by A and B or task objects produced by C and D.
	 */
	private BlockingQueue<FinalResultSetABCD> commonQueue;
	
	/**
	 * finalResultSetQueue will hold all the future object after processing all the task objects produced by all producers 
	 */
	private BlockingQueue<Future<FinalResultSetABCD>> finalResultSetQueue;
	
	/**
	 * totalTaskByAllProducers : number of all tasks produced by all producers 
	 */
	private int totalTaskByAllProducers;
	
	private ExecutorService executorService;

	public ConsumerTask(BlockingQueue<FinalResultSetABCD> commonQueue, ExecutorService executorService, BlockingQueue<Future<FinalResultSetABCD>> finalResultSetQueue, int totalTaskByAllProducers) {
		this.commonQueue = commonQueue;
		this.executorService = executorService;
		this.finalResultSetQueue = finalResultSetQueue;
		this.totalTaskByAllProducers = totalTaskByAllProducers;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < totalTaskByAllProducers/2; i++) {
			try {
				FinalResultSetABCD task = commonQueue.poll(MessageProcessorConstants.POLL_FIRST_MSG, TimeUnit.MINUTES);
				
				finalResultSetQueue.put(executorService.submit(new Callable<FinalResultSetABCD>() {
					@Override
					public FinalResultSetABCD call() throws Exception {
						LOGGER.info("Message received from source AB or source CD");
						task.setProcessedValue(String.join(" ", task.getValue(), "Processed"));
						return task;
					}
				}));
			} catch (InterruptedException e) {
				e.printStackTrace();
				LOGGER.error("Error while putting the message in consumers queue and Exception is {} ",e.getMessage());
			}
		}

	}

}
