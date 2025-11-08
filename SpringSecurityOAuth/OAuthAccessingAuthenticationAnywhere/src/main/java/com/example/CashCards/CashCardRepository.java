package com.example.CashCards;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public interface CashCardRepository extends CrudRepository<CashCard, Long> {

    List<CashCard> findByOwner(String owner);

    /**
     * Notice that the code isn't checking for `null`.
     * That's because the filter chain, by default, populates the SecurityContext
     * with anonymous authentication instance.
     * @return
     */
    default List<CashCard>  findAll(){
        SecurityContext context = SecurityContextHolder.getContext();
        String owner = context.getAuthentication().getName();
        return this.findByOwner(owner);
    }

}
