package org.pos.web.rest.dto.logic;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.pos.util.DateTimePattern;
import org.pos.util.JodaTimeUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LineChart {
	
	private String key;
	
	@JsonIgnore
	private Object x;

	@JsonIgnore
	private Object y;
	
	private List<Object> values;
	
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

	public List<Object> getValues() {
		values = new ArrayList<Object>();
		try {
			DateTime dateTime = JodaTimeUtil.parse(x.toString(), DateTimePattern.ISO_DATE);
			values.add(dateTime.getMillis());
		} catch(Exception e) {
			values.add(x);	
		}
		values.add(y);
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

}
