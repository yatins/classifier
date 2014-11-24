package com.springer.semantic.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.springer.semantic.classifier.model.Journal;
import com.springer.semantic.classifier.model.Journals;

public class ImportJournals {
	
	public void importJournals(Journals journals) throws FileNotFoundException {

		try {
			File file = new File("src/main/resources/com/springer/semantic/classifier/utisl/journals.tsv");
			FileReader reader = new FileReader(file);
			BufferedReader in = new BufferedReader(reader);
			String string;
			while ((string = in.readLine()) != null) {
				
				String [] data = string.split("\t");
				Journal journal = new Journal();
				journal.setId(data[0]);
				journal.setName(data[1]);
				journals.put(journal.getId(), journal);
			}
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
