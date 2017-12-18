package ch.bfh.bti7535.utilities;

import java.util.function.Predicate;
import java.util.stream.Stream;


public final class RichStream<T>
{
	private final Stream<T> k_xStream;

	public static void main(final String[] rgArgs)
	{
		final RichStream<Integer> xStream = new RichStream<Integer>(Stream.iterate(1, i -> i + 1));
//		final Tuple<Stream<Integer>, Stream<Integer>> xPartitioned = xStream.partition(i -> i % 2 == 0);
//		System.out.println(xPartitioned.first().limit(10).collect(Collectors.toList()));
		Stream<Integer> x = Stream.generate(() -> 1);
	}


	public RichStream(final Stream<T> xStream)
	{
		k_xStream = xStream;
	}

	public Tuple<Stream<T>, Stream<T>> partition(final Predicate<T> xPredicate)
	{
		k_xStream.map(xElem -> xPredicate.test(xElem) ? );
		return new Tuple<>(k_xStream.filter(xPredicate), k_xStream.filter(xPredicate.negate()));
	}
}
