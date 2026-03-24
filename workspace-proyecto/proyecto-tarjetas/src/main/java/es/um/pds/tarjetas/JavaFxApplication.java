package es.um.pds.tarjetas;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {
	// Atributos
	private ConfigurableApplicationContext contextoApp;
	
	@Override
	public void init() {
		// Encender el motor de SpringBoot en segundo plano
		this.contextoApp = new SpringApplicationBuilder(ProyectoTarjetasApplication.class).run();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		// Cargar el tablero
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TableroView.fxml"));
		
		loader.setControllerFactory(contextoApp::getBean);
		Parent root = loader.load();
		
		// Configurar ventana
		stage.setTitle("Gestor de tableros");
		stage.setScene(new Scene(root, 1200, 1700)); // Tamaño inicial de la ventana
		stage.show();
	}
	
	@Override
	public void stop() {
		// Al cerrar la ventana, se apaga SpringBoot
		contextoApp.close();
		Platform.exit();
	}
}
