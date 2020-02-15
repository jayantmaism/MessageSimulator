package com.data.test.processer.common;

/*
 * All the common function and utility functions
 */
public class MessageProcessorUtility {

	/*
	 * identified the number of cores available in system
	 */
	public static int getAvailableCores(){
		return Runtime.getRuntime().availableProcessors();
	}
}
