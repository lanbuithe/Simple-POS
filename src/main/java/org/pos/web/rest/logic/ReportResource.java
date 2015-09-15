package org.pos.web.rest.logic;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.pos.service.logic.JasperReportService;
import org.pos.util.JasperReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public FileSystemResource getRevenue(HttpServletResponse response) {  
    	FileSystemResource fileSystemResource = null;
    	try {
    		fileSystemResource = jasperReportService.generateReport(REVENUE_REPORT_NAME, JasperReportType.PDF);
        	response.setContentType("application/pdf");
        	response.setHeader("Content-Disposition", "attachment; filename=" + REVENUE_REPORT_NAME + ".pdf");
    	} catch (Exception e) {
    		response.setStatus(HttpStatus.NOT_FOUND.value());
    		log.error("Exception export revenue {}", e.fillInStackTrace());
    	}
        return fileSystemResource;
    }  
    
}
