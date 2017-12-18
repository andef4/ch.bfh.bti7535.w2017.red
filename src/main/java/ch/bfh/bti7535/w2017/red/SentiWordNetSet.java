package ch.bfh.bti7535.w2017.red;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.bfh.bti7535.utilities.Arguments;
import ch.bfh.bti7535.utilities.FileUtilities;
import ch.bfh.bti7535.utilities.Tuple;


public final class SentiWordNetSet
{
	private final Map<Tuple<EPosType, Integer>, Synset> k_mapSynsets;


	private SentiWordNetSet(final Map<Tuple<EPosType, Integer>, Synset> mapSynsets)
	{
		k_mapSynsets = new HashMap<>();
		k_mapSynsets.putAll(mapSynsets);
	}

	public static SentiWordNetSet fromFile(final String strPath) throws Exception
	{
		final Builder xBuilder = new Builder();

		FileUtilities.readAllLines(strPath).forEach(strLine ->
		{
			final String strTrimmed = strLine.trim();
			if(!strTrimmed.startsWith("#") && !strTrimmed.isEmpty())
			{
				final String[] strStuff = strTrimmed.split("\t");
				final EPosType eType = EPosType.fromString(strStuff[0]);
				final int iId = Integer.parseInt(strStuff[1], 10);
				final double dPosScore = Double.parseDouble(strStuff[2]);
				final double dNegScore = Double.parseDouble(strStuff[3]);
				final List<Term> lstTerms = new ArrayList<>();
				for(final String strTerm : strStuff[4].split("\\s"))
				{
					final int iHashIndex = strTerm.indexOf('#');
					final String strName = strTerm.substring(0, iHashIndex);
					final int iSenseNumber = Integer.parseInt(strTerm.substring(iHashIndex + 1));
					lstTerms.add(new Term(strName, iSenseNumber, dPosScore, dNegScore));
				}
				final String strGlossary = strStuff[5];
				xBuilder.addSynset(eType, iId, new Synset(dPosScore, dNegScore, lstTerms, strGlossary));
			}
		});

		return xBuilder.build();
	}

	public List<Term> lookupAllWords(final String strWord)
	{
		final List<Term> lstTerms = new ArrayList<>();
		for(final EPosType eType : EPosType.values())
		{
			lstTerms.addAll(lookupWord(eType, strWord));
		}
		return lstTerms;
	}

	public List<Term> lookupWord(final EPosType eType, final String strWord)
	{
		Objects.requireNonNull(eType);
		Arguments.require(strWord, strWord != null && !strWord.isEmpty());

		final List<Term> lstTerms = new ArrayList<>();
		for(final Tuple<EPosType, Integer> xKey : k_mapSynsets.keySet())
		{
			if(xKey.first() != eType)
			{
				continue;
			}

			final Synset xSynset = k_mapSynsets.get(xKey);
			for(final Term xTerm : xSynset.terms())
			{
				if(xTerm.term().equals(strWord))
				{
					lstTerms.add(xTerm);
				}
			}
		}

		return lstTerms;
	}


	private static class Builder
	{
		private final Map<Tuple<EPosType, Integer>, Synset> m_mapSynsets = new HashMap<>();


		public Builder addSynset(final EPosType eType, final int iId, final Synset xSynset)
		{
			final Tuple<EPosType, Integer> xKey = new Tuple<>(eType, iId);
			m_mapSynsets.put(xKey, xSynset);
			return this;
		}

		public SentiWordNetSet build()
		{
			return new SentiWordNetSet(m_mapSynsets);
		}
	}


	public static class Synset
	{
		private final double k_dPosScore;
		private final double k_dNegScore;
		private final List<Term> k_lstTerms;
		private final String k_strGlossary;


		private Synset(final double dPosScore, final double dNegScore, final List<Term> lstTerms, final String strGlossary)
		{
			k_dPosScore = Arguments.require(dPosScore, 0.0 <= dPosScore && dPosScore <= 1.0, "Positive score out of range!");
			k_dNegScore = Arguments.require(dNegScore, 0.0 <= dNegScore && dNegScore <= 1.0, "Negative score out of range!");
			k_lstTerms = new ArrayList<>();
			k_lstTerms.addAll(lstTerms);
			k_strGlossary = Objects.requireNonNull(strGlossary);
		}

		public double positiveScore()
		{
			return k_dPosScore;
		}

		public double negativeScore()
		{
			return k_dNegScore;
		}

		public List<Term> terms()
		{
			return Collections.unmodifiableList(k_lstTerms);
		}

		public String glossary()
		{
			return k_strGlossary;
		}
	}


	public static class Term
	{
		private final String k_strTerm;
		private final int k_iSenseNumber;
		private final double k_dPosScore;
		private final double k_dNegScore;


		private Term(final String strTerm, final int iSenseNumber, final double dPosScore, final double dNegScore)
		{
			k_strTerm = Objects.requireNonNull(strTerm, "Term mustn't be null");
			k_iSenseNumber = Arguments.require(iSenseNumber, iSenseNumber >= 1, "Sense number must be >0");
			k_dPosScore = Arguments.require(dPosScore, 0.0 <= dPosScore && dPosScore <= 1.0, "Positive score out of range!");
			k_dNegScore = Arguments.require(dNegScore, 0.0 <= dNegScore && dNegScore <= 1.0, "Negative score out of range!");
		}

		public String term()
		{
			return k_strTerm;
		}

		public int senseNumber()
		{
			return k_iSenseNumber;
		}

		public double positiveScore()
		{
			return k_dPosScore;
		}

		public double negativeScore()
		{
			return k_dNegScore;
		}
	}


	public enum EPosType
	{
		eVerb,
		eAdjective,
		eAdverb,
		eNoun;


		public static EPosType fromString(final String strData)
		{
			switch(strData)
			{
			case "v":
				return eVerb;
			case "a":
				return eAdjective;
			case "r":
				return eAdverb;
			case "n":
				return eNoun;
			default:
				throw new IllegalArgumentException("Unknown POS-Type \"" + strData + "\"");
			}
		}
	}
}
