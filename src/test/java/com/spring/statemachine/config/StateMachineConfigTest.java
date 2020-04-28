package com.spring.statemachine.config;

import com.spring.statemachine.domain.PaymentEvent;
import com.spring.statemachine.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = StateMachineConfig.class)
public class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    public void testStateMachine() {
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE_APPROVED);
    }
}