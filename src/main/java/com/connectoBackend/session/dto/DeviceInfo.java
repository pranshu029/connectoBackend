package com.connectoBackend.session.dto;

import com.connectoBackend.session.enums.DeviceType;
import lombok.Builder;
import lombok.Getter;

/**
 * Device information extracted from the request.
 */
@Getter
@Builder
public class DeviceInfo {

    private String deviceId;

    private String deviceName;

    private DeviceType deviceType;

    private String operatingSystem;

    private String browser;

    private String ipAddress;

    private String userAgent;

}