package com.omnirio.userservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnirio.userservice.entities.User;
import com.omnirio.userservice.repositories.UserRepository;
import com.omnirio.userservice.service.UserService;

@RestController
@RequestMapping("/api/userservice")
public class UserController {

	@Autowired
	UserService service;

	@Autowired
	UserRepository repo;
	
	@SuppressWarnings("unchecked")
	@PostMapping("/users")
	public ResponseEntity<?> saveUser(@RequestBody String user) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> parser = mapper.readValue(user, Map.class);
		User result = service.saveUser(parser);

		EntityModel<User> modelResource = EntityModel.of(result, getAllLinks(result.getUserId()));

		return modelResource.getLink(IanaLinkRelations.SELF).map(Link::getHref).map(href -> {
			try {
				return new URI(href);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}).map(uri -> ResponseEntity.noContent().location(uri).build())
				.orElse(ResponseEntity.badRequest().body("Unable to update " + user));

	}

	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("id") long id) throws Exception {
		User result = service.Update(user, id);
		EntityModel<User> modelResource = EntityModel.of(result, getAllLinks(result.getUserId()));

		return modelResource.getLink(IanaLinkRelations.SELF).map(Link::getHref).map(href -> {
			try {
				return new URI(href);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}).map(uri -> ResponseEntity.noContent().location(uri).build())
				.orElse(ResponseEntity.badRequest().body("Unable to update " + user));

	}

	@GetMapping(value = "/users")
	public ResponseEntity<CollectionModel<EntityModel<User>>> getAllUsers() throws Exception {
		 List<User> user = service.getAllUser();
	    List<EntityModel<User>> finalResponse =new ArrayList<EntityModel<User>>();
		 for(User userObject: user)
		 {
			   EntityModel<User> model =EntityModel.of(userObject,getAllLinks(userObject.getUserId()));
			   finalResponse.add(model);
		 }
		
		return ResponseEntity.ok(CollectionModel.of( //
				finalResponse, //
				linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel()
						.andAffordance(afford(methodOn(UserController.class).saveUser(null)))));
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") long id) throws Exception {
		return service.getUserById(id).map(mapper->EntityModel.of(mapper,getAllLinks(mapper.getUserId())))
				.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") long id) throws Exception {
		service.delete(id);
		return null;
	}

	private List<Link> getAllLinks(long id) {
		List<Link> links=null;
		try {
			 links =Arrays.asList(
					linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel()
							.andAffordance(afford(methodOn(UserController.class).updateUser(null, id)))
							.andAffordance(afford(methodOn(UserController.class).deleteUser(id))),
					linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return links;
	}
}
