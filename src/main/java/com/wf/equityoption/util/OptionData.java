package com.wf.equityoption.util;

import java.util.Date;



public class OptionData {
	private String optionName;
	private double strike;
	private double volatility;
	private Date expiryDate;
	
	public OptionData(){
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Contract Name : "+optionName + " -- ");
		sb.append("Expiry Date : "+expiryDate + " -- ");
		sb.append("Strike : "+strike + " -- ");
		sb.append("volatility : "+volatility + " -- ");
		return sb.toString();
	}

	public OptionData(String optionName,double strike, double voloatility, Date expiry ){
		this.optionName = optionName;
		this.strike = strike;
		this.volatility = voloatility;
		this.expiryDate = expiry;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public double getStrike() {
		return strike;
	}

	public void setStrike(double strike) {
		this.strike = strike;
	}

	public double getVolatility() {
		return volatility;
	}

	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	
}
