package org.pos.web.rest.dto.logic;

import java.math.BigDecimal;

public class PieChart {

	private String key;
	
	private BigDecimal y;
	
	public PieChart() {
	}
	
	public PieChart(String key, BigDecimal y) {
		this.key = key;
		this.y = y;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public BigDecimal getY() {
		return y;
	}

	public void setY(BigDecimal y) {
		this.y = y;
	}
	
}
