package com.example.CashCards;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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
    private ResponseEntity<CashCard> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb){
        CashCard savedCashCard = this.repository.save(cashCard);
        URI newCashCardLocation = ucb.path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(newCashCardLocation).body(savedCashCard);

    }

    @GetMapping
    public ResponseEntity<Iterable<CashCard>> findAll(){
        return ResponseEntity.ok(this.repository.findAll());
    }
}
