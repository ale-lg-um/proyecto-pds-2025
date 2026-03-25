package es.um.pds.tarjetas.ui.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
	// Atributos
	private Stage stage;
	private Scene actual;
	
	public void inicializar(Stage stage) {
		this.stage = stage;
	}
	
	public void showVentanaPrincipal() {
		cargarYMostrar("TableroView");
	}
	
	private void cargarYMostrar(String vista) {
		try {
			Parent root = loadFXML(vista);
			if(actual == null) {
				actual = new Scene(root, 1200, 1700);
				stage.setScene(actual);
				stage.setTitle("Tablero");
				stage.show();
			} else {
				actual.setRoot(root);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Parent loadFXML(String vista)throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + vista + ".fxml"));
		return loader.load();
	}
}
