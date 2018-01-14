package ch.bfh.bti7535.w2017.red.utilities;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public final class StreamHelper
{
	private StreamHelper()
	{
	}

	public static void main(final String[] rgArgs)
	{
	}

//	public static <T, U> Tuple<Stream<? extends T>, Stream<? extends U>> unzip(final Stream<Tuple<? extends T, ? extends U>> xStream)
//	{
//		final Iterator<Tuple<? extends T, ? extends U>> itIterator = xStream.iterator();
//		final Iterator<A> itT = new Iterator<A>()
//		{
//			@Override
//			public boolean hasNext()
//			{
//				return itIterator.hasNext();
//			}
//
//			@Override
//			public A next()
//			{
//				return itIterator.next();
//			}
//		}
//	}

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
