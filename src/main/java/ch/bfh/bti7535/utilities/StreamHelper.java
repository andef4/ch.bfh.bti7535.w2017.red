package ch.bfh.bti7535.utilities;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public final class StreamHelper
{
	private StreamHelper()
	{
	}

	public static <T, U> Stream<Tuple<? extends T, ? extends U>> zip(final Stream<? extends T> xStreamT, final Stream<? extends U> xStreamU)
	{
		final Iterator<? extends T> itIteratorT = xStreamT.iterator();
		final Iterator<? extends U> itIteratorU = xStreamU.iterator();
		final Iterator<Tuple<? extends T, ? extends U>> itC = new Iterator<Tuple<? extends T, ? extends U>>()
		{
			@Override
			public boolean hasNext()
			{
				return itIteratorT.hasNext() && itIteratorU.hasNext();
			}

			@Override
			public Tuple<? extends T, ? extends U> next()
			{
				return new Tuple<>(itIteratorT.next(), itIteratorU.next());
			}
		};
		final Iterable<Tuple<? extends T, ? extends U>> it = () -> itC;
		return StreamSupport.stream(it.spliterator(), xStreamT.isParallel() && xStreamU.isParallel());
	}

	public static <T> Stream<Tuple<? extends Integer, ? extends T>> zipWithIndex(final Stream<? extends T> xStream)
	{
		return zip(IntStream.iterate(0, i -> i + 1).boxed(), xStream);
	}
}
