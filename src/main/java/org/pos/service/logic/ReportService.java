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
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);
    
    @Inject
    private ResourceLoader resourceLoader;
    
    @Inject
    private MessageSource messageSource;
    
    @Inject
    private DataSource dataSource;
    
    public void generateRevenueReport() throws JRException, IOException, SQLException {
    	Resource resource = resourceLoader.getResource("classpath:reports/Coffee_Landscape.jrxml");
        File file = resource.getFile();
    	JasperDesign jasperDesign = JRXmlLoader.load(file);
    	JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	Locale locale = Locale.forLanguageTag("vi");
    	ResourceBundle resourceBundle = new MessageSourceResourceBundle(messageSource, locale);
    	parameters.put(JRParameter.REPORT_LOCALE, locale);
        parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
    	Connection connection = dataSource.getConnection();
    	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
    	File f = new File("d:\\Coffee_Landscape.pdf");
        f.createNewFile();
        OutputStream output = new FileOutputStream(f);
        JasperExportManager.exportReportToPdfStream(jasperPrint, output);
        connection.close();
    }
    
    public void generateReport() {
    
    }
    
}
