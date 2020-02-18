package com.data.test.processer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.test.processer.common.MessageProcessorConstants;
import com.data.test.processer.dto.FinalResultSetABCD;
import com.data.test.processer.dto.SourceABImpl;
import com.data.test.processer.dto.SourceCDImpl;
import com.data.test.processer.message.ConsumerTask;
import com.data.test.processer.message.Dispatcher;
import com.data.test.processer.message.MesssageProducer;

/*
 * MessageProcessor : It will process the data and consume the data then It will produce the resultSet data
 */
public class MessageProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

	public void startMessageSimulator(int totalMessageCount, int totalThreadsForProcessing) throws Exception {

		/*
		 * commonQueue : All sources will put messages in commonQueue
		 * abSourceMsgQueue : Dispatcher will put the messages in
		 * abSourceMsgQueue which is coming from source A and B cdSourceMsgQueue
		 * : Dispatcher will put the messages in cdSourceMsgQueue which is
		 * coming from source C and D finalResultSetQueue : Source AB and source
		 * CD will put their messages after processing
		 * 
		 * abConsumerService : Source AB will submit their task to
		 * abConsumerService cdConsumerService : Source CD will submit their
		 * task to abConsumeService
		 */
		BlockingQueue<FinalResultSetABCD> commonQueue = new LinkedBlockingQueue<FinalResultSetABCD>(totalMessageCount);
		BlockingQueue<FinalResultSetABCD> abSourceMsgQueue = new LinkedBlockingQueue<FinalResultSetABCD>(
				totalMessageCount / 2);
		BlockingQueue<FinalResultSetABCD> cdSourceMsgQueue = new LinkedBlockingQueue<FinalResultSetABCD>(
				totalMessageCount / 2);
		BlockingQueue<Future<FinalResultSetABCD>> finalResultSetQueue = new LinkedBlockingQueue<Future<FinalResultSetABCD>>(
				totalMessageCount);
		ExecutorService abConsumerService = Executors.newFixedThreadPool(totalThreadsForProcessing);
		ExecutorService cdConsumerService = Executors.newFixedThreadPool(totalThreadsForProcessing);
		ExecutorService executorService = Executors.newFixedThreadPool(2);

		// commonErrorQueue : All the failed messages while putting in
		// commonQueue will go in commonErrorQueue
		// commonErrorQueue Will be processed by error dispatcher
		BlockingQueue<FinalResultSetABCD> commonErrorQueue = new LinkedBlockingQueue<FinalResultSetABCD>(
				totalMessageCount);
		try {

			/*
			 * Start result set consumer
			 */
			startResultSetConsumerTask(totalMessageCount, abSourceMsgQueue, cdSourceMsgQueue, finalResultSetQueue,
					abConsumerService, cdConsumerService, executorService);

			/*
			 * Start Dispatcher thread
			 */
			Thread dispatcherThread = new Thread(
					new Dispatcher(commonQueue, totalMessageCount, abSourceMsgQueue, cdSourceMsgQueue));
			dispatcherThread.start();

			/*
			 * commonErrorQueue : start Another Dispatcher thread will process
			 * commonErrorQueue If commonErrorQueue has any task
			 */
			Thread errorDispatcherThread = new Thread(
					new Dispatcher(commonErrorQueue, totalMessageCount, abSourceMsgQueue, cdSourceMsgQueue));
			errorDispatcherThread.start();

			/*
			 * Starting producer Thread
			 */
			startProducers(commonQueue, commonErrorQueue, totalMessageCount);

			/*
			 * Below method will display all the task in console OR file based
			 * on the logback configuration
			 */
			displayResultSetTask(totalMessageCount, finalResultSetQueue);

		} catch (Exception e) {
			throw new Exception(e.getCause());
		} finally {
			executorService.shutdown();
			abConsumerService.shutdown();
			cdConsumerService.shutdown();
		}

	}

	/*
	 * This method will display all the task on console OR file based on the
	 * logback configuration
	 */
	private void displayResultSetTask(int totalMessageCount,
			BlockingQueue<Future<FinalResultSetABCD>> finalResultSetQueue) {
		for (int i = 0; i < totalMessageCount; i++) {
			try {
				String processedValue = finalResultSetQueue.poll(1, TimeUnit.MINUTES).get().getProcessedValue();
				LOGGER.info("Thread Name ::   {}  and processed message :: {} ", Thread.currentThread().getName(),
						processedValue);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * This method will start ResultSet consumer task
	 */
	private void startResultSetConsumerTask(int totalMessageCount, BlockingQueue<FinalResultSetABCD> abSourceMsgQueue,
			BlockingQueue<FinalResultSetABCD> cdSourceMsgQueue,
			BlockingQueue<Future<FinalResultSetABCD>> finalResultSetQueue, ExecutorService abConsumerService,
			ExecutorService cdConsumerService, ExecutorService executorService) {
		startResultSetTask(abSourceMsgQueue, abConsumerService, finalResultSetQueue, totalMessageCount,
				executorService);
		startResultSetTask(cdSourceMsgQueue, cdConsumerService, finalResultSetQueue, totalMessageCount,
				executorService);
	}

	/*
	 * Starting resultSet thread
	 */
	private void startResultSetTask(BlockingQueue<FinalResultSetABCD> queue, ExecutorService consumerWorkerService,
			BlockingQueue<Future<FinalResultSetABCD>> finalResultSetQueue, int totalTaskByAllProducers,
			ExecutorService executorService) {
		executorService
				.submit(new ConsumerTask(queue, consumerWorkerService, finalResultSetQueue, totalTaskByAllProducers));
	}

	/**
	 * commonQueue : Source A , B ,C ,D put the messages in commonQueue
	 * totalTaskByAllProducers : Equal Task will be divided in all producer
	 * based on the source
	 * 
	 * Here is an assumption that each producer will produce same number of task
	 * objects.
	 */
	private void startProducers(BlockingQueue<FinalResultSetABCD> commonQueue,
			BlockingQueue<FinalResultSetABCD> commonErrorQueue, int totalTaskByAllProducers) {

		/*
		 * Create all the producers and start all the producers
		 */

		int individualTaskCount = totalTaskByAllProducers / 4;
		MesssageProducer producerA = new MesssageProducer(commonQueue, commonErrorQueue,
				createTask("A", individualTaskCount, true));
		MesssageProducer producerB = new MesssageProducer(commonQueue, commonErrorQueue,
				createTask("B", individualTaskCount, true));
		MesssageProducer producerC = new MesssageProducer(commonQueue, commonErrorQueue,
				createTask("C", individualTaskCount, false));
		MesssageProducer producerD = new MesssageProducer(commonQueue, commonErrorQueue,
				createTask("D", individualTaskCount, false));

		producerA.start();
		producerB.start();
		producerC.start();
		producerD.start();
	}

	/*
	 * Identified the source and based on the source putting the messages in
	 * respective queue
	 */
	private List<FinalResultSetABCD> createTask(String msgId, int totalTask, boolean isSourceAB) {
		List<FinalResultSetABCD> list = new ArrayList<FinalResultSetABCD>();
		for (int i = 0; i < totalTask; i++) {
			if (isSourceAB)
				list.add(new SourceABImpl(msgId + (i + 1), MessageProcessorConstants.AB_MESSAGE_SOURCE));
			else
				list.add(new SourceCDImpl(msgId + (i + 1), MessageProcessorConstants.CD_MESSAGE_SOURCE));
		}
		return list;
	}

}
