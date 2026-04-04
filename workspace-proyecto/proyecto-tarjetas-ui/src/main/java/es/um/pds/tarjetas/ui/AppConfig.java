package es.um.pds.tarjetas.ui;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication(scanBasePackages = {"es.um.pds.tarjetas"})
@EntityScan(basePackages = {"es.um.pds.tarjetas"})
@EnableJpaRepositories(basePackages = {"es.um.pds.tarjetas"})
public class AppConfig {
	@Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}
