package net.unicon.cas.client.demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@SpringBootApplication
class BootifulCasClientApplication {

    static main(args) {
        SpringApplication.run BootifulCasClientApplication, args
    }

    @Configuration
    static class WebConfig implements WebMvcConfigurer {
        @Override
        void addInterceptors(InterceptorRegistry registry) {
           registry.addInterceptor(new DemoHandlerInterceptor())
        }
    }
}
