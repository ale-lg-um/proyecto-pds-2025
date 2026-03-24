package es.um.pds.tarjetas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;

@SpringBootApplication
public class ProyectoTarjetasApplication {

	public static void main(String[] args) {
		//SpringApplication.run(ProyectoTarjetasApplication.class, args);
		Application.launch(JavaFxApplication.class, args);
	}

}
