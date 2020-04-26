package com.spring.statemachine.repository;

import com.spring.statemachine.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentStateRepository extends JpaRepository<PaymentEntity, Long> {

}
