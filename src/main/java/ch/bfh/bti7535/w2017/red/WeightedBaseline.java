package ch.bfh.bti7535.w2017.red;

import java.util.List;
import java.util.stream.Collectors;

import ch.bfh.bti7535.w2017.red.SentiWordNetSet.SynsetTerm;
import ch.bfh.bti7535.w2017.red.preprocessing.LoadFiles;
import ch.bfh.bti7535.w2017.red.preprocessing.Tokenize;
import net.sf.extjwnl.JWNLException;


public class WeightedBaseline
{
	private static class Counter
	{
		private final long k_lStartTime;
		private final int k_iTarget;
		private int m_iCounter = 0;

		public Counter(final int iTarget)
		{
			if(iTarget < 1)
			{
				throw new IllegalArgumentException("Must count to at least 1!");
			}

			k_iTarget = iTarget;
			k_lStartTime = System.nanoTime();
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
			final double dProgress = progress();
			final long lTime = (System.nanoTime() - k_lStartTime);

			long lRemaining = (long) (lTime * ((1.0 - dProgress) / dProgress));
			final int iHours = (int) (lRemaining / (1e9 * 60.0 * 60.0));
			lRemaining -= (iHours * 1e9 * 60.0 * 60.0);
			final int iMinutes = (int) (lRemaining / (1e9 * 60.0));
			lRemaining -= (iMinutes * 1e9 * 60.0);
			final int iSeconds = (int) (lRemaining * 1e-9);

			System.out.printf("%d/%d [%g%%] (%dh%dm%ds left)\n", m_iCounter, k_iTarget, dProgress * 100.0, iHours, iMinutes, iSeconds);
		}
	}

	public static void main(final String[] rgArgs) throws Exception
	{
		try
		{
			final long lStart = System.nanoTime();	

			final SentiWordNetSet xSentiSet = SentiWordNetSet.fromFile("src/main/resources/sentiwordnet.txt");

			final List<String> lstPositive = LoadFiles.getPositiveReviews();
			final List<String> lstNegative = LoadFiles.getNegativeReviews();

			final int iNum = lstPositive.size() + lstNegative.size();
			final Counter xCounter = new Counter(iNum);
			final List<Double> lstPositiveScores = lstPositive.stream()
					.map(Tokenize::tokenize)
					.map(lstTokenized ->
					{
						double dScore = 0.0;
						int iCount = 0;
						for(final String strWord : lstTokenized)
						{
							final List<SynsetTerm> lstTerms = xSentiSet.lookupAllWords(strWord);
							for(final SynsetTerm xTerm : lstTerms)
							{
								++iCount;
								dScore += (xTerm.positiveScore() / (xTerm.senseNumber() + 1));
								dScore -= (xTerm.negativeScore() / (xTerm.senseNumber() + 1));
							}
						}
						if(iCount > 0)
						{
							dScore /= iCount;
						}

						xCounter.count();
						xCounter.reportProgress();
						return dScore;
					}).collect(Collectors.toList());
			final List<Double> lstNegativeScores = lstNegative.stream()
					.map(Tokenize::tokenize)
					.map(lstTokenized ->
					{
						double dScore = 0.0;
						int iCount = 0;
						for(final String strWord : lstTokenized)
						{
							final List<SynsetTerm> lstTerms = xSentiSet.lookupAllWords(strWord);
							for(final SynsetTerm xTerm : lstTerms)
							{
								++iCount;
								dScore += (xTerm.positiveScore() / (xTerm.senseNumber() + 1));
								dScore -= (xTerm.negativeScore() / (xTerm.senseNumber() + 1));
							}
						}
						if(iCount > 0)
						{
							dScore /= iCount;
						}

						xCounter.count();
						xCounter.reportProgress();
						return dScore;
					}).collect(Collectors.toList());

			final long lTime = (int) ((System.nanoTime() - lStart) * 1e-9);
			final int iMinutes = (int) (lTime / 60);
			final int iSeconds = (int) (lTime % 60);
			System.out.printf("Done in %02d:%02d\n", iMinutes, iSeconds);

			final int iCorrect = (int) (lstNegativeScores.stream().filter(d -> d <= 0.0).count() + lstPositiveScores.stream().filter(d -> d >= 0.0).count());
			System.out.printf("Result: %g%% correct\n", 100 * iCorrect / (double) iNum);
		}
		catch(final JWNLException xException)
		{
			System.err.println("Could not load WordNet dictionary!");
		}
	}
}
