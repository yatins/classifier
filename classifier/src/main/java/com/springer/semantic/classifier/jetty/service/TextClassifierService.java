package com.springer.semantic.classifier.jetty.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

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
import com.springer.semantic.classifier.model.impl.JournalScoreImpl;
import com.springer.semantic.classifier.model.Journals;
import com.springer.semantic.utils.ConvertNaicsToSeq;


@Component
public class TextClassifierService {

	public static Comparator<JournalScore> ScoreComparator =  new Comparator<JournalScore>() {

		public int compare(JournalScore journal1, JournalScore journal2) {

			if (journal1.getScoreVal() < journal2.getScoreVal()) return 1;
			if (journal1.getScoreVal() > journal2.getScoreVal()) return -1;
			return 0;
		}

	};

	@Value( "${modelPath}" )
	private String modelPath;

	@Value( "${labelIndexPath}" )
	private String labelIndexPath;

	@Value( "${dictionaryPath}" )
	private String dictionaryPath;

	@Value( "${documentFrequencyPath}" )
	private String documentFrequencyPath;	

	@Value("${name:World}")
	private String name;

	public String getForm() {

		String htmlStr = "<html>" + 
				"<head>" + 
				"<title>Machine Abstract Classifier</title>" + 
				"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + 
				"</head>" + 
				"<body>" + 
				"<form name=\"user\" action=\"classify\" method=\"post\">" +
				"<table width=\"100%\" cellspacing=\"2\" + callpadding=\"2\"  frame=\"box\">" + 
				"<tr><td halign=\"center\" colspan=\"2\" width=\"100%\"><h1>Mahout Abstract Classifier</h1></td></tr>" + 
				"<tr><td halign=\"left\" colspan=\"2\" width=\"100%\"><a href=\"/topwords\">View Top Words for each Journal</a></td></tr>" + 
				"<tr><td halign=\"left\" colspan=\"2\" width=\"100%\">Paste the text from an abstract into the box below then click the 'Classify' button (bottom left) to see the automatic classification</td></tr>" + 
				"<tr><td width=\"10%\">Number of results</td><td width=\"90%\"><select name=\"resultCount\">" + 
				" <option value=\"5\">5</option>" + 
				"  <option value=\"10\">10</option>" + 
				"  <option value=\"20\">20</option>" + 
				"  <option value=\"50\">50</option>" + 
				"</select></td></tr>" + 
				"<tr><td valign=\"top\">Abstract</td><td valign=\"top\" width=\"100%\"><textarea rows=\"30\" cols=\"100\" name=\"abstractVal\"></textarea></td></tr>" +
				"<tr><td halign=\"left\" colspan=\"2\" width=\"100%\"><input type=\"submit\" value=\"Classify\" /></td></tr>" + 
				"</table>" + 
				" </form>" + 
				"</body>" + 
				"</html>";
		return htmlStr;
	}

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

	public String classify(String abstractVal, int resultCount) {
		
		String retval = "";
		
		retval = retval + "<head>";
		retval = retval + "<title>Mahout Bayesian Classifier</title>";
		retval = retval + "</head>";
		
		ArrayList<JournalScore> journalScores = null;
		try {
			journalScores = this.performClassification(abstractVal, resultCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
		retval = retval + "<table cellspacing=\"2\" + callpadding=\"2\"  frame=\"box\">";
		//retval = retval + "<tr><td><b>Journal</b></td><td><b>Score</b></td><td><b>Bayesian Distance</b></td></tr>";
		retval = retval + "<tr><td><b>NAICs ID</b></td><td><b>Description</b></td><td><b>Score</b></td><td><b>Bayesian Distance</b></td></tr>";
			for (JournalScore journalScore:journalScores) {
			retval = retval + "<tr><td>" + journalScore.getJournalId() + "<?td><td>" +  Journals.getInstance().getJournalTitle(journalScore.getJournalId()) + "</td><td>" + journalScore.getPercentage() + "</td><td>" + journalScore.getBayesianDistance() + "</td></tr>";
		}
		retval = retval  + "</table>";
		return retval;

	}

	private ArrayList< JournalScore> performClassification(String textToClassify, int resultCount) throws IOException  {
		
		System.out.println(modelPath);
		System.out.println(labelIndexPath);
		System.out.println(dictionaryPath);
		System.out.println(documentFrequencyPath);

		ArrayList<JournalScore>  journalScores = new ArrayList<JournalScore>();

		Configuration configuration = new Configuration();

		//System.out.println("Model Path:" + modelPath);

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

		// extract words from tweet
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
		double minval = 0;

		String bestJournalID = "";
		for(Element element: resultVector.all()) {
			int categoryId = element.index();

			double score = element.get();
			if (score < minval) {
				minval = score;
			}		

			JournalScore journalScore = new JournalScoreImpl();
			journalScore.setJournalId(labels.get(categoryId));
			journalScore.setScoreVal(score);
			journalScore.setBayesianDistance(score);
			journalScores.add(journalScore);
			if (score > bestScore) {
				bestScore = score;
				bestCategoryId = categoryId;
				bestJournalID = journalScore.getJournalId();
			}			

		}

		analyzer.close();




		for (JournalScore journalScore:journalScores) {
			journalScore.setScoreVal(journalScore.getScoreVal() - minval);
		}		

		ArrayList<JournalScore>  topTenJournalScores = new ArrayList<JournalScore>();
		Collections.sort(journalScores, ScoreComparator);		


		/* Only look at the top ten */
		double totalVal = 0;
		int i=0;
		for (JournalScore journalScore:journalScores) {
			totalVal = totalVal + journalScore.getScoreVal();
			topTenJournalScores.add(journalScore);
			if (i++ >=resultCount) break;
		}
		//System.out.print("bestCategoryId  :" + bestJournalID + ":" + bestScore);
		//System.out.print("Number of journals:  :" + topTenJournalScores.size());
		this.setPercentages(totalVal, topTenJournalScores);


		return topTenJournalScores;

	}	

	private void setPercentages(double total, ArrayList<JournalScore> journalScores) {


		for (JournalScore journalScore:journalScores) {
			double percentage = (journalScore.getScoreVal() / total) * 100;
			journalScore.setPercentage(percentage);
		}

	}

}