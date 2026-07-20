package com.connectoBackend.auth.service.impl;


import com.connectoBackend.auth.dto.request.LoginRequest;
import com.connectoBackend.auth.dto.request.RefreshTokenRequest;
import com.connectoBackend.auth.dto.response.AuthenticationResponse;
import com.connectoBackend.auth.entity.RefreshToken;
import com.connectoBackend.auth.service.AuthenticationService;
import com.connectoBackend.auth.service.RefreshTokenService;
import com.connectoBackend.common.constants.SecurityConstants;

import com.connectoBackend.security.service.JwtService;
import com.connectoBackend.session.dto.DeviceInfo;

import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

/**
 * Implementation of {@link AuthenticationService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final DeviceInfoExtractor deviceInfoExtractor;

    @Override
    public AuthenticationResponse login(
            LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthenticationException("Invalid email or password.")
                );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        DeviceInfo deviceInfo = deviceInfoExtractor.extract(httpServletRequest);

        refreshTokenService.createRefreshToken(
                user.getId(),
                HashUtil.sha256(refreshToken),
                deviceInfo
        );

        return buildAuthenticationResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse refreshToken(
            RefreshTokenRequest request
    ) {

        String refreshTokenHash = HashUtil.sha256(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenService
                .validateRefreshToken(refreshTokenHash);

        String accessToken = jwtService.generateAccessToken(refreshToken.getUser());

        return buildAuthenticationResponse(
                accessToken,
                request.getRefreshToken()
        );
    }

    @Override
    public void logout(String refreshToken) {

        refreshTokenService.revokeRefreshToken(
                HashUtil.sha256(refreshToken)
        );
    }

    // -> Build authentication response.
    private AuthenticationResponse buildAuthenticationResponse(
            String accessToken,
            String refreshToken
    ) {

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(SecurityConstants.BEARER)
                .expiresIn(accessTokenExpiration)
                .build();
    }
}