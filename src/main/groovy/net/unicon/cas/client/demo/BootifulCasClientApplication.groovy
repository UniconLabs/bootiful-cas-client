package net.unicon.cas.client.demo

import org.apereo.cas.util.crypto.PrivateKeyFactoryBean
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource

@SpringBootApplication
class BootifulCasClientApplication {

    static main(args) {
        SpringApplication.run BootifulCasClientApplication, args
    }

    @Bean
    PrivateKeyFactoryBean credentialPrivateKey() {
        new PrivateKeyFactoryBean(algorithm: 'RSA', location: new ClassPathResource('credential-private.p8'))
    }
}
