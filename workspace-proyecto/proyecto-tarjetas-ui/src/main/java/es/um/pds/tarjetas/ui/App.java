package es.um.pds.tarjetas.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class App extends Application{
    public static void main(String[] args) {
        //System.out.println("Hello World!");
    	launch();
    }
	
	@Override
	public void start(Stage stage) throws IOException {
		Configuracion configuracion = new ConfiguracionImpl();
		Configuracion.setInstancia(configuracion);
		configuracion.getSceneManager().inicializar(stage);
	}
}
