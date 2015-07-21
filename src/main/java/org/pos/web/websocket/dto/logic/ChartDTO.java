package org.pos.web.websocket.dto.logic;

import java.util.List;

import org.pos.web.rest.dto.logic.LineChart;
import org.pos.web.rest.dto.logic.PieChart;

public class ChartDTO {
	private List<LineChart> lines;

	private List<PieChart> pies;
	
	public ChartDTO(List<LineChart> lines, List<PieChart> pies) {
		this.lines = lines;
		this.pies = pies;
	}
	
	public List<LineChart> getLines() {
		return lines;
	}

	public void setLine(List<LineChart> lines) {
		this.lines = lines;
	}

	public List<PieChart> getPies() {
		return pies;
	}

	public void setPies(List<PieChart> pies) {
		this.pies = pies;
	}
}
