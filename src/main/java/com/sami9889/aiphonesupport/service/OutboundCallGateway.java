package com.sami9889.aiphonesupport.service;

public interface OutboundCallGateway {
    String originate(String callId, String fromNumber, String toNumber);
}