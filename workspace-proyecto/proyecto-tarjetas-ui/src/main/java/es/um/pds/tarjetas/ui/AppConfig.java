package es.um.pds.tarjetas.ui;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import es.um.pds.tarjetas.ui.infrastructure.api.DashboardApiClient;
import es.um.pds.tarjetas.ui.infrastructure.api.ListaApiClient;
import es.um.pds.tarjetas.ui.infrastructure.api.LoginApiClient;
import es.um.pds.tarjetas.ui.infrastructure.api.PlantillaApiClient;
import es.um.pds.tarjetas.ui.infrastructure.api.TarjetaApiClient;

@SpringBootApplication(scanBasePackages = {"es.um.pds.tarjetas"})
@EntityScan(basePackages = {"es.um.pds.tarjetas"})
@EnableJpaRepositories(basePackages = {"es.um.pds.tarjetas"})
public class AppConfig {
	
	@Value("${spring.mail.host}")
	private String host;

	@Value("${spring.mail.port}")
	private int port;

	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.mail.password}")
	private String password;
	
	@Bean
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        
        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "false"); 

        return mailSender;
    }
	
	@Bean
	String baseURL() {
		return "http://localhost:8080";
	}

    @Bean
    DashboardApiClient dashboardApiClient(String baseURL) {
		return new DashboardApiClient(baseURL);
	}
    
    @Bean
    ListaApiClient listaApiClient(String baseURL) {
    	return new ListaApiClient(baseURL);
    }
    
    @Bean
    LoginApiClient loginApiClient(String baseURL) {
    	return new LoginApiClient(baseURL);
    }
    
    @Bean
    TarjetaApiClient tarjetaApiClient(String baseURL) {
    	return new TarjetaApiClient(baseURL);
    }
    
    @Bean
    PlantillaApiClient plantillaApiClient(String baseURL) {
    	return new PlantillaApiClient(baseURL);
    }
}
