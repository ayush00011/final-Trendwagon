package com.cts.TrendWagon.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.cts.TrendWagon.model.User;
import com.cts.TrendWagon.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserDetailsServiceImpl userdetailsserviceImpl;
	
	@DisplayName("LOAD  USER BY USERNAME")
	public void testloadUserByUsername() {
		
		//Arrange
		
		User user = new User();
		user.setRole("User");
		user.setName("Messi");
		user.setEmail("messi.ronaldo@gmail.com");
		String email = user.getEmail();
		
		when(userRepository.save(user)).thenReturn(user);
		
		//Act
	 UserDetails exist = userdetailsserviceImpl.loadUserByUsername(email);
		
	}
	
	
}
