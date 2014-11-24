package com.springer.semantic.classifier.jetty.service;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.springer.semantic.utils.ConvertToSeq;


@Component
public class ConvertToSeqService {

	public void convertToSeq(String inputFileName, String outputDirName) throws IOException {
		ConvertToSeq convertToSeq = new ConvertToSeq(); 
		convertToSeq.convert(inputFileName, outputDirName);
	}

}