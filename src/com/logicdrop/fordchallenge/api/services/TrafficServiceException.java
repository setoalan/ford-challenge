package com.logicdrop.fordchallenge.api.services;

/**
 * Traffice service exception.
 * <p>
 * To be used when a low-level service fails.
 * 
 * @author kjq
 * 
 */
public class TrafficServiceException extends Exception
{
	private static final long serialVersionUID = -9101551022936593193L;

	public TrafficServiceException()
	{
		super();
	}

	public TrafficServiceException(final String message)
	{
		super(message);
	}

	public TrafficServiceException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public TrafficServiceException(final Throwable cause)
	{
		super(cause);
	}

}
