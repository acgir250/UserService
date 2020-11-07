package com.omnirio.userservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.client.http.HttpRequest;
import com.omnirio.userservice.entities.User;
import com.omnirio.userservice.security.JwtTokenUtil;
import com.omnirio.userservice.service.UserService;

@RestController
@RequestMapping("/api/userservice")
public class UserController {

	@Autowired
	UserService service;

	@Autowired
	JwtTokenUtil tokenUtil;

	@SuppressWarnings("unchecked")
	@PostMapping("/users")
	public ResponseEntity<?> saveUser(@RequestBody String user,@RequestHeader("Authorization") String token) throws Exception {
		String role = tokenUtil.getRoleFromToken(token.substring(7));
		if(role.equalsIgnoreCase("customer"))
		{
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer Can't be able to login");
		}
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

	@GetMapping("/users/{name}/username")
	public ResponseEntity<?> getUserByName(@PathVariable("name") String name,@RequestHeader("Authorization") String token) throws Exception {
		String role = tokenUtil.getRoleFromToken(token.substring(7));
		if(role.equalsIgnoreCase("customer"))
		{
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer Can't be able to login");
		}
		return ResponseEntity.ok(service.getUserByName(name));
	}
	
	

	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("id") long id,
			@RequestHeader("Authorization") String token) throws Exception {
		String role = tokenUtil.getRoleFromToken(token.substring(7));
		if (role.equalsIgnoreCase("customer")) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer Can't be able to login");
		}
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
	public ResponseEntity<CollectionModel<EntityModel<User>>> getAllUsers(
			@RequestHeader(value = "Authorization") String token) throws Exception {
		String role = tokenUtil.getRoleFromToken(token.substring(7));
		if (role.equalsIgnoreCase("customer")) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer Can't be able to login");
		}
		List<User> user = service.getAllUser();
		// String role = tokenUtil.getRoleFromToken(token.substring(7));
		List<EntityModel<User>> finalResponse = new ArrayList<EntityModel<User>>();
		for (User userObject : user) {
			EntityModel<User> model = EntityModel.of(userObject, getAllLinks(userObject.getUserId()));
			finalResponse.add(model);
		}

		return ResponseEntity.ok(CollectionModel.of( //
				finalResponse, //
				linkTo(methodOn(UserController.class).getAllUsers(null)).withSelfRel()
						.andAffordance(afford(methodOn(UserController.class).saveUser(null,null)))));
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") long id) throws Exception {
		return service.getUserById(id).map(mapper -> EntityModel.of(mapper, getAllLinks(mapper.getUserId())))
				.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") long id, @RequestHeader("Authorization") String token)
			throws Exception {
		String role = tokenUtil.getRoleFromToken(token.substring(7));
		if (role.equalsIgnoreCase("customer")) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer Can't be able to login");
		}
		service.delete(id);
		return ResponseEntity.accepted().build();
	}

	private List<Link> getAllLinks(long id) {
		List<Link> links = null;
		try {
			links = Arrays.asList(
					linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel()
							.andAffordance(afford(methodOn(UserController.class).updateUser(null, id, null)))
							.andAffordance(afford(methodOn(UserController.class).deleteUser(id,null))),
					linkTo(methodOn(UserController.class).getAllUsers(null)).withRel("users"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return links;
	}
}
