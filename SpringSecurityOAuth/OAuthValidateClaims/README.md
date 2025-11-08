# Validate Claims

Customizing the authentication process, specifically regarding validating JWT claims

## Authenticating a JWT

Authentication process:
1. Parse the credentials
2. **Validate the credentials**
3. Construct the corresponding principal and authorities

## Validating a JWT

By default, spring security authenticates each JWT by:
1. Validating the signature, and 
2. Checking that the current time is between the timestamps in the `iat` (Issued At) and `exp` (Expires At) claims

In addition to the defaults, spring security can validate the issuer (the `iss` claim) and the audience (the `aud` claim).
Since there is no way that spring security can know these values by default, they both need to be configured. 

Boot provides properties to simplify adding these two validation steps. 

The first is  `issuer-uri`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://issuer.example.org
```

And the second is `audiences`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          audiences: https://cashcard.example.org
```

* `issuer-uri` represents the endpoint that is minting the JWT, something like the 'From' address in an email.
  * This property indicates that the application only trusts JWTs "From" `https://issuer.example.org`
* `audiences` represents the endpoint that is receiving the JWT, something like the "To" address in an email. 
  * This property is about application's discipline to only "read email" that is sent "To" it.

## Custom Validation

If you want to customize the validation further, In general you can look at `AuthenticationManager` as we already talking about it in 
the [Big Picture](../BigPicture.md) lesson.

For authenticating JWTs, there is more specific component called `JwtDecoder`. The class hierarchy is like this:

`AuthenticationManager` has `JwtAuthenticationProvider` has `JwtDecoder`

Each `JwtDecoder` instance takes care of verifying the signature and validating the claims.
Because Spring Security uses the Nimbus library by default, the default implementation of `JwtDecoder` is called
`NimbusJwtDecoder`.

You can specify custom validation steps by creating your own `NimbusJwtDecoder` lke so:

```java
@Bean
JwtDecoder jwtDecoder(String issuer, String audience) {
    OAuth2TokenValidator<Jwt> defaults = JwtValidators.createDefaultWithIssuer(issuer);
    OAuth2TokenValidator<Jwt> audiences = new JwtClaimValidator<List<String>>(AUD,
        (aud) -> aud != null && aud.contains(audience));
    OAuth2TokenValidator<Jwt> all = new DelegatingOAuth2TokenValidator<>(defaults, audiences);
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
    jwtDecoder.setOAuth2TokenValidator(all);
    return jwtDecoder;
}

```

> **Note:** this replaces the `issuer-uri` and `audiences` boot properties

Spring security ships with both a `JwtDecoder` and `JwtEncoder` API

# Lab

We are going to use Spring boot properties and write tests that mint their own token to confirm spring security's validation rules.

1. A new test class has been added. It has the configurations to create the JWT token. [CashCardSpringSecurityTests.java](src/test/java/com/example/CashCards/CashCardSpringSecurityTests.java)
2. **Minting Tokens in our tests**
