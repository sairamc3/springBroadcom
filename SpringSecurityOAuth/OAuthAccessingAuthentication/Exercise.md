# Exercise from the lab

The use case is simple, we are going to configure the application as resource server. Add the resource server dependency in `pom.xml`.

**pom.xml**
```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
		</dependency>
```

This will configure the application as resource server. As a resource server, the application should be able to decrypt the JWT tokens.
For which it requires decryption keys, which should be obtained from the authorization server. 
But for this example, we are simplifying by not including the authorization server.
Hence, we statically save the public keys in the path `src/main/resources/authz.pub`, instead of retrieving them from the authorization server.

To point the application to fetch the tokens, configure the resource server to point to the static keys. 

**application.properties**
```properties
spring.security.oauth2.resourceserver.jwt.public-key-location=classpath:authz.pub
```

For checking all the logs related to spring security, enable it in the `application.properties`
```properties
logging.level.org.springframework.security=TRACE
```

Make sure that the existing test cases still work.

Now if you hit the application using httpie, you will get the response like. 

```bash
> http :8080/cashcards
HTTP/1.1 401 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Length: 0
Date: Tue, 21 Oct 2025 14:10:25 GMT
Expires: 0
Keep-Alive: timeout=60
Pragma: no-cache
Set-Cookie: JSESSIONID=C4A49692017025A73DC7D029FFEDDD6E; Path=/; HttpOnly
WWW-Authenticate: Bearer
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 0

```

> The authentication mechanism has changed from `Basic` to `Bearer`

In the exercise, there is a token that is provided as environment variable. It's a JWT token. 

When I decrypted the token using `jwt-cli` tool, here is the output. 

```bash
jwt decode $TOKEN

Token header
------------
{
  "typ": "JWT",
  "alg": "RS256"
}

Token claims
------------
{
  "aud": "https://cashcard.example.org",
  "exp": 1761089958,
  "iat": 1761053958,
  "iss": "https://issuer.example.org",
  "scope": [
    "cashcard:read",
    "cashcard:write"
  ],
  "sub": "sarah1"
}
```

The same jwt token has been used to send the http request. Here is the result. 

```bash
http :8080/cashcards "Authorization: Bearer $TOKEN"
HTTP/1.1 200 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Type: application/json
Date: Tue, 21 Oct 2025 14:21:10 GMT
Expires: 0
Keep-Alive: timeout=60
Pragma: no-cache
Transfer-Encoding: chunked
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 0

[
    {
        "amount": 123.45,
        "id": 99,
        "owner": "sarah1"
    },
    {
        "amount": 1.0,
        "id": 100,
        "owner": "sarah1"
    },
    {
        "amount": 150.0,
        "id": 101,
        "owner": "esuez5"
    }
]
```

> At this point of time, I don't know how to generate the JWT token, hence using the one from the exercise. It has expiry date 10 hours from the time it is generated, which is the lab start time. Hence, the token cannot be used after that time.  


