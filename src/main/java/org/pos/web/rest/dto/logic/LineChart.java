package org.pos.web.rest.dto.logic;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LineChart {
	
	private String key;
	
	@JsonIgnore
	private Object x;

	@JsonIgnore
	private Object y;
	
	private List<List<Object>> values;
	
	public LineChart() {
		
	}
	
	public LineChart(String key, Object x, Object y) {
		this.key = key;
		this.x = x;
		this.y = y;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public Object getX() {
		return x;
	}

	public void setX(Object x) {
		this.x = x;
	}

	public Object getY() {
		return y;
	}

	public void setY(Object y) {
		this.y = y;
	}

	public List<List<Object>> getValues() {
		values = new ArrayList<List<Object>>();
		List<Object> elements = new ArrayList<Object>();
		elements.add(x);
		elements.add(y);
		values.add(elements);
		return values;
	}

	public void setValues(List<List<Object>> values) {
		this.values = values;
	}

}
