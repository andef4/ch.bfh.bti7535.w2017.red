package ch.bfh.bti7535.utilities;


/**
 * Represents a pair with two elements, i.e. tuple
 * It wraps two elements which may be of different type into a single tuple.
 * The class is immutable, however the getters return references to the elements
 * 
 * @author Casimir Platzer
 *
 * @param <T> type of the first element
 * @param <U> type of the second element
 */
public final class Tuple<T, U>
{
	/** first element */
	private final T k_xA;
	/** second element */
	private final U k_xB;


	/**
	 * Constructor takes two references and keeps them stored internally
	 * 
	 * @param xA first element
	 * @param xB second element
	 */
	public Tuple(final T xA, final U xB)
	{
		k_xA = xA;
		k_xB = xB;
	}

	/** @return reference to first element */
	public T first()
	{
		return k_xA;
	}

	/** @return reference to second element */
	public U second()
	{
		return k_xB;
	}

	@Override
	public boolean equals(final Object xObject)
	{
		// two tuples are equal if their elements are equal
		if(!(xObject.getClass().equals(getClass())))
		{
			return false;
		}

		final Tuple<?, ?> xOther = (Tuple<?, ?>) xObject;
		return ((xOther.k_xA == null || k_xA == null) ? xOther.k_xA == null && k_xA == null : xOther.k_xA.equals(k_xA))
		    && ((xOther.k_xB == null || k_xB == null) ? xOther.k_xB == null && k_xB == null : xOther.k_xB.equals(k_xB));
	}

	@Override
	public int hashCode()
	{
		// use the lower 16bits of each elements' hashcode, make a 32bit integer out of them
		return ((k_xA.hashCode() & 0xFFFF) << 16)
		     | (k_xB.hashCode() & 0xFFFF);
	}

	@Override
	public String toString()
	{
		return String.format("(%s, %s)", k_xA, k_xB);
	}
}
