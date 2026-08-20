package com.connectoBackend.auth.service.impl;

import com.connectoBackend.auth.dto.request.SendOtpRequest;
import com.connectoBackend.auth.dto.request.VerifyOtpRequest;
import com.connectoBackend.auth.dto.response.RegistrationVerificationResponse;
import com.connectoBackend.auth.entity.EmailVerification;
import com.connectoBackend.auth.repository.EmailVerificationRepository;
import com.connectoBackend.auth.service.EmailVerificationService;
import com.connectoBackend.common.exception.ConflictException;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.common.util.HashUtil;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${otp.expiration-minutes:5}")
    private long otpExpirationMinutes;

    @Value("${otp.registration-token-expiration-minutes:10}")
    private long tokenExpirationMinutes;

    @Value("${otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${otp.resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    @Value("${otp.max-sends-per-day:10}")
    private int maxSendsPerDay;

    private final EmailVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(SendOtpRequest request) {
        String email = normalize(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email is already registered.");
        }

        LocalDateTime now = LocalDateTime.now();
        EmailVerification verification = verificationRepository
                .findByEmailForUpdate(email)
                .orElseGet(() -> EmailVerification.builder().email(email).build());

        if (verification.getLastSentAt() != null
                && Duration.between(verification.getLastSentAt(), now).getSeconds() < resendCooldownSeconds) {
            throw new ConflictException("Please wait before requesting another OTP.");
        }
        if (verification.getSendWindowStartedAt() == null
                || verification.getSendWindowStartedAt().plusDays(1).isBefore(now)) {
            verification.setSendWindowStartedAt(now);
            verification.setSendCount(0);
        }
        if (verification.getSendCount() >= maxSendsPerDay) {
            throw new ConflictException("Too many OTP requests. Please try again later.");
        }

        String otp = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        verification.setOtpHash(HashUtil.sha256(otp));
        verification.setOtpExpiresAt(now.plusMinutes(otpExpirationMinutes));
        verification.setLastSentAt(now);
        verification.setSendCount(verification.getSendCount() + 1);
        verification.setFailedAttempts(0);
        verification.setVerified(false);
        verification.setRegistrationTokenHash(null);
        verification.setTokenExpiresAt(null);
        verification.setTokenConsumed(false);

        sendEmail(email, otp);
        verificationRepository.save(verification);
    }

    @Override
    public RegistrationVerificationResponse verifyOtp(VerifyOtpRequest request) {
        String email = normalize(request.getEmail());
        EmailVerification verification = verificationRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new ForbiddenException("OTP is invalid or has expired."));
        LocalDateTime now = LocalDateTime.now();

        if (verification.getOtpExpiresAt().isBefore(now)) {
            throw new ForbiddenException("OTP has expired. Please request a new OTP.");
        }
        if (verification.getFailedAttempts() >= maxAttempts) {
            throw new ForbiddenException("Too many OTP attempts. Please request a new OTP.");
        }
        if (!HashUtil.sha256(request.getOtp()).equals(verification.getOtpHash())) {
            verification.setFailedAttempts(verification.getFailedAttempts() + 1);
            verificationRepository.save(verification);
            throw new ForbiddenException("Invalid OTP.");
        }

        String registrationToken = randomToken();
        verification.setVerified(true);
        verification.setRegistrationTokenHash(HashUtil.sha256(registrationToken));
        verification.setTokenExpiresAt(now.plusMinutes(tokenExpirationMinutes));
        verification.setTokenConsumed(false);
        verificationRepository.save(verification);

        return RegistrationVerificationResponse.builder()
                .email(email)
                .registrationToken(registrationToken)
                .build();
    }

    @Override
    public void consumeRegistrationToken(String email, String registrationToken) {
        EmailVerification verification = verificationRepository.findByEmailForUpdate(normalize(email))
                .orElseThrow(() -> new ForbiddenException("Email verification is required before registration."));
        if (!verification.isVerified()
                || verification.isTokenConsumed()
                || verification.getTokenExpiresAt() == null
                || verification.getTokenExpiresAt().isBefore(LocalDateTime.now())
                || verification.getRegistrationTokenHash() == null
                || !verification.getRegistrationTokenHash().equals(HashUtil.sha256(registrationToken))) {
            throw new ForbiddenException("Email verification is invalid or has expired.");
        }
        verification.setTokenConsumed(true);
        verificationRepository.save(verification);
    }

    private void sendEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Connecto email verification");
            message.setText("Your Connecto verification code is " + otp
                    + ". It expires in " + otpExpirationMinutes + " minutes.");
            mailSender.send(message);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Unable to send verification email.", exception);
        }
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}