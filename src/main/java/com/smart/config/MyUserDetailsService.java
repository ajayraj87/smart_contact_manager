package com.smart.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.entity.User;
import com.smart.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = this.userRepository.findByEmail(username);
		if (user.isPresent()) {
			  User u = user.get();
			return  org.springframework.security.core.userdetails.User.builder()
					.username(u.getName())
					.password(u.getPassword())
					.roles(u.getRole())
					.build();
		} else {

			throw new UsernameNotFoundException("this is not exist email "+username);
		}
	}

}
