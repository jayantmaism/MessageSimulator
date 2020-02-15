package com.data.test.processer.message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.test.processer.common.MessageProcessorConstants;
import com.data.test.processer.dto.FinalResultSetABCD;

/*
 * Dipatcher will divide the message based on the source and put into respective queue
 */
public class Dispatcher implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);
	
	private BlockingQueue<FinalResultSetABCD> commonMsgQueue;
	private BlockingQueue<FinalResultSetABCD> sourceABMsgQueue;
	private BlockingQueue<FinalResultSetABCD> sourceCDMshQueue;
	private int totalPublishMsgTask;
	
	public Dispatcher(BlockingQueue<FinalResultSetABCD> commonMsgQueue, int totalPublishMsgTask, BlockingQueue<FinalResultSetABCD> sourceABMsgQueue, BlockingQueue<FinalResultSetABCD> sourceCDMshQueue) {
		this.commonMsgQueue = commonMsgQueue;
		this.totalPublishMsgTask = totalPublishMsgTask;
		this.sourceABMsgQueue = sourceABMsgQueue;
		this.sourceCDMshQueue = sourceCDMshQueue;
	}

	@Override
	public void run() {
		for (int i = 0; i < totalPublishMsgTask; i++) {
			try {
				FinalResultSetABCD task = commonMsgQueue.poll(1, TimeUnit.MINUTES);
				if(task.getSource().contains(MessageProcessorConstants.AB_MESSAGE_SOURCE))
					sourceABMsgQueue.put(task);
				else if(task.getSource().contains(MessageProcessorConstants.CD_MESSAGE_SOURCE))
					sourceCDMshQueue.put(task);
				else
					LOGGER.error("Unknown source identified");
			} catch (InterruptedException e) {
				LOGGER.error("Error while putting the message in source queue is {}",e.getMessage());
			}
		}
	}

}