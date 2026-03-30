package es.um.pds.tarjetas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

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

}