package com.ecom.userservice.service;

import com.ecom.userservice.model.Users;
import com.ecom.userservice.repository.AuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user=authRepository.findByUsername(username)
                .orElseThrow( ()-> new UsernameNotFoundException("User not found with the username :"+username ) );
        return new MyUserDetails(user);
    }
}
