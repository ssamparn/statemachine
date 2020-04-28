package com.spring.statemachine.service;

import com.spring.statemachine.domain.PaymentEvent;
import com.spring.statemachine.domain.PaymentState;
import com.spring.statemachine.entity.PaymentEntity;
import org.springframework.statemachine.StateMachine;

public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentEntity newPayment(PaymentEntity paymentEntity) {
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuthorization(Long paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> approvePaymentAuthorization(Long paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declinePaymentAuthorization(Long paymentId) {
        return null;
    }
}
