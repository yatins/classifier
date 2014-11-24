package com.springer.semantic.classifier.jetty.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.springer.semantic.classifier.core.Classifier;
import com.springer.semantic.classifier.model.Journal;
import com.springer.semantic.classifier.model.JournalScore;
import com.springer.semantic.classifier.model.Journals;
import com.springer.semantic.classifier.model.Word;

@Component
public class TopJournalWordsService {



	@Value( "${modelPath}" )
	private String modelPath;

	@Value( "${labelIndexPath}" )
	private String labelIndexPath;

	@Value( "${dictionaryPath}" )
	private String dictionaryPath;

	@Value( "${documentFrequencyPath}" )
	private String documentFrequencyPath;	
	
	@Value("${indexFile}")
	public static String indexFile;	
	
	private int labelCount;
	private int documentCount;



	public static Map<Integer, String> readInverseDictionnary(Configuration conf, Path dictionnaryPath) {
		Map<Integer, String> inverseDictionnary = new HashMap<Integer, String>();
		for (Pair<Text, IntWritable> pair : new SequenceFileIterable<Text, IntWritable>(dictionnaryPath, true, conf)) {
			inverseDictionnary.put(pair.getSecond().get(), pair.getFirst().toString());
		}
		return inverseDictionnary;
	}

	public static Map<Integer, Long> readDocumentFrequency(Configuration conf, Path documentFrequencyPath) {
		Map<Integer, Long> documentFrequency = new HashMap<Integer, Long>();
		for (Pair<IntWritable, LongWritable> pair : new SequenceFileIterable<IntWritable, LongWritable>(documentFrequencyPath, true, conf)) {
			documentFrequency.put(pair.getFirst().get(), pair.getSecond().get());
		}
		return documentFrequency;
	}

	public static Map<Integer, Long> getTopWords(Map<Integer, Long> documentFrequency, int topWordsCount) {
		List<Map.Entry<Integer, Long>> entries = new ArrayList<Map.Entry<Integer, Long>>(documentFrequency.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Integer, Long>>() {
			@Override
			public int compare(Entry<Integer, Long> e1, Entry<Integer, Long> e2) {
				return -e1.getValue().compareTo(e2.getValue());
			}
		});

		Map<Integer, Long> topWords = new HashMap<Integer, Long>();
		int i = 0;
		for(Map.Entry<Integer, Long> entry: entries) {
			topWords.put(entry.getKey(), entry.getValue());
			i++;
			if (i > topWordsCount) {
				break;
			}
		}
		return topWords;
	}

	public static class WordWeight implements Comparable<WordWeight> {
		private int wordId;
		private double weight;

		public WordWeight(int wordId, double weight) {
			this.wordId = wordId;
			this.weight = weight;
		}

		public int getWordId() {
			return wordId;
		}

		public Double getWeight() {
			return weight;
		}

		@Override
		public int compareTo(WordWeight w) {
			return -getWeight().compareTo(w.getWeight());
		}
	}

