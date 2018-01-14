package ch.bfh.bti7535.utilities;


public final class Arguments
{
	private final static String k_strIllegalArgument = "Illegal argument!";


	/**
	 * Generic method returning the passed argument if the boolean expression evaluates to true, otherwise
	 * throws an exception with the specified error
	 *
	 * @param xValue argument to return
	 * @param bExpression expression to evaluate if it is valid
	 * @param strError error message
	 * @return argument if expression evaluates to true
	 * @throws IllegalArgumentException if the expression fails
	 */
	public static <T> T require(final T xValue, final boolean bExpression, final String strError)
	{
		if(bExpression)
		{
			return xValue;
		}
		else
		{
			throw new IllegalArgumentException(strError);
		}
	}

	/**
	 * Generic method returning the passed argument if the boolean expression evaluates to true, otherwise
	 * throws an exception with the specified error
	 *
	 * @param xValue argument to return
	 * @param bExpression expression to evaluate if it is valid
	 * @return argument if expression evaluates to true
	 * @throws IllegalArgumentException if the expression fails
	 */
	public static <T> T require(final T xValue, final boolean bExpression)
	{
		return require(xValue, bExpression, k_strIllegalArgument);
	}
}
