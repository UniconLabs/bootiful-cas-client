## Bootiful CAS-enabled web application

This is the simplest CASyfied Spring Boot application that there could be. It uses [cas client auto config support](https://github.com/Unicon/cas-client-autoconfig-support)

It could be used as a template to build more complex CAS-enabled Spring Boot apps, or simply as a quick tester for various CAS servers installations.

### To get started

* Clone this repository

* Change `server.url-prefix` property in `src/main/resources/application.yml` pointing to the desired CAS server. For example:

  ```yaml
  cas:
    server.url-prefix:  https://mycas.example.org/cas
    client.service-url: http://localhost:8080
  ```

* From the command line run: `./gradlew bootRun`

* Visit `http://localhost:8080` in the web browser of choice (based on the `client.service-url` property in `application.yml`) and enjoy your CASyfied app!
