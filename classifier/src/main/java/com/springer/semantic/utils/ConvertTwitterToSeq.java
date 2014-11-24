package com.springer.semantic.utils;

import java.io.BufferedReader;

/**
 * Convert the training set to Hadoop sequence file format
 */
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;


public class ConvertTwitterToSeq {

	static final List<String> stopWords = Arrays.asList(
			"a", "an", "and", "are", "as", "at", "be", "but", "by",
			"for", "if", "in", "into", "is", "it",
			"no", "not", "of", "on", "or", "such",
			"that", "the", "their", "then", "there", "these",
			"they", "this", "to", "was", "will", "with", "we", "were", "has", "between", "from", "using", "have", "we", "which", "null", "most", "one", "more", "1", "p", 
			"used", "compared", "can", "two", "high", "than", "other", "our", "well", "been", "during", "found", "both", 
			"from",  "have", "associated", "may", "between", "time", "all", "however", "after", "background", "study",
			"also", "had", "using", "g", "its", "under", "important", "significant", "based", "methods", "results", "studies", "use",
			"non", "expression", "different", "significantly", "only", "null", "batch", "growth", "type", "levels", "increased",
			"subjects", "rating", "total", "scores", "scale", "areas", "site", "set","sets", "activity", "left", "right", "vs",
			"nm", "among", "who", "underwent", "scores", "items", "self", "expressed", "expression", "us", "per", "costs", "based", "va",
			"show", "within", "qtl", "snp", "recurrence", "showed", "case", "cases",
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", 
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"type", "here", "further", "could", "would", "sec", "st", "data", "identified", "years", "specific", "related", "including", "first", "three",
			"higher", "analysis", "present", "number", "production", "ci", "0,0", "recent", "ra", "anti", 
			"3d","7bit","a0","about","above","abstract","across","additional","after", "conclusions",
			"afterwards","again","against","align","all","almost","alone","along",
			"already","also","although","always","am","among","amongst","amoungst",
			"amount","an","and","another","any","anybody","anyhow","anyone","anything",
			"anyway","anywhere","are","arial","around","as","ascii","assert","at",
			"back","background","base64","bcc","be","became","because","become","becomes",
			"becoming","been","before","beforehand","behind","being","below","beside",
			"besides","between","beyond","bgcolor","blank","blockquote","body","boolean",
			"border","both","br","break","but","by","can","cannot","cant","case","catch",
			"cc","cellpadding","cellspacing","center","char","charset","cheers","class",
			"co","color","colspan","com","con","const","continue","could","couldnt",
			"cry","css","de","dear","default","did","didnt","different","div","do",
			"does","doesnt","done","dont","double","down","due","during","each","eg",
			"eight","either","else","elsewhere","empty","encoding","enough","enum",
			"etc","eu","even","ever","every","everyone","everything","everywhere",
			"except","extends","face","family","few","ffffff","final","finally","float",
			"font","for","former","formerly","fri","from","further","get","give","go",
			"good","got","goto","gt","h1","ha","had","has","hasnt","have","he","head",
			"height","hello","helvetica","hence","her","here","hereafter","hereby",
			"herein","hereupon","hers","herself","hi","him","himself","his","how",
			"however","hr","href","html","http","https","id","ie","if","ill","im",
			"image","img","implements","import","in","inc","instanceof","int","interface",
			"into","is","isnt","iso-8859-1","it","its","itself","ive","just","keep",
			"last","latter","latterly","least","left","less","li","like","long","look",
			"lt","ltd","mail","mailto","many","margin","may","me","meanwhile","message",
			"meta","might","mill","mine","mon","more","moreover","most","mostly","mshtml",
			"mso","much","must","my","myself","name","namely","native","nbsp","need",
			"neither","never","nevertheless","new","next","nine","no","nobody","none",
			"noone","nor","not","nothing","now","nowhere","null","of","off","often",
			"ok","on","once","only","onto","or","org","other","others","otherwise",
			"our","ours","ourselves","out","over","own","package","pad","per","perhaps",
			"plain","please","pm","printable","private","protected","public","put",
			"quot","quote","r1","r2","rather","re","really","regards","reply","return",
			"right","said","same","sans","sat","say","saying","see","seem","seemed",
			"seeming","seems","serif","serious","several","she","short","should","show",
			"side","since","sincere","six","sixty","size","so","solid","some","somehow",
			"someone","something","sometime","sometimes","somewhere","span","src",
			"static","still","strictfp","string","strong","style","stylesheet","subject",
			"such","sun","super","sure","switch","synchronized","table","take","target",
			"td","text","th","than","thanks","that","the","their","them","themselves",
			"then","thence","there","thereafter","thereby","therefore","therein","thereupon",
			"these","they","thick","thin","think","third","this","those","though",
			"three","through","throughout","throw","throws","thru","thu","thus","tm",
			"to","together","too","top","toward","towards","tr","transfer","transient",
			"try","tue","type","ul","un","under","unsubscribe","until","up","upon",
			"us","use","used","uses","using","valign","verdana","very","via","void",
			"volatile","want","was","we","wed","weight","well","were","what","whatever",
			"when","whence","whenever","where","whereafter","whereas","whereby","wherein",
			"whereupon","wherever","whether","which","while","whither","who","whoever",
			"whole","whom","whose","why","width","will","with","within","without",
			"wont","would","wrote","www","yes","yet","you","your","yours","yourself",
			"yourselves", "lc", "il", "response", "method", "throughput", "developed",
			"allows", "system", "gy", "months", "quality", "given", "available",
			"low", "observed", "disease", "potential",
			"95", "art", "mm3", "df"

			);

	static final CharArraySet stopSet = new CharArraySet(Version.LUCENE_4_9, stopWords, false);	
	
	
	public int performConversion(String inputFileName, String outputDirName) throws IOException {
		
		Configuration configuration = new Configuration();
		FileSystem fs = FileSystem.get(configuration);
		Writer writer = new SequenceFile.Writer(fs, configuration, new Path(outputDirName + "/chunk-0"),
				Text.class, Text.class);

		int count = 0;
		BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
		Text key = new Text();
		Text value = new Text();
		while(true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			String[] tokens = line.split("\t", 2);
			if (tokens.length != 2) {
				System.out.println("Skip line: " + line);
				continue;
			}
			
			String category = tokens[1];
			String id = String.format("%06d", count);
			String message = tokens[0];
			if (message==null || id==null || category==null) {
				continue;
			}
			message = removeStopWords(message);
			System.out.println("id " + id + "category:" + category + " message:" + message);
			key.set("/" + category + "/" + id);
			value.set(message);
			writer.append(key, value);
			count++;
		}
		reader.close();
		writer.close();
		System.out.println("Wrote " + count + " entries.");
		return count;
		
	}

	public static void main(String args[]) throws Exception {
		if (args.length != 2) {
			System.err.println("Arguments: [input tsv file] [output sequence file]");
			return;
		}
		String inputFileName = args[0];
		String outputDirName = args[1];
		
		ConvertTwitterToSeq convertToSeq = new ConvertTwitterToSeq();
		
		convertToSeq.performConversion(inputFileName, outputDirName);
	}

	public String removeStopWords(String stringVal) throws IOException {
		String retval = "";
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9, stopSet);
		TokenStream ts = analyzer.tokenStream("text", new StringReader(stringVal));
		CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		int wordCount = 0;
		while (ts.incrementToken()) {
			if (termAtt.length() > 0) {
				String word = ts.getAttribute(CharTermAttribute.class).toString();
				retval = retval + " " + word;
			}
		}
		return retval;
	}
}




