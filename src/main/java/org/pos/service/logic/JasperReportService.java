package org.pos.service.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.joda.time.DateTime;
import org.pos.util.JasperReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JasperReportService {

    private final Logger log = LoggerFactory.getLogger(JasperReportService.class);
    
    @Inject
    private ResourceLoader resourceLoader;
    
    @Inject
    private MessageSource messageSource;
    
    @Inject
    private DataSource dataSource;
    
    public FileSystemResource generateReport(String reportTemlateName, JasperReportType jasperReportType, Map<String, Object> parameters) throws JRException, IOException, SQLException {
    	FileSystemResource fileSystemResource = null;
    	String reportTemplateClassPath = "classpath:reports/";
    	reportTemplateClassPath = reportTemplateClassPath.concat(reportTemlateName).concat(".jasper");
    	//Resource resource = resourceLoader.getResource("classpath:reports/revenue_report.jrxml");
    	Resource resource = resourceLoader.getResource(reportTemplateClassPath);
        File file = resource.getFile();
    	/*JasperDesign jasperDesign = JRXmlLoader.load(file);
    	JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);*/
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        if (null == parameters) {
        	parameters = new HashMap<String, Object>();
        }
    	Locale locale = Locale.forLanguageTag("vi");
    	ResourceBundle resourceBundle = new MessageSourceResourceBundle(messageSource, locale);
    	parameters.put(JRParameter.REPORT_LOCALE, locale);
        parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
    	Connection connection = dataSource.getConnection();
    	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);        
        String destinationPath = System.getProperty("java.io.tmpdir");
		destinationPath = destinationPath.concat(reportTemlateName);
		destinationPath = destinationPath.concat("_").concat(String.valueOf(new DateTime().getMillis()));
		if (JasperReportType.PDF.equals(jasperReportType)); {
			destinationPath = destinationPath.concat(".pdf");
		}
		log.debug("reportTemplateClassPath={}, destinationPath={}", reportTemplateClassPath, destinationPath);
		OutputStream outputStream = new FileOutputStream(destinationPath);
		if (JasperReportType.PDF.equals(jasperReportType)); {
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
		}
		fileSystemResource = new FileSystemResource(destinationPath);
        connection.close();
        outputStream.close();
        return fileSystemResource;
    }
    
}
