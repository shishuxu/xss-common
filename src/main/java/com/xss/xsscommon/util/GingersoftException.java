package com.xss.xsscommon.util;

import lombok.Data;

@Data
public class GingersoftException extends Exception {

	private static final long serialVersionUID = 231652886388215191L;

	private String messageCode;
	private String systemMessage = null;
	private String[] messageTokens = null;

	public GingersoftException(String messageCode, String systemMessage) {
		this(systemMessage, null, messageCode);
	}

	public GingersoftException(String messageCode) {
		this(null, null, messageCode, new String[]{});
	}

	public GingersoftException(String messageCode, String[] messageTokens) {
		this(null, null, messageCode, messageTokens);
	}

	public GingersoftException setMessageTokens(String[] messageTokens) {
		this.messageTokens = messageTokens;
		return this;
	}

	public GingersoftException(Throwable cause, String messageCode, String... messageTokens) {
		this(null, cause, messageCode, messageTokens);
	}

	public GingersoftException(String systemMessage, Throwable cause, String messageCode, String... messageTokens) {
		super(cause);
		this.messageCode = messageCode;
		this.systemMessage = systemMessage;
		if (null != messageTokens && 0 < messageTokens.length) {
			this.messageTokens = messageTokens;
		}
	}
}
