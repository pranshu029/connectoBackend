package com.connectoBackend.session.util;

import com.connectoBackend.session.dto.DeviceInfo;
import com.connectoBackend.session.enums.DeviceType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Extracts device information from an HTTP request.
 */
@Component
public class DeviceInfoExtractor {

    // -> Extract device information.
    public DeviceInfo extract(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        return DeviceInfo.builder()
                .deviceId(generateDeviceId(request))
                .deviceName(resolveDeviceName(userAgent))
                .deviceType(resolveDeviceType(userAgent))
                .operatingSystem(resolveOperatingSystem(userAgent))
                .browser(resolveBrowser(userAgent))
                .ipAddress(resolveIpAddress(request))
                .userAgent(userAgent)
                .build();
    }

    // -> Generate deterministic device id.
    private String generateDeviceId(HttpServletRequest request) {

        String fingerprint =
                resolveIpAddress(request)
                        + "|"
                        + request.getHeader("User-Agent");

        return UUID.nameUUIDFromBytes(
                fingerprint.getBytes(StandardCharsets.UTF_8)
        ).toString();
    }

    // -> Resolve client IP.
    private String resolveIpAddress(HttpServletRequest request) {

        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    // -> Resolve device type.
    private DeviceType resolveDeviceType(String userAgent) {

        if (userAgent == null) {
            return DeviceType.UNKNOWN;
        }

        String agent = userAgent.toLowerCase();

        if (agent.contains("mobile")) {
            return DeviceType.MOBILE;
        }

        if (agent.contains("tablet") || agent.contains("ipad")) {
            return DeviceType.TABLET;
        }

        return DeviceType.DESKTOP;
    }

    // -> Resolve device name.
    private String resolveDeviceName(String userAgent) {

        if (userAgent == null) {
            return "Unknown Device";
        }

        if (userAgent.contains("Windows")) {
            return "Windows PC";
        }

        if (userAgent.contains("Mac")) {
            return "Mac";
        }

        if (userAgent.contains("Android")) {
            return "Android";
        }

        if (userAgent.contains("iPhone")) {
            return "iPhone";
        }

        if (userAgent.contains("iPad")) {
            return "iPad";
        }

        if (userAgent.contains("Linux")) {
            return "Linux";
        }

        return "Unknown Device";
    }

    // -> Resolve operating system.
    private String resolveOperatingSystem(String userAgent) {

        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Windows")) {
            return "Windows";
        }

        if (userAgent.contains("Mac OS")) {
            return "macOS";
        }

        if (userAgent.contains("Android")) {
            return "Android";
        }

        if (userAgent.contains("iPhone")
                || userAgent.contains("iPad")) {
            return "iOS";
        }

        if (userAgent.contains("Linux")) {
            return "Linux";
        }

        return "Unknown";
    }

    // -> Resolve browser.
    private String resolveBrowser(String userAgent) {

        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Edg")) {
            return "Edge";
        }

        if (userAgent.contains("Chrome")) {
            return "Chrome";
        }

        if (userAgent.contains("Firefox")) {
            return "Firefox";
        }

        if (userAgent.contains("Safari")
                && !userAgent.contains("Chrome")) {
            return "Safari";
        }

        if (userAgent.contains("Opera")
                || userAgent.contains("OPR")) {
            return "Opera";
        }

        return "Unknown";
    }
}