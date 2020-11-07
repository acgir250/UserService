package com.omnirio.userservice.security;

import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

public class CustomUserConverter {

	public static User toUser(com.omnirio.userservice.entities.User user) {
		return new User(user.getUserName(), user.getPassword(), Collections.emptyList());
	}

	public static UsernamePasswordAuthenticationToken toAuthenticationToken(com.omnirio.userservice.entities.User user) {
		return new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword(), Collections.emptyList());
	}
}
