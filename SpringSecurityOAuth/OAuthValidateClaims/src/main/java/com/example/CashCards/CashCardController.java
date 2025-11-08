package com.example.CashCards;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository repository;

    public CashCardController(CashCardRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestId){
        return this.repository.findById(requestId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<CashCard> createCashCard(@RequestBody CashCardRequest cashCardRequest,
                                                    UriComponentsBuilder ucb,
    @CurrentOwner String owner){
        CashCard newCashCard = new CashCard(cashCardRequest.amount(), owner);
        CashCard savedCashCard = this.repository.save(newCashCard);
        URI newCashCardLocation = ucb.path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(newCashCardLocation).body(savedCashCard);

    }

    @GetMapping
    public ResponseEntity<Iterable<CashCard>> findAll(){

        Iterable<CashCard> all = this.repository.findAll();

        return ResponseEntity.ok(all);
    }
}
