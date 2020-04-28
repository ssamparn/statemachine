package com.spring.statemachine.service;

import com.spring.statemachine.domain.PaymentEvent;
import com.spring.statemachine.domain.PaymentState;
import com.spring.statemachine.entity.PaymentEntity;
import com.spring.statemachine.repository.PaymentStateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentStateRepository paymentRepository;

    private PaymentEntity paymentEntity;

    @BeforeEach
    void setUp() {
        paymentEntity = PaymentEntity.builder().amount(new BigDecimal("12.99")).build();
    }

    @Test
    @Transactional
    public void preAuthorization() {
        PaymentEntity newPaymentCreated = paymentService.newPayment(this.paymentEntity);
        StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.preAuthorization(newPaymentCreated.getId());

        PaymentEntity preAuthorizedPayment = paymentRepository.getOne(newPaymentCreated.getId());

        Assertions.assertNotNull(preAuthorizedPayment);
        Assertions.assertEquals(stateMachine.getState().getId(), PaymentState.NEW);
        Assertions.assertEquals(preAuthorizedPayment.getPaymentState(), PaymentState.NEW);
    }

    @Test
    @Transactional
    public void preAuthorizationApproved() {
        PaymentEntity newPaymentCreated = paymentService.newPayment(this.paymentEntity);
        StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.preAuthorizationApproved(newPaymentCreated.getId());

        PaymentEntity preAuthorizedPayment = paymentRepository.getOne(newPaymentCreated.getId());

        Assertions.assertNotNull(preAuthorizedPayment);
        Assertions.assertEquals(stateMachine.getState().getId(), PaymentState.PRE_AUTH);
        Assertions.assertEquals(preAuthorizedPayment.getPaymentState(), PaymentState.PRE_AUTH);
    }

}