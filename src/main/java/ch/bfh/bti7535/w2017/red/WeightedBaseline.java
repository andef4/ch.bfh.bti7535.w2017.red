package ch.bfh.bti7535.w2017.red;

import java.util.List;
import java.util.stream.Collectors;

import ch.bfh.bti7535.w2017.red.SentiWordNetSet.Term;
import ch.bfh.bti7535.w2017.red.preprocessing.LoadFiles;
import ch.bfh.bti7535.w2017.red.preprocessing.Tokenize;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;


public class WeightedBaseline
{
	private static class Counter
	{
		private final int k_iTarget;
		private int m_iCounter = 0;

		public Counter(final int iTarget)
		{
			if(iTarget < 1)
			{
				throw new IllegalArgumentException("Must count to at least 1!");
			}

			k_iTarget = iTarget;
		}

		public void count()
		{
			++m_iCounter;
		}

		public double progress()
		{
			return (m_iCounter / (double) k_iTarget);
		}

		public void reportProgress()
		{
			System.out.printf("Progress: %g%%\n", progress() * 100.0);
		}
	}

	public static void main(final String[] rgArgs) throws Exception
	{
		try
		{
			final long lStart = System.nanoTime();

			final Dictionary xDictionary = Dictionary.getDefaultResourceInstance();
			final SentiWordNetSet xSentiSet = SentiWordNetSet.fromFile("src/main/resources/sentiwordnet.txt");

			final List<String> lstPositive = LoadFiles.getPositiveReviews();
			final List<String> lstNegative = LoadFiles.getNegativeReviews();

			final Counter xCounter = new Counter(lstPositive.size() + lstNegative.size());
			final List<Double> lstPositiveScores = lstPositive.parallelStream()
					.map(Tokenize::tokenize)
					.map(lstTokenized ->
					{
						double dScore = 0.0;
						for(final String strWord : lstTokenized)
						{
							final List<Term> lstTerms = xSentiSet.lookupAllWords(strWord);
							for(final Term xTerm : lstTerms)
							{
								dScore += (xTerm.positiveScore() / (xTerm.senseNumber() + 1));
								dScore -= (xTerm.negativeScore() / (xTerm.senseNumber() + 1));
							}
						}
						xCounter.count();
						xCounter.reportProgress();
						return dScore;
					}).collect(Collectors.toList());
			final List<Double> lstNegativeScores = lstNegative.parallelStream()
					.map(Tokenize::tokenize)
					.map(lstTokenized ->
					{
						double dScore = 0.0;
						for(final String strWord : lstTokenized)
						{
							final List<Term> lstTerms = xSentiSet.lookupAllWords(strWord);
							for(final Term xTerm : lstTerms)
							{
								dScore += (xTerm.positiveScore() / (xTerm.senseNumber() + 1));
								dScore -= (xTerm.negativeScore() / (xTerm.senseNumber() + 1));
							}
						}
						xCounter.count();
						xCounter.reportProgress();
						return dScore;
					}).collect(Collectors.toList());

			final long lTime = (int) ((System.nanoTime() - lStart) * 1e-9);
			final int iMinutes = (int) (lTime / 60);
			final int iSeconds = (int) (lTime % 60);
			System.out.printf("Done in %02d:%02d\n", iMinutes, iSeconds);
		}
		catch(final JWNLException xException)
		{
			System.err.println("Could not load WordNet dictionary!");
		}
	}
}
