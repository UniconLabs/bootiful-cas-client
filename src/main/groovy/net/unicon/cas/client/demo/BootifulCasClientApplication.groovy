package net.unicon.cas.client.demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class BootifulCasClientApplication {

    static main(args) {
        SpringApplication.run BootifulCasClientApplication, args
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.build()
    }
}
