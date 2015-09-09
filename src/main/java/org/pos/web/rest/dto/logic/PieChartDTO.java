package org.pos.web.rest.dto.logic;

import java.math.BigDecimal;

public class PieChartDTO {

	private String key;
	
	private Object y;
	
	public PieChartDTO() {
	}
	
	public PieChartDTO(String key, BigDecimal y) {
		this.key = key;
		this.y = y;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getY() {
		return y;
	}

	public void setY(Object y) {
		this.y = y;
	}
	
}
