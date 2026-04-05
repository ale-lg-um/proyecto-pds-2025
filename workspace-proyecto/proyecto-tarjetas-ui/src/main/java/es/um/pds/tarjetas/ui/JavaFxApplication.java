package es.um.pds.tarjetas.ui;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

//import es.um.pds.tarjetas.Main;
import es.um.pds.tarjetas.ui.controllers.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class JavaFxApplication extends Application {
	private ConfigurableApplicationContext contexto;
	
	@Override
	public void start(Stage stage) throws IOException {
		try {
			contexto = SpringApplication.run(AppConfig.class, new String[]{});
			SceneManager manager = contexto.getBean(SceneManager.class);
			manager.inicializar(stage);
			manager.showVentanaPrincipal();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void stop() {
		if(contexto != null) {
			contexto.close();
		}
		Platform.exit();
	}
}
