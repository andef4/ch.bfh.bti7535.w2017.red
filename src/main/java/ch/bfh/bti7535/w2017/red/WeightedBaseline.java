package ch.bfh.bti7535.w2017.red;

import java.util.List;

import ch.bfh.bti7535.w2017.red.SentiWordNetSet.Term;
import ch.bfh.bti7535.w2017.red.preprocessing.LoadFiles;
import ch.bfh.bti7535.w2017.red.preprocessing.Tokenize;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;


public class WeightedBaseline
{
	public static void main(final String[] rgArgs) throws Exception
	{
		try
		{
			final Dictionary xDictionary = Dictionary.getDefaultResourceInstance();
			final SentiWordNetSet xSentiSet = SentiWordNetSet.fromFile("src/main/resources/sentiwordnet.txt");

			final List<String> lstPositive = LoadFiles.getPositiveReviews();
			final List<String> lstNegative = LoadFiles.getNegativeReviews();

			final List<String> strReview = Tokenize.tokenize(lstPositive.get(0));
			double dScore = 0.0;
			for(final String strWord : strReview)
			{
				final List<Term> lstTerms = xSentiSet.lookupAllWords(strWord);
				for(final Term xTerm : lstTerms)
				{
					dScore += (xTerm.positiveScore() / (xTerm.senseNumber() + 1));
					dScore -= (xTerm.negativeScore() / (xTerm.senseNumber() + 1));
				}
			}
			System.out.println(dScore);
		}
		catch(final JWNLException xException)
		{
			System.err.println("Could not load WordNet dictionary!");
		}
	}
}
