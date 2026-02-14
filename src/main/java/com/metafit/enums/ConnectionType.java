package com.metafit.enums;

/**
 * Device connection type
 */
public enum ConnectionType {
    LAN("LAN", "Wired Ethernet connection"),
    WIFI("WiFi", "Wireless network connection"),
    CLOUD("Cloud", "Cloud-based API connection"),

    SERIAL("Serial", "RS232/RS485 serial connection"),
    USB("USB", "USB connection"),
    BLUETOOTH("Bluetooth", "Bluetooth connection");

    private final String displayName;
    private final String description;

    ConnectionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresNetwork() {
        return this == LAN || this == WIFI || this == CLOUD;
    }

    public boolean isWireless() {
        return this == WIFI || this == BLUETOOTH || this == CLOUD;
    }
}
