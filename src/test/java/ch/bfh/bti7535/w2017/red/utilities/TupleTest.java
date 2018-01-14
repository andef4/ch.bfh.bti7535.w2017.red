package ch.bfh.bti7535.w2017.red.utilities;

import org.junit.Test;
import static org.junit.Assert.*;


public final class TupleTest
{
	private static final double k_dDelta = 1e-6;


	@Test
	public void ctorTest()
	{
		for(int iTestIdx = 0; iTestIdx < 100; ++iTestIdx)
		{
			new Tuple<>(Math.random(), (int) (Math.random() % 1000.0));
		}
	}

	@Test
	public void getterTest()
	{
		assertEquals(new Tuple<>(0.25, "Test").first().doubleValue(), 0.25, k_dDelta);
		assertEquals(new Tuple<>(3, "Test").first().intValue(), 3);
		assertEquals(new Tuple<>("Test", 0.125).second().doubleValue(), 0.125, k_dDelta);
		assertEquals(new Tuple<>("Test", 9).second().intValue(), 9);
	}

	@Test
	public void getterTestNull()
	{
		assertEquals(new Tuple<>(null, "Test").first(), null);
		assertEquals(new Tuple<>(null, null).first(), null);
		assertEquals(new Tuple<>(3, null).first().intValue(), 3);

		assertEquals(new Tuple<>("Test", null).second(), null);
		assertEquals(new Tuple<>(null, null).second(), null);
		assertEquals(new Tuple<>(null, 6).second().intValue(), 6);

		assertEquals(new Tuple<>(null, null).first(), null);
		assertEquals(new Tuple<>(null, null).second(), null);
	}

	@Test
	public void equalsTest()
	{
		assertEquals(new Tuple<>(3, 4).equals(new Tuple<>(3, 4)), true);
		assertEquals(new Tuple<>(0.5, "Test").equals(new Tuple<>(0.5, "Test")), true);
		assertEquals(new Tuple<>(0.5, "Test").equals(new Tuple<>(0.25, "Test")), false);
		assertEquals(new Tuple<>("Test", 1).equals(new Tuple<>("Test", 1)), true);
		assertEquals(new Tuple<>("Test", 2).equals(new Tuple<>("Test", 9)), false);
		assertEquals(new Tuple<>("Test", 0.5).equals(new Tuple<>("Test", 9)), false);
	}
}
