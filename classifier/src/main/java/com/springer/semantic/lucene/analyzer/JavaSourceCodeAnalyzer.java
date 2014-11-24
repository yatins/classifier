package com.springer.semantic.lucene.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.Version;

public class JavaSourceCodeAnalyzer extends Analyzer {

	private Set<Object> javaStopSet;
	private Set<Object> englishStopSet;
	private SynonymMap synonyms;

	private static final String[] JAVA_STOP_WORDS = {
		"public","private","protected","interface",
		"abstract","implements","extends","null", "new",
		"switch","case", "default" ,"synchronized" ,
		"do", "if", "else", "break","continue","this",
		"assert" ,"for","instanceof", "transient",
		"final", "static" ,"void","catch","try",
		"throws","throw","class", "finally","return",
		"const" , "native", "super","while", "import",
		"package" ,"true", "false" };

	private static final String[] ENGLISH_STOP_WORDS ={
		"a", "an", "and", "are","as","at","be", "but",
		"by", "for", "if", "in", "into", "is", "it",
		"no", "not", "of", "on", "or", "s", "such",
		"that", "the", "their", "then", "there","these",
		"they", "this", "to", "was", "with" };

	public JavaSourceCodeAnalyzer(){
		super();
		javaStopSet = StopFilter.makeStopSet(Version.LUCENE_4_9, JAVA_STOP_WORDS);
		englishStopSet = StopFilter.makeStopSet(Version.LUCENE_4_9, ENGLISH_STOP_WORDS);
		try {
			this.setSynonyms();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setSynonyms() throws IOException {

		String base1 = "influenza";
		String syn11 = "flu";
		String syn12 = "grippe";
		String base2 = "to be";
		String syn21 = "am";
		String syn22 = "is";
		String syn23 = "was";
		String syn24 = "will";

		SynonymMap.Builder sb = new SynonymMap.Builder(true);
		sb.add(new CharsRef(base1), new CharsRef(syn11), true);
		sb.add(new CharsRef(base1), new CharsRef(syn12), true);
		sb.add(new CharsRef(base2), new CharsRef(syn21), true);
		sb.add(new CharsRef(base2), new CharsRef(syn22), true);
		sb.add(new CharsRef(base2), new CharsRef(syn23), true);
		sb.add(new CharsRef(base2), new CharsRef(syn24), true);
		synonyms = sb.build(); 		
		synonyms.fst.toString();

		System.out.println(synonyms.fst.getArcCount());
		
		String synFile = "C:\\Innovation\\Lucene\\HackDaysynonymMap.txt";
		
		//CharsRef analyze = SynonymMap.Parser.analyze(synFile, null);


		/*
		for (int i = 0; i < words.length; i++) {
			String[] synonyms = synonyms.getSynonyms(words[i]);
			System.out.println(words[i] + ":" + java.util.Arrays.asList(synonyms).toString());
		}*/		

	}

	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		final Tokenizer source = new LowerCaseTokenizer(Version.LUCENE_4_9, reader);
		//TokenStream result = new PorterStemFilter(source);
		//result = new StopFilter(Version.LUCENE_4_9, result, (CharArraySet) javaStopSet);
		//result = new StopFilter(Version.LUCENE_4_9, result, (CharArraySet) englishStopSet);
		TokenStream result = new SynonymFilter(source, synonyms, false);
		return new TokenStreamComponents(source, result);
	}

}