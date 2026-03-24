package es.um.pds.tarjetas.ui.controllers;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.exceptions.TableroBloqueadoException;
import es.um.pds.tarjetas.domain.model.lista.Lista;
import es.um.pds.tarjetas.domain.model.lista.ListaId;
import es.um.pds.tarjetas.domain.ports.input.tablero.ServicioGestionTablero;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Controller
public class TableroController {
	// Atributos
	private final ServicioGestionTablero servicioTablero;
	private final ApplicationContext contextoApp;
	
	@FXML private HBox contenedorListas;
	
	// Inyectar servicio y contexto
	public TableroController(ServicioGestionTablero servicioTablero, ApplicationContext contextoApp) {
		this.servicioTablero = servicioTablero;
		this.contextoApp = contextoApp;
	}
	
	@FXML
	public void initialize() {
		// Se leen las líneas existentes de la base de datos
		// Se llama en bucle a instanciarListaVisual()
		
		System.out.println("Cargando el tablero principal...");
		// Prueba
		try {
			Lista listaEjemplo = Lista.of(ListaId.of(), "Ejemplo de lista");
			instanciarListaVisual(listaEjemplo);
		} catch(Exception e) {
			System.err.println("Error al cargar el tablero: " + e.getMessage());
		}
	}
	
	@FXML
	public void accionAnadirLista(ActionEvent evento) {
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nueva lista");
		dialogo.setHeaderText("Añadir lista al tablero");
		dialogo.setContentText("Nombre:");
		
		Optional<String> resultado = dialogo.showAndWait();
		
		resultado.ifPresent(nombre -> {
			try {
				// Primero, llamar al servicio para guardar la lista
				// Pintar la lista en panalla
				System.out.println("Creando la lista: " + nombre);
				// instanciarListaVisual(nuevaLista)
			} catch(Exception e) {
				if(e instanceof TableroBloqueadoException) {
					mostrarError("Tablero bloqueado", "El tablero está bloqueado.");
				} else if(e instanceof ListaInvalidaException) {
					mostrarError("Lista inválida", "No se ha podido crear la lista: " + nombre);
				} else {
					mostrarError("Error", "Error desconocido: " + e.getMessage());
				}
			}
		});
	}
	
	// Fusión de JavaFX con Spring
	private void instanciarListaVisual(Lista lista) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ListaView.fxml"));
			
			// JavaFX pide a Spring los controladores
			loader.setControllerFactory(contextoApp::getBean);
			
			VBox nodoLista = loader.load();
			
			// recuperar el controlador
			ListaController controlador = loader.getController();
			controlador.configurarLista(lista);
			
			// Insertar la lista a la izquierda del botón de añadir lista
			int posicionBoton = contenedorListas.getChildren().size() - 1;
			contenedorListas.getChildren().add(posicionBoton, nodoLista);
		} catch(Exception e) {
			System.err.println("Error al cargar la vista de la lista: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// Mostrar ventanas de error
	private void mostrarError(String titulo, String mensaje) {
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}
}
