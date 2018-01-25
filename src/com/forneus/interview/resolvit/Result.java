package com.forneus.interview.resolvit;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("results")
public class Result {
	
	@JsonProperty
	private String word;
	
	@JsonIgnore
	private String lemma;
	
	@JsonProperty("total-occurrences")
	private Integer totalOcurrences;
	
	@JsonProperty("sentence-indexes")
	private List<Integer> sentenceIndexes;	
	
	public Result(String word, String lemma) {
		super();
		this.word = word;
		this.lemma = lemma;
		this.totalOcurrences = 0;
		this.sentenceIndexes = new ArrayList<Integer>();
	}
	
	public String getWord() {
		return word;
	}

	public String getLemma() {
		return lemma;
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
