package es.um.pds.tarjetas.ui;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;

//@SpringBootApplication(scanBasePackages = {"es.um.pds.tarjetas"})
public class App {
	//public static ConfigurableApplicationContext contexto;
	public static void main(String[] args) {
		//contexto = SpringApplication.run(App.class, args);
		Application.launch(JavaFxApplication.class, args);
	}
}
