package es.um.pds.tarjetas.ui.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class SceneManager {
	// Atributos
	private Stage stage;
	private Scene actual;
	private final ApplicationContext contextoSpring;
	
	public SceneManager(ApplicationContext contexto) {
		this.contextoSpring = contexto;
	}
	
	public void inicializar(Stage stage) {
		this.stage = stage;
	}
	
	public void showVentanaPrincipal() {
		cargarYMostrar("LoginView");
	}
	
	public void showTablero() {
		cargarYMostrar("TableroView");
	}
	
	public void showDashboard() {
		cargarYMostrar("DashboardView");
	}
	
	private void cargarYMostrar(String vista) {
	    try {
	        String rutaCompleta = "/views/" + vista + ".fxml";
	        System.out.println("Intentando cargar: " + rutaCompleta);
	        
	        // Verificar que el recurso existe
	        var recurso = getClass().getResource(rutaCompleta);
	        if (recurso == null) {
	            System.err.println("❌ ARCHIVO NO ENCONTRADO: " + rutaCompleta);
	            System.err.println("Ruta absoluta esperada: " + getClass().getResource("/views/").getPath());
	            throw new RuntimeException("El archivo FXML no existe: " + rutaCompleta);
	        }
	        
	        System.out.println("✅ Archivo encontrado: " + recurso);
	        
	        FXMLLoader loader = new FXMLLoader(recurso);
	        loader.setControllerFactory(contextoSpring::getBean);
	        
	        System.out.println("Cargando controlador...");
	        Parent root = loader.load();
	        System.out.println("✅ FXML cargado correctamente");
	        
	        if(actual == null) {
	            actual = new Scene(root, 1200, 700);
	            stage.setScene(actual);
	            stage.setTitle("Gestor de Tableros");
	            stage.show();
	        } else {
	            actual.setRoot(root);
	        }
	        
	    } catch(Exception e) {
	        System.err.println("❌ Error al cargar la vista: " + vista);
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    }
	}
}
