package com.springer.semantic.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.springer.semantic.classifier.jetty.ClassifierApplication;
import com.springer.semantic.classifier.model.DataValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ClassifierApplication.class)
public class ConvertToSeqTest {


	@Test
	public void testReadTSV() {
		URL url = this.getClass().getResource("/test-train.tsv");
		ConvertToSeq convertToSeq = new ConvertToSeq();
		List<DataValue> dataVals = null;
		try {
			dataVals = convertToSeq.readTSV(url, ConvertToSeq.mapDataSet);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(19 ,dataVals.size());
	}

	@Test
	public void testReadTweetTSV() {
		URL url = this.getClass().getResource("/tweets-train.tsv");
		ConvertToSeq convertToSeq = new ConvertToSeq();
		List<DataValue> dataVals = null;
		try {
			dataVals = convertToSeq.readTSV(url, ConvertToSeq.mapTweetSet);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(539, dataVals.size());
	}

	@Test
	public void testConvertTSV() {
		URL url = this.getClass().getResource("/test-train.tsv");
		ConvertToSeq convertToSeq = new ConvertToSeq();
		int convertCount = 0;
		try {
			convertCount = convertToSeq.convert(url, "outtmp", ConvertToSeq.mapDataSet);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(18, convertCount);
	}

	@Test
	public void testConvertTweetsTSV() {
		URL url = this.getClass().getResource("/tweets-train.tsv");
		ConvertToSeq convertToSeq = new ConvertToSeq();
		int convertCount = 0;
		try {
			convertCount = convertToSeq.convert(url, "outtweet", ConvertToSeq.mapDataSet);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(538, convertCount);
	}
	
	@Test
	public void removeStopWordsTest() {
		
		String originalStr = "The quick brown fox jumped over 3.45 the lazy 12345 dog";
		List<String> stopWords = Arrays.asList("brown", "fox", "egg");
		ConvertToSeq convertToSeq = new ConvertToSeq();
		List<String> cleanStr = convertToSeq.tokenizePhrase(originalStr, stopWords);
		List<String> resultList = Arrays.asList("the", "quick", "jump", "over", "the", "lazi", "dog");
		assertEquals(resultList, cleanStr);
		
	}
	
	/*@Test
	public void testCleanTSV() {
		URL url = this.getClass().getResource("/BMCAll-train.tsv");
		ConvertToSeq convertToSeq = new ConvertToSeq();
		int convertCount = 0;
		try {
			convertCount = convertToSeq.clean(url, "BMCAll_train.clean.tsv", ConvertToSeq.mapDataSet);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(142578, convertCount);
	}*/	

}
