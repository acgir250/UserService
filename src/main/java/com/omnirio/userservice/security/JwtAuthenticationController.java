package com.omnirio.userservice.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnirio.userservice.entities.User;
import com.omnirio.userservice.repositories.UserRepository;
import com.omnirio.userservice.service.UserService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService jwtInMemoryUserDetailsService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService service;
	
	@Autowired
	@Qualifier("bCryptPasswordEncoder")
	BCryptPasswordEncoder encoder;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody User authenticationRequest)
			throws Exception {

		authenticate(authenticationRequest.getUserName(), authenticationRequest.getPassword());

		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(authenticationRequest.getUserName());

		User userData = userRepository.findByUserName(userDetails.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Username not exception: " + userDetails.getUsername()));

		final String token = jwtTokenUtil.generateToken(userData.getRole(), userData.getUserName());

		return ResponseEntity.ok(token);	
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/registeruser", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody String user)
			throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> parser = mapper.readValue(user, Map.class);
		User userNameObj =service.getUserByName(parser.get("userName").toString());
		Map<String,Object> response = new HashMap<String, Object>();
		if(userNameObj== null)
		{
			String rawPassword = parser.get("password").toString();
			String encodePass = encoder.encode(rawPassword);
			parser.put("password",encodePass);
			parser.put("roleName","Customer");
			User result = service.saveUser(parser);
			
			final String token = jwtTokenUtil.generateToken(result.getRole(), result.getUserName());
			response.put("token",token);
			String servletPath =ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
			response.put("_links",new HashMap<String, String>().put("self", servletPath+"/api/userservice"+"/"+result.getUserId()));
		}
		else
		{
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User Already Register");
		}
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("/username/{name}")
	public ResponseEntity<?> getUserByName(@PathVariable("name") String name) throws Exception {
		User user =service.getUserByName(name);
		ObjectMapper mapper=  new ObjectMapper();
		Map<String,String> putData = new HashMap<String, String>();
		putData.put("userName", user.getUserName());
		putData.put("password", user.getPassword());
		String ee = mapper.writeValueAsString(putData);
		return ResponseEntity.ok(ee);
	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
