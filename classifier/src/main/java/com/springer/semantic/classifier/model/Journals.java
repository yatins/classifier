package com.springer.semantic.classifier.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;

import com.springer.semantic.classifier.core.Classifier;

public class Journals extends TreeMap<String, Journal> {
	
	public static String indexFile = "2012_NAICS_Index_File.csv";

	private static final long serialVersionUID = 1L;
	private static volatile Journals instance = null;
	
	public static Journals getInstance() {
		if (instance == null) {
			synchronized (Journals.class) {
				if (instance == null) {
					instance = new Journals();
					try {
						instance.importJournals();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (instance.size()==0) {
						try {
							instance.importJournalsLocal();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return instance;
	}


	public void importJournals() throws FileNotFoundException {
		try {
			File inputFile = new File(indexFile);
			FileReader reader = new FileReader(inputFile);
			BufferedReader in = new BufferedReader(reader);
			String string;
			while ((string = in.readLine()) != null) {
				try {
				String [] data = string.split("\t");
				Journal journal = new Journal();
				journal.setId(data[0]);
				journal.setName(data[1]);
				this.put(journal.getId(), journal);
				} catch(Exception e){}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void importJournalsLocal() throws FileNotFoundException {

		try {
			File inputFile = new File(Classifier.indexFile);
			FileReader reader = new FileReader(inputFile);
			BufferedReader in = new BufferedReader(reader);
			String string;
			while ((string = in.readLine()) != null) {

				String [] data = string.split("\t");
				Journal journal = new Journal();
				journal.setId(data[0]);
				journal.setName(data[1]);
				//System.out.println("key:" + journal.getId() + ":" + journal.getName());
				this.put(journal.getId(), journal);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	
	
	public String getJournalTitle(String journalID) {
		if (this.containsKey(journalID)) {
			return this.get(journalID).getName();
		}
		return "";
	}

}


