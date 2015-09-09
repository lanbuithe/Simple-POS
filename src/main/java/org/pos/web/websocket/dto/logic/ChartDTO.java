package org.pos.web.websocket.dto.logic;

import java.util.List;

import org.pos.web.rest.dto.logic.LineChartDTO;
import org.pos.web.rest.dto.logic.PieChartDTO;

public class ChartDTO {
	private List<LineChartDTO> lines;

	private List<PieChartDTO> pies;
	
	public ChartDTO(List<LineChartDTO> lines, List<PieChartDTO> pies) {
		this.lines = lines;
		this.pies = pies;
	}
	
	public List<LineChartDTO> getLines() {
		return lines;
	}

	public void setLine(List<LineChartDTO> lines) {
		this.lines = lines;
	}

	public List<PieChartDTO> getPies() {
		return pies;
	}

	public void setPies(List<PieChartDTO> pies) {
		this.pies = pies;
	}
}
