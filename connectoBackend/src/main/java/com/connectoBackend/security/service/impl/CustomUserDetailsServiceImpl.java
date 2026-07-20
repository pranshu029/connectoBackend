package com.connectoBackend.security.service.impl;

import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.security.service.CustomUserDetailsService;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link CustomUserDetailsService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return user;
    }
}