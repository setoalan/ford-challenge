package com.logicdrop.fordchallenge.api.utils;

/**
 * String utilities
 * 
 * @author kjq
 * 
 */
public final class StringHelper
{

	/**
	 * Joins an array of tokens.
	 * 
	 * @param delim delimiter to separate tokens with.
	 * @param emptyVal if the value is null what to use.
	 * @param tokens tokens to join
	 * @return a joined string of the tokens.
	 */
	public static String join(final char delim, final String emptyVal, final String[] tokens)
	{
		final StringBuilder buf = new StringBuilder();

		for (int i = 0; i < tokens.length; i++)
		{
			final String tokenVal = tokens[i];
			buf.append(valueOr(tokenVal, emptyVal));

			// Add delimiter?
			if (i < tokens.length - 1)
			{
				buf.append(delim);
			}
		}

		return buf.toString();
	}

	/**
	 * Returns the value if not null or empty.
	 * 
	 * @param value
	 * @param orValue
	 * @return
	 */
	public static String valueOr(final String value, final String orValue)
	{
		if (value == null || "".equals(value))
		{
			return orValue;
		}

		return value;
	}
}
