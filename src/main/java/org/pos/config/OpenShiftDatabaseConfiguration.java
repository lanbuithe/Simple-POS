package org.pos.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile(Constants.SPRING_PROFILE_OPENSHIFT)
public class OpenShiftDatabaseConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(OpenShiftDatabaseConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }

    @Bean
    public DataSource dataSource() {
        log.debug("Configuring OpenShift Datasource");

        String openShiftUrl = propertyResolver.getProperty("openshift-url");
        if (openShiftUrl != null) {
            log.info("Using OpenShift, parsing their $OPENSHIFT_POSTGRESQL_DB_URL to use it with JDBC");
            URI dbUri = null;
            try {
                dbUri = new URI(openShiftUrl);
            } catch (URISyntaxException e) {
                throw new ApplicationContextException("OpenShift database connection pool is not configured correctly");
            }
            log.info(String.format("DB URI user info=%s", dbUri.getUserInfo()));
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" +
                    dbUri.getHost() +
                    ':' +
                    dbUri.getPort() +
                    "/" + 
                    propertyResolver.getProperty("openshift-databaseName");
            HikariConfig config = new HikariConfig();
            config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));
            config.addDataSourceProperty("url", dbUrl);
            config.addDataSourceProperty("user", username);
            config.addDataSourceProperty("password", password);
            log.info(String.format("DB information:\n \tusername=%s\n \tpassword=%s\n \thost=%s\n \tport=%s\n \turl=%s\n \tdataSourceClassName=%s", 
            		username, password, dbUri.getHost(), dbUri.getPort(), dbUrl, config.getDataSourceClassName()));            
            return new HikariDataSource(config);
        } else {
            throw new ApplicationContextException("OpenShift database URL is not configured, you must set --spring.datasource.openshift-url=$OPENSHIFT_POSTGRESQL_DB_URL");
        }
    }
}
