package com.springer.semantic.classifier.model;

import java.util.Comparator;

public class Word  {
	
	public static Comparator<Word> WordComparator =  new Comparator<Word>() {

		public int compare(Word word1, Word word2) {

			if (word1.getWeight() < word2.getWeight()) return 1;
			if (word1.getWeight() > word2.getWeight()) return -1;
			return 0;
		}

	};	
	
	private String word;
	private double weight;
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double value) {
		this.weight = value;
	}


}
