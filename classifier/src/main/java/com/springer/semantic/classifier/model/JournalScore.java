package com.springer.semantic.classifier.model;

public interface JournalScore extends Comparable {
	public String getJournalId();
	public void setJournalId(String journalId);
	public double getScoreVal();
	public void setScoreVal(double scoreVale);
	public double getPercentage();
	public void setPercentage(double percentage);
	public double getBayesianDistance();
	public void setBayesianDistance(double dist);
}
