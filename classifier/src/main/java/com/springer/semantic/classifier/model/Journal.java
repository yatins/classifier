package com.springer.semantic.classifier.model;

import java.util.ArrayList;
import java.util.List;

public class Journal {
	

	
	private String id;
	private String name;
	private List<Word> words = new ArrayList<Word>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}

}
