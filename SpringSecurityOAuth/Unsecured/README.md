# Unsecured application

> Spring security has not been added into the project

## Observations:

* Any one having access to the network can access the application
* There is no way to track who is accessing the application
* An unknown user can access the application and hit an api, if the api is incorrect, it is returning `404 NOT FOUND`.
* Because of which the attacker can identify the underlying technology with few hits 

## Http testing

```bash
http :8080/cashcards
http :8080/cashcards/99
```

---
<- [Home](../README.md)
