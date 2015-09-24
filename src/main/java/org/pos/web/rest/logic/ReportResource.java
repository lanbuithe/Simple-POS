package org.pos.web.rest.logic;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.pos.service.logic.JasperReportService;
import org.pos.util.DateTimePattern;
import org.pos.util.JasperReportType;
import org.pos.util.ReportParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Report.
 */
@RestController
@RequestMapping("/api/report")
public class ReportResource {
	
	private final Logger log = LoggerFactory.getLogger(ReportResource.class);
	
	private final String REVENUE_REPORT_NAME = "revenue_report";

    @Inject
    private JasperReportService jasperReportService;
    
    @RequestMapping(value = "/revenue", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource getRevenue(HttpServletResponse response,
    		@RequestParam(value = "from" , required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE_TIME) DateTime from, 
    		@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE_TIME) DateTime to) {  
    	FileSystemResource fileSystemResource = null;
    	try {
    		Map<String, Object> parameters = new HashMap<String, Object>();
    		DateTime now = new DateTime();
    		if (null == from) {
    			parameters.put(ReportParameter.POS_FROM_DATE.toString(), now.toString(DateTimePattern.ISO_DATE));
    		} else {
    			parameters.put(ReportParameter.POS_FROM_DATE.toString(), from.toString(DateTimePattern.ISO_DATE));
    		}
    		if (null == to) {
    			parameters.put(ReportParameter.POS_TO_DATE.toString(), now.toString(DateTimePattern.ISO_DATE));
    		} else {
    			parameters.put(ReportParameter.POS_TO_DATE.toString(), to.toString(DateTimePattern.ISO_DATE));
    		}
    		fileSystemResource = jasperReportService.generateReport(REVENUE_REPORT_NAME, JasperReportType.PDF, parameters);
        	response.setContentType("application/pdf");
        	response.setHeader("Content-Disposition", "attachment; filename=" + REVENUE_REPORT_NAME + ".pdf");
    	} catch (Exception e) {
    		response.setStatus(HttpStatus.NOT_FOUND.value());
    		log.error("Exception export revenue {}", e.fillInStackTrace());
    	}
        return fileSystemResource;
    }  
    
}
