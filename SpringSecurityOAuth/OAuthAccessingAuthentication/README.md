# Accessing Authentication in Spring MVC

In this lesson, you'll learn 3 ways to access the authenticated principal in Spring MVC controller methods.

Let's consider some use cases when you may need to look up the user.

* You need to know profile or other details about the principal
* You need a primary key or other identifier about the principal to formulate a query
* You need to declare if a principal has permission to perform the given action
* You need to propagate credentials to downstream services.

## Method Injection

You can get `Authentication` instance in any spring MVC handler method by including it as a method parameter.

```java
@GetMapping
public ResponseEntity<Iterable<CashCard>> findAll(Authentication authentication) {  }
```

When spring MVC invokes this handler method, it will look up the `Authentication` instance and supply it automatically.

> **Note:** In Spring MVC, a handler method is one that "handles" an HTTP request. Some examples are those annotated with `@RequestMapping`, `@GetMapping`.

When this happens, you can access the principal, the credentials, and the authorities in the method body as needed.
This includes being able to pass the user information down to service and repository layers in your application.

> For greatest flexibility `Autentication#getPrincipal` returns `Object`. This is helpful when integrating spring security with custom user representations.

However, using `Authentication#getPrincipal` directly can cause unwanted casting. To assist with that, spring security provides `@CurrentSecurityContext` which we'll talk about next.

## Principal type conversion

The `@CurrentSecurityContext` annotation allows you to remove some of the boilerplate around specific values like the principal from the current authentication.

As you already learned, you can call `Authentication#getPrincipal` yourself. Or, you can use @CurrentSecurityContext to have spring security handle the type conversion for you.

For example, with Bearer JWT authentication, `Authentication#getPrincipal` holds a JWT instance. Given that, you can get the underlying JWT instance by changing the earlier snippet to: 

```java
@GetMapping
public ResponseEntity<Iterable<CashCard>> findAll(@CurrentSecurityContext(expression = "authentication.principal") Jwt jwt) {  }
```

This is handy if you are needing to get JWT-specific information, like calling `Jwt#getIssuer` or `Jwt#getAudience`.

> *Note:* The `@CurrentSecurityContext` annotation is only processed by controller methods. Keep this in mind since from a language perspective, java will allow it on any method.

While this is a nice improvement, there is still boilerplate like the SpEL expression. It's a good security practice to remove duplication. You don't have to secure the code that don't write. 

Moreover, SpEL expressions are not compiled along with your code and so it's a good idea to keep SpEL expressions to a minimum since errors in such expressions might not be revealed until they are encountered at runtime. 
To help you with this, `@CurrentSecurityContext` also supports meta-annotations.

## Meta-Annotations

You can consolidate the repetitive nature of getting authentication information by creating a custom annotation and configuring it as  meta-annotation.

A *meta-annotation* is an annotation that aliases another annotation. 

You can create a custom annotation called `@CurrentOwner` that extracts the owner's name like so:

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@CurrentSecurityContext(expression="authentication.name")
public @interface CurrentOwner {
}
```

This annotation is now an alias for `@CurrentSecurityContext(expression="authentication.name")`. Now it can simplify the signature, like this:

```java
@GetMapping
public ResponseEntity<Iterable<CashCard>> findAll(@CurrentOwner String owner) {  }
```

## `@AuthenticationPrincipal`

Spring security ships with its own meta-like annotation for `@CurrentSecurityContext` called `@AuthenticationPrincipal`, which is equivalent to `@CurrentSecurityContext(expression="authentication.principal")`

---
<- [Home](../README.md)