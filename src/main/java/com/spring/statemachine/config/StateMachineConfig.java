package com.spring.statemachine.config;

import com.spring.statemachine.domain.PaymentEvent;
import com.spring.statemachine.domain.PaymentState;
import com.spring.statemachine.service.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
                    .action(preAuthAction())
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTHORIZE_APPROVED)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTHORIZE_DECLINED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> listenerAdapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("StateChanged(from: %s, to: %s)", from + " ", to + " "));
            }
        };
        config.withConfiguration()
                .listener(listenerAdapter);
    }

    public Action<PaymentState, PaymentEvent> preAuthAction() {
        return context -> {
            log.info("PreAuth was called");
            if (new Random().nextInt(10) < 9) {
                log.info("Approved");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE_APPROVED)
                .setHeader(PaymentServiceImpl.payment_header, context.getMessageHeader(PaymentServiceImpl.payment_header))
                .build());
            } else {
                log.info("Declined! No Credit!!!!!");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE_DECLINED)
                        .setHeader(PaymentServiceImpl.payment_header, context.getMessageHeader(PaymentServiceImpl.payment_header))
                        .build());
            }
        };
    }
}
