package com.company.repository;

import com.company.entity.SmsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsRepository extends CrudRepository<SmsEntity, Integer> {

    Optional<SmsEntity> findTopByPhoneOrderByCreatedDateDesc(String phone);
}
