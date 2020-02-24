package com.data.test.processer.message.source;

public class SourceABImpl implements FinalResultSetABCD {

	private final String value;
	
	private final String source;
	
	private String valueAfterProcessing;
	
	public SourceABImpl(String value , String source) {
		super();
		this.value = value;
		this.source = source;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public void setProcessedValue(String value) {
		this.valueAfterProcessing = value;
	}
	
	@Override
	public String getProcessedValue() {
		return valueAfterProcessing;
	}

	@Override
	public String getSource() {
		return source;
	}
	
	
}
