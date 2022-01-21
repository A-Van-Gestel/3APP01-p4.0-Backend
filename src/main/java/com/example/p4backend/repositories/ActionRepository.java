package com.example.p4backend.repositories;

import com.example.p4backend.models.Action;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ActionRepository  extends MongoRepository<Action, String> {
    List<Action> findByEndDateAfterOrderByStartDateDesc(Date endDate);
    List<Action> findActionsByVzwID(String VzwId);
}
