## Bootiful CAS-enabled web application

This is the simplest CASyfied Spring Boot application that there could be. It uses [official CAS Client](https://github.com/apereo/java-cas-client#spring-boot-autoconfiguration)

It could be used as a template to build more complex CAS-enabled Spring Boot apps, or simply as a quick tester for various CAS servers installations.

### To get started

* Make sure you have Java 8 installed (it won't work on Java versions less than 8)

* Clone this repository

* Verify dependency in `build.gradle` for _org.jasig.cas.client:cas-client-support-springboot_ is updated to latest version.

* Change 3 required URL properties in `src/main/resources/application.yml` pointing to the desired CAS server and client host. For example:

  ```yaml
  cas:
    #Required properties
    server-url-prefix: https://localhost:8143/cas
    server-login-url: https://localhost:8143/cas/login
    client-host-url: https://localhost:8443
  ```

* Change SSL settings in `src/main/resources/application.yml` pointing to your local keystore and truststore. For example:
 
 ```yaml
 server:
   port: 8443
   ssl:
     enabled: true
     key-store: /Users/dima767/.keystore
     key-store-password: changeit     
 ```
 
  > Note: you also might need to do the self-cert generation/importing dance into the JVM's trustore for this CAS client/server SSL handshake to 
  work properly. 

* From the command line run: `./gradlew clean bootRun`

* Visit `https://localhost:8443` in the web browser of choice and enjoy the CASyfied Spring Boot app! 
