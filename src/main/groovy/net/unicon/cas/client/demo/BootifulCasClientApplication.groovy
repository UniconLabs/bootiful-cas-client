package net.unicon.cas.client.demo

import org.apereo.cas.util.crypto.PrivateKeyFactoryBean
import org.apereo.cas.util.crypto.PublicKeyFactoryBean
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
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

    @Bean
    PrivateKeyFactoryBean decryptionPrivateKey() {
        new PrivateKeyFactoryBean(algorithm: 'RSA', location: new FileSystemResource('/etc/cas/config/keys/jwt-decryption-private.key'))
    }

    @Bean
    PublicKeyFactoryBean signingPublicKey() {
        new PublicKeyFactoryBean(algorithm: 'RSA', resource: new FileSystemResource('/etc/cas/config/keys/jwt-signing-public.key'))
    }
}
