package com.springer.semantic.classifier.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.vectorizer.TFIDF;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.springer.semantic.classifier.model.JournalScore;
import com.springer.semantic.classifier.model.Journals;
import com.springer.semantic.classifier.model.impl.JournalScoreImpl;
import com.springer.semantic.utils.ConvertNaicsToSeq;


@Component
public class Classifier {
	

	@Value("${modelPath}")
	public static String modelPath;

	@Value("${labelIndexPath}")
	public static String labelIndexPath;

	@Value("${dictionaryPath}")
	public static String dictionaryPath;

	@Value("${documentFrequencyPath}")
	public static String documentFrequencyPath;	
	
	@Value("${indexFile}")
	public static String indexFile;
	
	public static Comparator<JournalScore> ScoreComparator =  new Comparator<JournalScore>() {

		public int compare(JournalScore journal1, JournalScore journal2) {

			if (journal1.getScoreVal() < journal2.getScoreVal()) return 1;
			if (journal1.getScoreVal() > journal2.getScoreVal()) return -1;
			return 0;
		}

	};	
	
	public static Map<String, Integer> readDictionnary(Configuration conf, Path dictionnaryPath) {
		Map<String, Integer> dictionnary = new HashMap<String, Integer>();
		for (Pair<Text, IntWritable> pair : new SequenceFileIterable<Text, IntWritable>(dictionnaryPath, true, conf)) {
			dictionnary.put(pair.getFirst().toString(), pair.getSecond().get());
		}
		return dictionnary;
	}

	public static Map<Integer, Long> readDocumentFrequency(Configuration conf, Path documentFrequencyPath) {
		Map<Integer, Long> documentFrequency = new HashMap<Integer, Long>();
		for (Pair<IntWritable, LongWritable> pair : new SequenceFileIterable<IntWritable, LongWritable>(documentFrequencyPath, true, conf)) {
			documentFrequency.put(pair.getFirst().get(), pair.getSecond().get());
		}
		return documentFrequency;
	}

	public Set<JournalScore> classify(String textToClassify) throws IOException  {


		Set<JournalScore>  journalScores = new TreeSet<JournalScore>();

		Configuration configuration = new Configuration();

		// model is a matrix (wordId, labelId) => probability score
		NaiveBayesModel model = NaiveBayesModel.materialize(new Path(modelPath), configuration);

		StandardNaiveBayesClassifier classifier = new StandardNaiveBayesClassifier(model);

		// labels is a map label => classId
		Map<Integer, String> labels = BayesUtils.readLabelIndex(configuration, new Path(labelIndexPath));
		Map<String, Integer> dictionary = readDictionnary(configuration, new Path(dictionaryPath));
		Map<Integer, Long> documentFrequency = readDocumentFrequency(configuration, new Path(documentFrequencyPath));


		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9, ConvertNaicsToSeq.stopSet);

		int labelCount = labels.size();
		int documentCount = documentFrequency.get(-1).intValue();

		Multiset<String> words = ConcurrentHashMultiset.create();

		// extract words
		TokenStream ts = analyzer.tokenStream("text", new StringReader(textToClassify));
		CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		int wordCount = 0;
		while (ts.incrementToken()) {
			if (termAtt.length() > 0) {
				String word = ts.getAttribute(CharTermAttribute.class).toString();
				Integer wordId = dictionary.get(word);
				// if the word is not in the dictionary, skip it
				if (wordId != null) {
					words.add(word);
					wordCount++;
				}
			}
		}

		// create vector wordId => weight using tfidf
		Vector vector = new RandomAccessSparseVector(10000);
		TFIDF tfidf = new TFIDF();
		for (Multiset.Entry<String> entry:words.entrySet()) {
			String word = entry.getElement();
			int count = entry.getCount();
			Integer wordId = dictionary.get(word);
			Long freq = documentFrequency.get(wordId);
			double tfIdfValue = tfidf.calculate(count, freq.intValue(), wordCount, documentCount);
			vector.setQuick(wordId, tfIdfValue);
		}
		// With the classifier, we get one score for each label 
		// The label with the highest score is the one the tweet is more likely to
		// be associated to
		Vector resultVector = classifier.classifyFull(vector);
		double bestScore = -Double.MAX_VALUE;
		int bestCategoryId = -1;
		for(Element element: resultVector.all()) {
			int categoryId = element.index();
			double score = element.get();
			if (score > bestScore) {
				bestScore = score;
				bestCategoryId = categoryId;
			}
			//System.out.print("  " + labels.get(categoryId) + ": " + score);
			JournalScore journalScore = new JournalScoreImpl();
			journalScore.setJournalId(labels.get(categoryId));
			journalScore.setScoreVal(score);
			journalScores.add(journalScore);
		}
		analyzer.close();

		return journalScores;

	}

	public static void main(String[] args) throws Exception {
		if (args.length < 5) {
			System.out.println("Arguments: [model] [label index] [dictionnary] [document frequency] [test file]");
			return;
		}
		modelPath = args[0];
		labelIndexPath = args[1];
		dictionaryPath = args[2];
		documentFrequencyPath = args[3];
		String testFilePath = args[4];	
		File testFile = new File(testFilePath);
		BufferedReader reader = null;
		String textToClassify = "";

		try {
		    reader = new BufferedReader(new FileReader(testFile));
		    String text = null;

		    while ((text = reader.readLine()) != null) {
		    	textToClassify += text;
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}		
		
		Classifier classifier = new Classifier();
		Set<JournalScore> results = classifier.classify(textToClassify);
		
		//Collections.sort(results, ScoreComparator);	
		
		//Journals journals = Journals.getInstance();
		
		for (JournalScore score :results) {
			System.out.println("Journal :" + score.getJournalId() + ":" + score.getBayesianDistance() + ":" + score.getScoreVal());
		}
		
	}
	
}
