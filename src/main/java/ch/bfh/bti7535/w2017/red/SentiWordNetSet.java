package ch.bfh.bti7535.w2017.red;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.bfh.bti7535.utilities.FileUtilities;
import ch.bfh.bti7535.utilities.Tuple;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;


public final class SentiWordNetSet
{
	private final List<Synset> k_lstSynsets;
	private final Map<Tuple<EPosType, String>, List<SynsetTerm>> k_mapString2Synset;
	private Dictionary m_xDictionary;

	private SentiWordNetSet(final List<Synset> lstSynsets)
	{
		k_lstSynsets = lstSynsets;
		try
		{
			m_xDictionary = Dictionary.getDefaultResourceInstance();
		}
		catch(final JWNLException xException)
		{
			m_xDictionary = null;
			xException.printStackTrace();
		}

		k_mapString2Synset = new HashMap<>();
		for(final Synset xSet : k_lstSynsets)
		{
			for(final SynsetTerm xTerm : xSet.terms())
			{
				final String strName = xTerm.name();
				final EPosType xType = xTerm.type();
				final Tuple<EPosType, String> xKey = new Tuple<>(xType, strName);
				if(!k_mapString2Synset.containsKey(xKey))
				{
					k_mapString2Synset.put(xKey, new ArrayList<>());
				}
				k_mapString2Synset.get(xKey).add(xTerm);
			}
		}
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
				final List<SynsetTerm> lstTerms = new ArrayList<>();
				for(final String strTerm : strStuff[4].split("\\s"))
				{
					final int iHashIndex = strTerm.indexOf('#');
					final String strName = strTerm.substring(0, iHashIndex);
					final int iSenseNumber = Integer.parseInt(strTerm.substring(iHashIndex + 1));
					lstTerms.add(new SynsetTerm(strName, iSenseNumber));
				}
				final String strGlossary = strStuff[5];
				final Synset xSet = new Synset(eType, iId, dPosScore, dNegScore, lstTerms, strGlossary);
				xBuilder.addSynset(xSet);
			}
		});

		return xBuilder.build();
	}

	public List<SynsetTerm> lookupWord(final EPosType eType, final String strWord)
	{
		String strLemma = strWord;
		try
		{
			final IndexWord xWord = m_xDictionary.getIndexWord(eType.convert(), strWord);
			if(xWord != null)
			{
				strLemma = xWord.getLemma();
			}
		}
		catch(final JWNLException xException)
		{
			xException.printStackTrace();
		}

		final Tuple<EPosType, String> xKey = new Tuple<>(eType, strLemma);
		final List<SynsetTerm> lstResult = k_mapString2Synset.get(xKey);
		return (lstResult == null) ? new ArrayList<>() : lstResult;
	}

	public List<SynsetTerm> lookupAllWords(final String strWord)
	{
		final List<SynsetTerm> lstTerms = new ArrayList<>();
		for(final EPosType eType : EPosType.values())
		{
			lstTerms.addAll(lookupWord(eType, strWord));
		}
		return lstTerms;
	}

	private static class Builder
	{
		private final List<Synset> m_lstSynsets = new ArrayList<>();

		public Builder addSynset(final Synset xSet)
		{
			m_lstSynsets.add(xSet);
			return this;
		}

		public SentiWordNetSet build()
		{
			return new SentiWordNetSet(m_lstSynsets);
		}
	}


	public static class Synset
	{
		private final EPosType k_eType;
		private final int k_iId;
		private final double k_dPosScore;
		private final double k_dNegScore;
		private final List<SynsetTerm> k_lstTerms;
		private final String k_strGlossary;

		public Synset(final EPosType eType, final int iId, final double dPosScore, final double dNegScore, final List<SynsetTerm> lstTerms, final String strGlossary)
		{
			k_eType = eType;
			k_iId = iId;
			k_dPosScore = dPosScore;
			k_dNegScore = dNegScore;
			k_lstTerms = lstTerms;
			k_strGlossary = strGlossary;

			for(final SynsetTerm xTerm : lstTerms)
			{
				xTerm.setSynset(this);
			}
		}

		public List<SynsetTerm> terms()
		{
			return k_lstTerms;
		}

		public EPosType type()
		{
			return k_eType;
		}

		public int id()
		{
			return k_iId;
		}

		public double positiveScore()
		{
			return k_dPosScore;
		}

		public double negativeScore()
		{
			return k_dNegScore;
		}

		public String glossary()
		{
			return k_strGlossary;
		}
	}


	public static class SynsetTerm
	{
		private final String k_strName;
		private final int k_iSenseNumber;
		private Synset m_xSet;

		public SynsetTerm(final String strName, final int iSenseNumber)
		{
			k_strName = strName;
			k_iSenseNumber = iSenseNumber;
		}

		public void setSynset(final Synset xSet)
		{
			m_xSet = xSet;
		}

		public String name()
		{
			return k_strName;
		}

		public EPosType type()
		{
			return m_xSet.type();
		}

		public int id()
		{
			return m_xSet.id();
		}

		public double positiveScore()
		{
			return m_xSet.positiveScore();
		}

		public double negativeScore()
		{
			return m_xSet.negativeScore();
		}

		public Synset synset()
		{
			return m_xSet;
		}

		public int senseNumber()
		{
			return k_iSenseNumber;
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

		public POS convert()
		{
			switch(this)
			{
			case eVerb:
				return POS.VERB;
			case eAdjective:
				return POS.ADJECTIVE;
			case eAdverb:
				return POS.ADVERB;
			case eNoun:
				return POS.NOUN;
			default:
				return null;
			}
		}
	}
}
