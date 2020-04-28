package com.spring.statemachine.service;

import com.spring.statemachine.domain.PaymentEvent;
import com.spring.statemachine.domain.PaymentState;
import com.spring.statemachine.entity.PaymentEntity;
import com.spring.statemachine.repository.PaymentStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentStateChangeInceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentStateRepository paymentRepository;

    @Autowired
    public PaymentStateChangeInceptor(PaymentStateRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine) {
        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.payment_header, -1L)))
                    .ifPresent(paymentId -> {
                        PaymentEntity paymentEntity = paymentRepository.getOne(paymentId);
                        paymentEntity.setPaymentState(state.getId());
                        paymentRepository.save(paymentEntity);
                    });
        });
    }
}
