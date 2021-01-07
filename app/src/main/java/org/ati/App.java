package org.ati;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EntityScan("org.ati.core.model")
@ComponentScan({"org.ati.core.model", "org.ati.core.repository", "org.ati.core.service", "org.ati.core.validators", "org.ati.web"})
@EnableJpaRepositories("org.ati.core.repository")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
