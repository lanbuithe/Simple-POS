package org.pos.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.pos.domain.User;
import org.pos.repository.UserRepository;
import org.pos.service.UserService;
import org.pos.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private UserService userService;
    
    /**
     * GET  /users/username/:login -> get the "login" user.
     */
    @RequestMapping(value = "/users/username/{login}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public User getUser(@PathVariable String login, HttpServletResponse response) {
        log.debug("REST request to get User : {}", login);
        User user = userRepository.findOneByLogin(login);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return user;
    }
    
    /**
     * POST  /users -> create a new user.
     */
    @RequestMapping(value = "/users",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody User newUser) throws URISyntaxException {
        User user = userRepository.findOneByLogin(newUser.getLogin());
        if (user != null) {
            return ResponseEntity.badRequest().header("Failure", "login already in use").build();
        } else {
            if (userRepository.findOneByEmail(newUser.getEmail()) != null) {
                return ResponseEntity.badRequest().header("Failure", "e-mail address already in use").build();
            }           
            user = userService.createUserByAdmin(newUser);
            return ResponseEntity.created(new URI("/api/users/" + user.getLogin())).build();
        }
    }
    
    /**
     * PUT  /users -> Update an existing user.
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody User user) throws URISyntaxException {
        log.debug("REST request to update Usser : {}", user);
        if (user.getId() == null) {
            return create(user);
        }
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }    
    
    /**
     * GET  /users -> get all the users.
     */
    @RequestMapping(value = "/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<User>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<User> page = userRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users", offset, limit);
        return new ResponseEntity<List<User>>(page.getContent(), headers, HttpStatus.OK);
    }    
    
    /**
     * GET  /users/:id -> get the "id" user.
     */
    @RequestMapping(value = "/users/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get User : {}", id);
        User user = userRepository.findOne(id);
        if (user == null) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }     
    
    /**
     * DELETE  /users/:id -> delete the "id" user.
     */
    @RequestMapping(value = "/users/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete User : {}", id);
        userRepository.delete(id);
    }
    
}
