package org.pos.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.pos.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(DateTime dateTime);

    User findOneByResetKey(String resetKey);

    User findOneByLogin(String login);

    User findOneByEmail(String email);
    
    @Query("select u from User u inner join u.authorities a where u.activated = ?1 and a.name = ?2")
    List<User> findAllByActivatedIsAndAuthorityNameIs(boolean activated, String authorityName);
    
    @Query("select u.email from User u inner join u.authorities a where u.activated = ?1 and a.name = ?2")
    List<String> findAllEmailByActivatedIsAndAuthorityNameIs(boolean activated, String authorityName);

}
