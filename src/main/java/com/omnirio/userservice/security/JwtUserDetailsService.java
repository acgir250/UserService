package com.omnirio.userservice.security;

import com.omnirio.userservice.repositories.UserRepository;
import com.omnirio.userservice.security.CustomUserConverter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public JwtUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUserName(username)
				.map(CustomUserConverter::toUser)
				.orElseThrow(() -> new UsernameNotFoundException("User name not found: " + username));
	}

}