# Secure Defaults

> Secure by default and Principle of least Privilege

## Spring security defaults

When spring security is on the classpath, spring boot works to configure your application with the following default for tea REST API

* [Requires authentication for all requests](#requires-authentication-for-all-requests)
* [Responds with secure headers for all requests](#responds-with-secure-headers-for-all-requests)
* [Requires CSRF mitigation for all requests with side-effects](#requires-csrf-mitigation-for-all-requests-with-side-effects)
* [Allows for HTTP Basic authentication with a default user](#allows-for-http-basic-authentication-with-a-default-user)
* [Responds RESTfully to security failures](#responds-restfully-to-security-failures)
* [Protects against malicious request with an application firewall](#protects-against-malicious-request-with-an-application-firewall)

## Requires authentication for all requests

* Whether the endpoint is you-generated or Boot-generated, all requests across all dispatches require authentication
* Regardless of the nature of the endpoint, spring security applies a *Servlet Filter* that inspects every request and rejects if the request is unauthenticated
* In spring terms, spring mvc constitutes a single servlet, spring security constitutes a set of filters, and spring boot ships with an embeded container that performs the various dispatches needed to service a single request

### Security Benefits
* For all the endpoints, spring security's servlet filter intercepts the request before any servlet can process it.
* When you include spring security, even non-existent api's will return `401 Unauthorized` instead of `404 Not Found`. 
* It is in accordance with the **Principle of Least Privilege**
* For security purpose, even which URIs are valid is privileged information. 
  * *You can imagine if someone requested index.jsp or /admin. If spring security returned a `404` in those cases instead of `401`, that would mean `404` is a hint to a bad actor that the given endpoint does not exist.*
  

## Responds with secure headers for all requests

* Whether the request is authenticated or not, spring security responds with certain headers by default. Each header defaults to the most secure value available.

### Caching Headers
* One class of browser based vulnerabilities is that HTTP responses get cached in the browser. 
* Spring security applies secure setting for Cache-Control and other headers to mitigate this class of vulnerabilities.

### Strict Transport Security Header

* This header forces a browser to upgrade requests to HTTPS for a specified period of time. 
  * > Since this is intended for HTTPS request, it isn't written by default for an HTTP request. Given that, you might not see it in your local testing over HTTP. 

### Content Type Options
* The content type header `X-Content-Type-Options`, tells browsers to not try to guess the content type of a response. 
* Spring security addresses this by issuing a secure setting for the header by default. 

## Requires CSRF mitigation for all requests with side-effects
* Another place where REST APIs are at risk is the ability for third-party web sites to make requests to them without the user's consent. 
* This is possible since browsers, by default, send all cookies and HTTP Basic authentication details automatically to any non-XHR endpoint. 
* Browsers, by default, will send all of coookies and HTTP basic credentials to it by default as well. This means that if your user is logged in, a third-party application can commdn your REST API without further protection. 
* Spring Security automatically protects these endpoints with side-effect, like POSTs, PUTs, and DELETEs. It does this by sending a special token to the client that it should use on subsequent requests. 
* The token is transmitted in such a way that third parties cannot see it. So when it is returned, Spring security believes that it is legitimately from the client 

## Allows for HTTP Basic authentication with a default user
* Spring security generates a default user called `user`. 
* It's password is generated, though, on each startup.
* The generated password will be available in the logs during the startup. 

## Responds RESTfully to security failures
* Responds with `401 Unauthorized`, when credentials are missing/wrong from the request.
* It will send the appropriate headers to indicate the kind of authentication that is expected

## Protects against malicious request with an application firewall
* There are myriad other ways that a bad actor may try and misuse your REST API. With many of them, the best practice is to reject the request outright.
* Spring security helps you with this by adding an application firewall that, by default, rejects requests that contain double encoding and several unsafe characters like carriage returns and linefeeds. 
* Using spring security's firewall helps mitigate entire classes of vulnerabilities.

## Commands Used

```bash
http -a user:df5187fc-68de-4012-9b31-049d4fcc0737 :8080/cashcards
http -a user:df5187fc-68de-4012-9b31-049d4fcc0737 :8080/cashcards "Accept:applicaton/json" amount=1 owner=sarah1

```

## Trying to hack the application

```bash
http :8080/admin
HTTP/1.1 401 
```
In the below example '/' is represented by '%2F'. Spring's firewall rejects the request by default. And hence the status is `400 Bad Request`
```bash
http :8080/admin%2Faction
HTTP/1.1 400 
```



