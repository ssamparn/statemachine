package com.spring.statemachine.service;

import com.spring.statemachine.domain.PaymentEvent;
import com.spring.statemachine.domain.PaymentState;
import com.spring.statemachine.entity.PaymentEntity;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    PaymentEntity newPayment(PaymentEntity paymentEntity);

    StateMachine<PaymentState, PaymentEvent> preAuthorization(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> preAuthorizationApproved(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> approvePaymentAuthorization(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declinePaymentAuthorization(Long paymentId);

}
