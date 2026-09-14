package com.connectchat.platform.egress;

public record LegacyGatewayResult(boolean success, String message) {

    public static LegacyGatewayResult ok(String message) {
        return new LegacyGatewayResult(true, message);
    }

    public static LegacyGatewayResult failed(String message) {
        return new LegacyGatewayResult(false, message);
    }
}