	public String listTopWords(int wordNum) {

		int colCount=4;

		String retval = "";

		retval = retval + "<head>";
		retval = retval + "<title>Top Words by Journal</title>";
		retval = retval + "</head>";

		Journals journals = null;
		try {
			journals = this.topJournalWords(wordNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
		retval = retval + "<table cellspacing=\"2\" + callpadding=\"2\"  frame=\"box\">";
		retval = retval + "<tr><td colspan=\"" + colCount + "\"><h2>Top words for each Category. Total of " + this.labelCount + " Categories. Number of texts in training set:" + this.documentCount + "</h2></td></tr>"; 
		Set keys = journals.keySet();
		int colcounter=1;
		retval = retval + "<tr>";
		int sizeval = journals.size();
		int counter=0;
		for (Object key:keys) {
			Journal journal = journals.get(key);
			if (journal.getId() == "0000") continue;
			counter++;
			retval = retval + "<td valign=\"top\">";
			retval = retval + getJournalData(journal);
			retval = retval + "</td>";

			if (colcounter++ > colCount ) {
				if (counter == sizeval) {
					retval = retval + "<tr>";
				} else {
					retval = retval + "</tr><tr>";
					colcounter=1;
				}
			} 
		}
		retval = retval  + "</table>";
		return retval;

	}

	private String getJournalData(Journal journal) {
		String retval = "<table cellspacing=\"2\" + callpadding=\"2\"  frame=\"box\" width=\"100%\" height=\"100%\">";
		retval = retval + "<tr bgcolor=\"#5C8576\"  color=\"#000000\"><td>" +  journal.getId() + "</td><td>" + Journals.getInstance().getJournalTitle(journal.getId()) + "</td></tr>";
		List<Word> words = journal.getWords();
		//System.out.println("For " + journal.getId() + " Number of words:" + words.size());
		Collections.sort(words, Word.WordComparator);
		for (Word word:words) {
			if (word != null &&  word.getWord() != null && word.getWeight() > 0) {
				retval = retval + "<tr><td>" +  word.getWord() + "</td><td>" + word.getWeight() + "</td></tr>";
			}
		}
		retval = retval  + "</table>";	
		return retval;
	}

	public Journals topJournalWords (int wordNum) throws IOException  {

		Journals journals = new Journals();

		Configuration configuration = new Configuration();

		// model is a matrix (wordId, labelId) => probability score
		NaiveBayesModel model = NaiveBayesModel.materialize(new Path(modelPath), configuration);

		// labels is a map label => classId
		Map<Integer, String> labels = BayesUtils.readLabelIndex(configuration, new Path(labelIndexPath));
		Map<Integer, String> inverseDictionary = readInverseDictionnary(configuration, new Path(dictionaryPath));
		Map<Integer, Long> documentFrequency = readDocumentFrequency(configuration, new Path(documentFrequencyPath));
		Journal journalAll = new Journal();
		journalAll.setId("0000");	
		journalAll.setName("Top Words Overall");
		List<Word> wordsAll = new ArrayList<Word>();
		Map<Integer, Long> topWords = getTopWords(documentFrequency, wordNum);
		//System.out.println("Top words");
		for(Map.Entry<Integer, Long> entry: topWords.entrySet()) {
			Word word = new Word();
			word.setWord(inverseDictionary.get(entry.getKey()));
			word.setWeight(entry.getValue());
			wordsAll.add(word);			
			//System.out.println(" - " + inverseDictionary.get(entry.getKey()) + ": " + entry.getValue());
		}
		journalAll.setWords(wordsAll);
		journals.put(journalAll.getId(), journalAll);

		labelCount = labels.size();
		documentCount = documentFrequency.get(-1).intValue();

		//System.out.println("Number of labels: " + labelCount);
		//System.out.println("Number of documents in training set: " + documentCount);

		for(int labelId = 0 ; labelId < model.numLabels() ; labelId++) {
			SortedSet<WordWeight> wordWeights = new TreeSet<WordWeight>();
			for(int wordId = 0 ; wordId < model.numFeatures() ; wordId++) {
				WordWeight w = new WordWeight(wordId, model.weight(labelId, wordId));
				wordWeights.add(w);
			}
			//System.out.println("Top " + wordNum + " words for label " + labels.get(labelId));
			String journalID = labels.get(labelId);
			Journal journal = new Journal();
			journal.setId(journalID);	
			//journal.setName(Journals.getInstance().getJournalTitle(journalID));
			int i = 0;
			List<Word> words = new ArrayList<Word>();
			for(WordWeight w: wordWeights) {
				Word word = new Word();
				word.setWord(inverseDictionary.get(w.getWordId()));
				word.setWeight(w.getWeight());
				words.add(word);
				//System.out.println(" - " + inverseDictionary.get(w.getWordId())	+ ": " + w.getWeight());
				//System.out.println("Adding:" +word.getWord() + ":" + word.getWeight());
				i++;
				if (i >= wordNum) {
					break;
				}
			}
			journal.setWords(words);
			journals.put(journal.getId(), journal);
		}
		return journals;
	}
	

	public static void main(String[] args) throws Exception {
		if (args.length < 5) {
			System.out.println("Arguments: [model] [label index] [dictionnary] [document frequency] [number of words]");
			return;
		}
		TopJournalWordsService topJournalWordsService = new TopJournalWordsService();
		topJournalWordsService.modelPath = args[0];
		topJournalWordsService.labelIndexPath = args[1];
		topJournalWordsService.dictionaryPath = args[2];
		topJournalWordsService.documentFrequencyPath = args[3];

		int numWords=100;
		if (args.length > 4 && args[4] != null)numWords=Integer.parseInt(args[4]);
		
		String journalName = "";
		if (args.length > 5 && args[5] != null) journalName = args[5];

		Journals journals = null;
		try {
			journals = topJournalWordsService.topJournalWords(numWords);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set keys = journals.keySet();
		int sizeval = journals.size();
		int counter=0;
		System.out.println("Checking for Journal:" + journalName);
		for (Object key:keys) {
			counter++;
			Journal journal = journals.get(key);
			if (journalName != null && !journalName.equals("")) {
				if (!journal.getId().replace("\"", "").toLowerCase().trim().equals(journalName.toLowerCase().trim())) continue;
			}
			List<Word> words = journal.getWords();
			Collections.sort(words, Word.WordComparator);
			for (Word word:words) {
				if (word != null &&  word.getWord() != null) {
					System.out.println(word.getWord() + ":" + word.getWeight());
				}
			}			
		}

		
	}	
}

