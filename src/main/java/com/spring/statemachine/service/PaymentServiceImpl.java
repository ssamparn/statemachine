package com.spring.statemachine.service;

import com.spring.statemachine.domain.PaymentEvent;
import com.spring.statemachine.domain.PaymentState;
import com.spring.statemachine.entity.PaymentEntity;
import com.spring.statemachine.repository.PaymentStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String payment_header = "payment_id";

    private final PaymentStateRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInceptor paymentStateChangeInceptor;

    @Autowired
    public PaymentServiceImpl(PaymentStateRepository paymentRepository, StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory, PaymentStateChangeInceptor paymentStateChangeInceptor) {
        this.paymentRepository = paymentRepository;
        this.stateMachineFactory = stateMachineFactory;
        this.paymentStateChangeInceptor = paymentStateChangeInceptor;
    }

    @Override
    public PaymentEntity newPayment(PaymentEntity paymentEntity) {
        paymentEntity.setPaymentState(PaymentState.NEW);
        return paymentRepository.save(paymentEntity);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuthorization(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEventToStateMachine(paymentId, stateMachine, PaymentEvent.PRE_AUTHORIZE);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> approvePaymentAuthorization(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEventToStateMachine(paymentId, stateMachine, PaymentEvent.AUTH_APPROVED);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declinePaymentAuthorization(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEventToStateMachine(paymentId, stateMachine, PaymentEvent.AUTH_DECLINED);
        return null;
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        PaymentEntity payment = paymentRepository.getOne(paymentId);
        StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(stateMachineAccess -> {
                    stateMachineAccess.addStateMachineInterceptor(paymentStateChangeInceptor);
                    stateMachineAccess.resetStateMachine(new DefaultStateMachineContext<>(payment.getPaymentState(), null, null, null));
                });

        stateMachine.start();

        return stateMachine;
    }

    private void sendEventToStateMachine(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent paymentEvent) {

        Message paymentEventMessage = MessageBuilder.withPayload(paymentEvent)
                                .setHeader(payment_header, paymentId)
                                .build();

        stateMachine.sendEvent(paymentEventMessage);
    }
}
