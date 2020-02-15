package com.data.test.processer.customexception;

public class InvalidSourceException extends RuntimeException {

	public InvalidSourceException(String errorMessage) {
		super(errorMessage);
	}
}
