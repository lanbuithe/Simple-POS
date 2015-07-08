package org.coffee.config;

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
@Profile(Constants.SPRING_PROFILE_HEROKU)
public class HerokuDatabaseConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(HerokuDatabaseConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }

    @Bean
    public DataSource dataSource() {
        log.debug("Configuring Heroku Datasource");

        String herokuUrl = propertyResolver.getProperty("heroku-url");
        if (herokuUrl != null) {
            log.info("Using Heroku, parsing their $DATABASE_URL to use it with JDBC");
            URI dbUri = null;
            try {
                dbUri = new URI(herokuUrl);
            } catch (URISyntaxException e) {
                throw new ApplicationContextException("Heroku database connection pool is not configured correctly");
            }
            log.debug(String.format("DB URI user info=%s", dbUri.getUserInfo()));
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" +
                    dbUri.getHost() +
                    ':' +
                    dbUri.getPort() +
                    dbUri.getPath() +
                    "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            HikariConfig config = new HikariConfig();
            config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));
            config.addDataSourceProperty("url", dbUrl);
            config.addDataSourceProperty("user", username);
            config.addDataSourceProperty("password", password);
            log.debug(String.format("DB information:\n \tusername=%s\n \tpassword=%s\n \thost=%s\n \tport=%s\n \turl=%s\n \tdataSourceClassName=%s", 
            		username, password, dbUri.getHost(), dbUri.getPort(), dbUrl, config.getDataSourceClassName()));            
            return new HikariDataSource(config);
        } else {
            throw new ApplicationContextException("Heroku database URL is not configured, you must set --spring.datasource.heroku-url=$DATABASE_URL");
        }
    }
}
