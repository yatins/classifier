package com.springer.semantic.classifier.model.impl;

import com.springer.semantic.classifier.model.JournalScore;

public class JournalScoreImpl implements JournalScore {

	private String journalId = "";
	private double scoreVale;
	private double percentage;
	private double bayesianDistance;

	public String getJournalId() {
		return journalId;
	}
	public void setJournalId(String journalId) {
		this.journalId = journalId;
	}
	public double getScoreVal() {
		return scoreVale;
	}
	public void setScoreVal(double scoreVale) {
		this.scoreVale = scoreVale;
	}

	@Override
	public double getPercentage() {
		return percentage;
	}

	@Override
	public void setPercentage(double percentage) {
		this.percentage=percentage;
	}

	@Override
	public double getBayesianDistance() {
		return bayesianDistance;
	}
	@Override
	public void setBayesianDistance(double dist) {
		this.bayesianDistance = dist;
	}
	@Override
	public int compareTo(Object obj) {
		if (!(obj instanceof JournalScoreImpl))
			throw new ClassCastException("A JournalScoreImpl object expected.");
		JournalScoreImpl otherJournalScoreImpl = ((JournalScoreImpl) obj);  
		if (this.scoreVale < otherJournalScoreImpl.scoreVale) {
			return -1;
		} else if (this.scoreVale > otherJournalScoreImpl.scoreVale) {
			return 1;
		} else {
			return 0;
		}

	}

}
