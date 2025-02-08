package com.cts.TrendWagon.service;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cts.TrendWagon.model.User;
import com.cts.TrendWagon.repository.UserRepository;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService{
	private static final Logger log=LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	
@Autowired
private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		Optional<User> optUser = userRepository.findByEmail(email);
		
		if(optUser.isPresent()) {
			User user = optUser.get();
			
			return org.springframework.security.core.userdetails.User.builder()
					.username(user.getEmail())
					.password(user.getPassword())
					.roles(user.getRole())
					.build();
		}
		throw new UsernameNotFoundException("User not found with email: "+email);
	}
	
	

}
