package ch.bfh.bti7535.w2017.red.utilities;


public final class Arguments
{
	private final static String k_strIllegalArgument = "Illegal argument!";


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

	public static <T> T require(final T xValue, final boolean bExpression)
	{
		return require(xValue, bExpression, k_strIllegalArgument);
	}
}
