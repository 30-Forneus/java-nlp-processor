package com.forneus.interview.resolvit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.forneus.interview.resolvit.Result;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class LemmaTest {
	
	private static String[] COMMON_LEMMAS= {"a", "the", "and", "of", "in", "be", "also", "as", "O"};

	public static void main(String[] args) {
        try{
        	        	
            // test sentence
//            String[] tokens = new String[]{"Most", "large", "cities", "in", "the", "US", "had",
//                    "morning", "and", "afternoon", "newspapers", "."};
			
			Path filePath = new File("someText.txt").toPath();        
			String text = new String(Files.readAllBytes(filePath));
			
			String[] sentences = sentenceDetection(text);
			
			Map<String, Result> classifiedResults = new HashMap<String, Result>();
			
			for (int i = 0; i < sentences.length; i++) {
				
				String sentence = sentences[i];				
				String[] tokens = tokenSeparation(sentence);
				String[] nt = normalizeCase(tokens);
				String[] tags = tagging(nt);
				String[] lemmas = lemmatizing(nt, tags);

				for (int j = 0; j < tokens.length; j++) {
					classify(i, tokens[j], lemmas[j], classifiedResults);
				}

			}
 
			removeCommonWords(classifiedResults);
			List<Result> results = flat(classifiedResults);
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer().withRootName("results");
	        System.out.println(writer.writeValueAsString(results));
			
 
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	private static String[] normalizeCase(String[]tokens) {
		String[] normalizedTokens = new String[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			normalizedTokens[i] = (isAllUpperCase(token))? token.toLowerCase() : token;			
		}
		return normalizedTokens;
	}
	
	private static boolean isAllUpperCase(String str){
		for(int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			if((!Character.isLetter(c)) || Character.isLowerCase(c)) {
				return false;
			}
		}
		//str.charAt(index)
		return true;
	}
	private static void classify(int sentenceIndex, String token, String lemma, Map<String, Result> classifiedResults) {
		Result r = classifiedResults.get(lemma);
		if(r == null) {
			r = new Result(token, lemma);
			classifiedResults.put(lemma, r);
		}
		r.set(sentenceIndex);
	}

	private static void removeCommonWords(Map<String, Result> classifiedResults) {
	    for(Iterator<Map.Entry<String, Result>> it = classifiedResults.entrySet().iterator(); it.hasNext(); ) {
	        if(isCommonLemma(it.next().getKey())) {
	        	it.remove();	        	
	        }
	    }
	    return;
	}

	private static boolean isCommonLemma(String key) {
		for (int i = 0; i < COMMON_LEMMAS.length; i++) {
			if (key.equals(COMMON_LEMMAS[i])) {
				return true;
			} 
		}      		
		return false;
	}

	private static String[] lemmatizing(String[] tokens, String[] tags) throws FileNotFoundException, IOException {
		// loading the dictionary to input stream
		InputStream dictLemmatizer = new FileInputStream("en-lemmatizer.dict");
		// loading the lemmatizer with dictionary
		DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
 
		// finding the lemmas
		String[] lemmas = lemmatizer.lemmatize(tokens, tags);
		return lemmas;
	}

	private static String[] tagging(String[] tokens) throws FileNotFoundException, IOException {
		// Parts-Of-Speech Tagging
		// reading parts-of-speech model to a stream
		InputStream posModelIn = new FileInputStream("en-pos-maxent.bin");
		// loading the parts-of-speech model from stream
		POSModel posModel = new POSModel(posModelIn);
		// initializing the parts-of-speech tagger with model
		POSTaggerME posTagger = new POSTaggerME(posModel);
		// Tagger tagging the tokens
		String tags[] = posTagger.tag(tokens);
		posModelIn.close();
		return tags;
	}

	private static String[] tokenSeparation(String text) throws FileNotFoundException, IOException {
		InputStream tokenModelIn = new FileInputStream("en-token.bin");
		TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		String tokens[] = tokenizer.tokenize(text);
		tokenModelIn.close();
		return tokens;
	}
	
    private static String[] sentenceDetection(String paragraph) throws InvalidFormatException, IOException {
 
        InputStream is = new FileInputStream("en-sent.bin");
        SentenceModel model = new SentenceModel(is);
        
        SentenceDetectorME sdetector = new SentenceDetectorME(model);
        
        is.close();

        // returns sentences in the paragraph
        return sdetector.sentDetect(paragraph);
 
    }
    
    private static List<Result> flat(Map<String, Result> result) {
    	List<Result> results = new ArrayList<Result>(result.size());
    	SortedSet<String> keys = new TreeSet<String>(result.keySet());
    	for (String key : keys) { 
    	   results.add(result.get(key));
    	}
    	return results;
    }

}

