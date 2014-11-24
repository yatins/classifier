package com.springer.semantic.lucene.analyzer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class AnalyzerHarness {

	/*private static String testString = 	"public class HelloWorldApp { " +
										"public static void main(String[] args) { " + 
										"System.out.println(\"The rain in Spain falls mainly on the plain!\"); }";*/
	
	private static String testSysonymString = 	"I will get the flu or grippe soon";	


	public static void main(String[] args) {

		JavaSourceCodeAnalyzer jcl = new 		JavaSourceCodeAnalyzer();
		TokenStream ts;
		try {
			ts = jcl.tokenStream("text", new StringReader(testSysonymString));

			CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			int wordCount = 0;
			while (ts.incrementToken()) {
				if (termAtt.length() > 0) {
					String word = ts.getAttribute(CharTermAttribute.class).toString();
					System.out.println(word);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
