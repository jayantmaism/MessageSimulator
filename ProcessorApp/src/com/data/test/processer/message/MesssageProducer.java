package com.data.test.processer.message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.data.test.processer.dto.FinalResultSetABCD;

/*
 * It will take the messages from the all queues and put in common queues.
 */
public class MesssageProducer extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(MesssageProducer.class);

	private BlockingQueue<FinalResultSetABCD> commonQueue;

	List<FinalResultSetABCD> allSourceMsgList;

	public MesssageProducer(BlockingQueue<FinalResultSetABCD> commonQueue, List<FinalResultSetABCD> allSourceMsgList) {
		super();
		this.commonQueue = commonQueue;
		this.allSourceMsgList = allSourceMsgList;
	}

	@Override
	public void run() {
		/**
		 * Put all the task object in common queue.
		 */
		allSourceMsgList.stream().filter(task -> (task != null && !StringUtils.isEmpty(task.getValue())))
				.forEach(task -> {
					try {
						commonQueue.put(task);
						LOGGER.debug("Message added in common Queue.");
					} catch (InterruptedException e) {
						LOGGER.error("Error while putting the message in commonQueue and exception is {}",e.getMessage());
					}
				});
	}
}
