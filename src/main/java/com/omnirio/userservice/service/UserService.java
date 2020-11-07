package com.omnirio.userservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.omnirio.userservice.entities.Role;
import com.omnirio.userservice.entities.User;
import com.omnirio.userservice.repositories.RoleRepository;
import com.omnirio.userservice.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepo;

	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	ModelMapper mapper;

	@Value("${roles.customer}")
	private String customer;

	@Value("${roles.branchManager}")
	private String branchManager;

	public List<User> getAllUser() throws Exception {
		return userRepo.findAll();
	}

	public Optional<User> getUserById(long id ) throws Exception {
		
		return userRepo.findById(id);
	}

	public User getUserByName(String name) throws Exception {
		return userRepo.findByUserName(name).orElse(null);
	}

	public List<User> getUserByGender(String gender) throws Exception {
		return null;
	}

	public User saveUser(Map<String, Object> saveUser) throws Exception {
		User user = mapper.map(saveUser, User.class);
		Role role = user.getRole();
		Map<String,Object> result = new HashMap<String, Object>();
		if (role.getRoleName().equalsIgnoreCase("Customer")) {
			role.setRoleCode(customer);
		} else {
			role.setRoleCode(branchManager);
		}
		user =userRepo.save(user);
		return user;
	}
	
	public User Update(User user, long id) throws Exception {
		Optional<User> result = userRepo.findById(id);
		// boolean b =
		// userRepo.exists(Example.of(user,ExampleMatcher.matching().withMatcher("userName",GenericPropertyMatcher.of(StringMatcher.EXACT))));
		User updateUser = null;
		if (result.isPresent()) {
			updateUser = result.get();
			updateUser.setDateOfBirth(user.getDateOfBirth());
			updateUser.setGender(user.getGender());
			updateUser.setPhoneNumber(user.getPhoneNumber());
			updateUser.setUserName(user.getUserName());
			return userRepo.saveAndFlush(updateUser);
		}
		return userRepo.saveAndFlush(user);

	}

	public void delete(long id) throws Exception {
		java.util.Optional<User> user = userRepo.findById(id);
		if (user.isPresent())
			userRepo.delete(user.get());
		else
			System.out.println("No user found");
	}
}
