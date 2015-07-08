package org.coffee.config;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {
	
	private final Logger log = LoggerFactory.getLogger(RepositoryConfiguration.class);
	
	@Inject
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public EntityManager entityManager() {
    	log.info("Initializing Enitity Manager");
        return entityManagerFactory.createEntityManager();
    }

}
