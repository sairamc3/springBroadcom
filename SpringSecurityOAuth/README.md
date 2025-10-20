# Adding Authentication
* The caller's identity
* The principal identity
* The request's integrity

## Authentication process
1. It parses the request material into a credential
2. it tests the credential
3. If the credential passes it translates that credential into a principal and authorities

You can see these three steps in action:

1. Spring security decodes the base64 encoded username and password. the password is credential in this case
2. It tests this username and password against a user store. Specifically, with passwords, it hashes the password and compares it to the user's password hash
3. If the passwords match, it loads the corresponding user and permissions and stores them in its security context. The resulting user is the principal that we mentioned earlier. 

## Limits of HTTP Basic
1. Long-term credentials
   * When is the last time that you have changed the password. 
   * Any one can impersonate you, as long as they have the username and password and they are valid.
2. Authorization Bypass, and
    * When you give your REST API username and password to a third party client, that application is now in possession of your username and password.
3. Sensitive Data Exposure
    * The HTTP Basic is stateless. 
    * Whenever a third-party client application calls the REST API, it needs to hand over your username and password each and every time you make an HTTP Request. 
    * It also means, the client application needs to hold your username and password in plain text somewhere so that it can hand them repeatedly to your REST API.