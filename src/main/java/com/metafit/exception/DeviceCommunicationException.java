package com.metafit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when communication with biometric/RFID device fails
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class DeviceCommunicationException extends RuntimeException {

    private final Long deviceId;
    private final String deviceName;
    private final String errorType;

    public DeviceCommunicationException(Long deviceId, String deviceName, String message) {
        super(String.format("Device communication failed [%s]: %s", deviceName, message));
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.errorType = null;
    }

    public DeviceCommunicationException(Long deviceId, String deviceName, String errorType, String message) {
        super(message);
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.errorType = errorType;
    }

    public DeviceCommunicationException(String message, Throwable cause) {
        super(message, cause);
        this.deviceId = null;
        this.deviceName = null;
        this.errorType = null;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getErrorType() {
        return errorType;
    }
}
