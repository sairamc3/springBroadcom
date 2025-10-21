package com.example.CashCards;

import org.springframework.data.annotation.Id;

public record CashCard(@Id Long id, Double amount, String owner) {

    public CashCard(Double amount, String owner) {
        this(null, amount, owner);
    }
}
