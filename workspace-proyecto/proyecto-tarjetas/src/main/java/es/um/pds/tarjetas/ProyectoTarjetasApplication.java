package es.um.pds.tarjetas;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.PuertoEnvioEmail;

//import es.um.pds.tarjetas.ui.App;
//import javafx.application.Application;

@SpringBootApplication(scanBasePackages = {"es.um.pds.tarjetas"})
public class ProyectoTarjetasApplication {
	public static ConfigurableApplicationContext contexto;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		contexto = SpringApplication.run(ProyectoTarjetasApplication.class, args);
		
		//Application.launch(App.class, args);
	}
	
	// Prueba del correo:
    @Bean
    CommandLineRunner testEmail(PuertoEnvioEmail puertoEnvioEmail) {
        return args -> {
            puertoEnvioEmail.enviarEmail(
                UsuarioId.of("danielmarinsanchez6@gmail.com"),
                "Test desde Spring Boot",
                "Hola, este es un email de prueba"
            );
        };
    }

}