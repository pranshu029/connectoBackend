package com.connectoBackend.security.service.impl;

import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.security.service.CustomUserDetailsService;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.AccountStatus;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        User user = userRepository.findByEmailAndAccountStatusAndDeletedFalse(username, AccountStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .disabled(user.isDeleted())
                .build();
    }
}