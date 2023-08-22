package net.lakis.cerebro.web.exceptions;

public class InServletException extends Exception {
	private static final long serialVersionUID = 1L;
	private int httpStatus;
	private String httpMessage;

	public InServletException(int httpStatus, String httpMessage)
	{
		super(httpStatus + " " + httpMessage);
		this.setHttpStatus(httpStatus);
		this.setHttpMessage(httpMessage);
	}

	public String getHttpMessage() {
		return httpMessage;
	}

	public void setHttpMessage(String httpMessage) {
		this.httpMessage = httpMessage;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
}
