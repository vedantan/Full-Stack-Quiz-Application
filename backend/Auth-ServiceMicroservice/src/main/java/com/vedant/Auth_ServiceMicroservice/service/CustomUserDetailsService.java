package com.vedant.Auth_ServiceMicroservice.service;

import com.vedant.Auth_ServiceMicroservice.Dao.UserRepository;
import com.vedant.Auth_ServiceMicroservice.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}


//INSERT INTO users(username, password, role)
//VALUES (
//  'admin',
//          '$2a$10$encryptedPasswordHere',
//          'ROLE_ADMIN'
//);
//
//System.out.println(passwordEncoder.encode("admin123"));