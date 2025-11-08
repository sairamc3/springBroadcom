# Accessing Authentication Anywhere

You can access current Authentication without spring mvc, using `SecurityContextHolder`. These principles can be used
to access the current authentication anywhere in your spring application, even outside spring-managed beans.


# Using the static class

In the earlier example, we have used spring mvc to supply the `Authentication` instance for you in the Controller class, request handler methods.

But, this can also be done like this:

```java
SecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();

```
`SecurityContextHolder` is a static class that provides access to the current security context, including the 
authentication details. You can access it anywhere in the application.

## Usages
* When you are creating a security filter, which executes before spring MVC. 
* When processing things outside the request context, like asynchronous events.

## Another example for JWT token

```java
@GetMapping
public ResponseEntity<Iterable<CashCard>> findAll() {
    Jwt owner = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ResponseEntity.ok(this.cashCards.findByOwner(owner.getSubject()));
}

```

> The `SecurityContextHolder` uses a `ThreadLocal` to store the security context, which means it is specific
to the current thread. Therefore, you can access the authentication information within the same thread throughout the application.


## Another POJO example

Consider this theoretical Cash Card discount-calculator class that uses the currently 
authenticated user to help determine if they qualify for a special discount:

```java

import org.springframework.security.core.context.SecurityContextHolder;

public class CashCardDiscountCalculator {
    public boolean currentUserQualifiesForDiscount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return qualifiesForDiscount(authentication.getName());
    }

    private boolean qualifiesForDiscount(String name) {  }
}

```
