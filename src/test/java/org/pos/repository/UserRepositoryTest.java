package org.pos.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pos.Application;
import org.pos.config.Constants;
import org.pos.domain.User;
import org.pos.security.AuthoritiesConstants;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class UserRepositoryTest {
	
	@Inject
	private UserRepository userRepository;
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testFindAllByActivatedIsAndAuthorityNameIs() {
		List<User> users = userRepository.findAllByActivatedIsAndAuthorityNameIs(true, AuthoritiesConstants.ADMIN);
		assertThat(users).isNotNull().isNotEmpty();
		assertThat(users.size()).isEqualTo(2);
	}
	
	@Test
	public void testFindAllEmailByActivatedIsAndAuthorityNameIs() {
		List<String> emails = userRepository.findAllEmailByActivatedIsAndAuthorityNameIs(true, AuthoritiesConstants.ADMIN);
		assertThat(emails).isNotNull().isNotEmpty();
		assertThat(emails.size()).isEqualTo(2);
	}
    
}
