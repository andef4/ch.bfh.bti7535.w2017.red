package ch.bfh.bti7535.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;


public final class FileUtilities
{
	private FileUtilities()
	{
	}

	public static Stream<String> readAllLines(final String strPath)
	{
		try
		{
			final BufferedReader xReader = new BufferedReader(new InputStreamReader(new FileInputStream(strPath)));
			return xReader.lines();
		}
		catch(final IOException xException)
		{
			xException.printStackTrace();
			return Stream.empty();
		}
	}
}
