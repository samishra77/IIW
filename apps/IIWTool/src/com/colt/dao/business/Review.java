package com.colt.dao.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Review extends Item{
	private static final long serialVersionUID = 1;
	@PrimaryKey
	private int reviewid;
	
	private int offeringid;
	private int subcategoryid;
	private int userid;
	private String overallcomment;
	private int overallrating;
	List<Integer> questionid = new ArrayList<Integer>();
	List<String> answertext = new ArrayList<String>();
	List<Integer> rating = new ArrayList<Integer>();

	public Review(int reviewid, int offeringid, int userid, int subcategoryid, int overallrating, String overallcomment, List<Integer> questionid, List<Integer> rating, List<String> answertext){
		this.reviewid = reviewid;
		this.offeringid = offeringid;
		this.userid = userid;
		this.subcategoryid = subcategoryid;
		this.overallrating = overallrating;
		this.overallcomment = overallcomment;
		this.questionid = questionid;
		this.answertext = answertext;
		this.rating = rating;
	}

	public int getReviewid() {
		return reviewid;
	}

	public void setReviewid(int reviewid) {
		this.reviewid = reviewid;
	}

	public int getOfferingid() {
		return offeringid;
	}

	public void setOfferingid(int offeringid) {
		this.offeringid = offeringid;
	}

	public int getSubcategoryid() {
		return subcategoryid;
	}

	public void setSubcategoryid(int subcategoryid) {
		this.subcategoryid = subcategoryid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getOverallcomment() {
		return overallcomment;
	}

	public void setOverallcomment(String overallcomment) {
		this.overallcomment = overallcomment;
	}

	public int getOverallrating() {
		return overallrating;
	}

	public void setOverallrating(int overallrating) {
		this.overallrating = overallrating;
	}

	public List<Integer> getQuestionid() {
		return questionid;
	}

	public void setQuestionid(List<Integer> questionid) {
		this.questionid = questionid;
	}

	public List<String> getAnswertext() {
		return answertext;
	}

	public void setAnswertext(List<String> answertext) {
		this.answertext = answertext;
	}

	public List<Integer> getRating() {
		return rating;
	}

	public void setRating(List<Integer> rating) {
		this.rating = rating;
	}
	


}
