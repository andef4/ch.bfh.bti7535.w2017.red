package ch.bfh.bti7535.utilities;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.omg.Messaging.SyncScopeHelper;


public final class RichStream<T>
{
	private final Stream<T> k_xStream;

	static class MyClass
	{
		public MyClass()
		{
			System.out.println("New instance!");
		}
	}

	public static void main(final String[] rgArgs)
	{
		Stream.concat(Stream.iterate(1, i -> i + 1), Stream.iterate(1, i -> i + 1));
		System.out.println("Hi");
		final RichStream<Integer> xStream = new RichStream<Integer>(Stream.iterate(1, i -> i + 1));
		final Tuple<Stream<Integer>, Stream<Integer>> x = xStream.partition(i -> i < 10000);
		x.first().forEach(System.out::println);
		System.out.println("---");
		x.second().forEach(System.out::println);
	}


	public RichStream(final Stream<T> xStream)
	{
		k_xStream = xStream;
	}

	public Stream<T> stream()
	{
		return k_xStream;
	}

	public Tuple<Stream<T>, Stream<T>> partition(final Predicate<T> xPredicate)
	{
		return k_xStream.reduce(
				new Tuple<>(Stream.<T>empty(), Stream.<T>empty()),
				(xRes, xElem) -> xPredicate.test(xElem)
					? new Tuple<>(Stream.concat(xRes.first(), Stream.of(xElem)), xRes.second())
					: new Tuple<>(xRes.first(), Stream.concat(xRes.second(), Stream.of(xElem))),
				(xA, xB) -> new Tuple<>(Stream.concat(xA.first(), xB.first()), Stream.concat(xA.second(), xB.second())));
	}
}
