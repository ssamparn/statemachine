package com.spring.statemachine.domain;

public enum PaymentEvent {
    PRE_AUTHORIZE,
    PRE_AUTHORIZE_APPROVED,
    PRE_AUTHORIZE_DECLINED,
    AUTHORIZE,
    AUTH_APPROVED,
    AUTH_DECLINED
}
