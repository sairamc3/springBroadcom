package com.example.CashCards;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CashCardRepository extends CrudRepository<CashCard, Long> {

    List<CashCard> findByOwner(String owner);

}
