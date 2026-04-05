package es.um.pds.tarjetas.ui;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication(scanBasePackages = {"es.um.pds.tarjetas"})
@EntityScan(basePackages = {"es.um.pds.tarjetas"})
@EnableJpaRepositories(basePackages = {"es.um.pds.tarjetas"})
public class AppConfig {
	@Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl() {
        	@Override
        	public void send(SimpleMailMessage... simpleMessages) {
        		for(SimpleMailMessage m : simpleMessages) {
        			System.out.println("-----MENSAJE NUEVO-----");
        			System.out.println("Para: " + (m.getTo() != null ? String.join(",", m.getTo()) : ""));
        			System.out.println("Asunto: " + m.getSubject());
        			System.out.println("Mensaje:\n" + m.getText());
        			System.out.println("\n");
        		}
        	}
        };
    }
}
