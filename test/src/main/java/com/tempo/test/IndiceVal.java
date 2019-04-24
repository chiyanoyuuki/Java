package com.tempo.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndiceVal 
{
	private double val;
	private String date;
	
	@JsonCreator
	public IndiceVal(@JsonProperty("val") double val, @JsonProperty("date") String date)
	{
		this.val = val;
		this.date = date;
	}
	
	public IndiceVal() {}
	
	public double getVal() {return this.val;}
	public String getDate() {return this.date;}
	
	public void setVal(double val) {this.val=val;}
	public void setDate(String date) {this.date=date;}
	
	 @Override
	 public String toString() {
	  return 
			 "val : " + this.val +
			 "date : " + this.date
			 ;
	 }
}