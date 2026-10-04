package com.sami9889.aiphonesupport.service;

public class OutboundTelephonyUnavailableException extends RuntimeException {
    public OutboundTelephonyUnavailableException(String message) {
        super(message);
    }

    public OutboundTelephonyUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}