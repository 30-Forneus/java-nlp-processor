package com.forneus.interview.resolvit;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
	
	@JsonProperty
	private String word;
	
	@JsonProperty("total-occurrences")
	private Integer totalOcurrences;
	
	@JsonProperty("sentence-indexes")
	private List<Integer> sentenceIndexes;	
	
	public Result(String word) {
		super();
		this.word = word;
		this.totalOcurrences = 0;
		this.sentenceIndexes = new ArrayList<Integer>();
	}
	
	public String getWord() {
		return word;
	}

	public Integer getTotalOcurrences() {
		return totalOcurrences;
	}

	public List<Integer> getSentenceIndexes() {
		return sentenceIndexes;
	}
	
	public void set(int sentenceIndex) {
		totalOcurrences ++;
		sentenceIndexes.add(sentenceIndex);		
	}

}
