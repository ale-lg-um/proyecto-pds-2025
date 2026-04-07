package es.um.pds.tarjetas.ui.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.application.common.exceptions.TableroBloqueadoException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
//import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
//import es.um.pds.tarjetas.ui.Configuracion;
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
	private final ServicioTablero servicioTablero;
	private final ServicioLista servicioLista;
	private final RepositorioListas repoListas;
	private final RepositorioTableros repoTableros;
	private final ApplicationContext contextoApp;
	private final ContextoUsuario contextoUsuario;
	private final SceneManager sceneManager;
	private int nListas = 0;
	
	private String actual;
	
	@FXML private HBox contenedorListas;
	
	// Inyectar servicio y contexto
	public TableroController(ServicioTablero servicioTablero, ServicioLista servicioLista, RepositorioListas repoListas, RepositorioTableros repoTableros, ApplicationContext contextoApp, ContextoUsuario contextoUsuario, SceneManager sceneManager) {
		this.servicioTablero = servicioTablero;
		this.servicioLista = servicioLista;
		this.repoListas = repoListas;
		this.repoTableros = repoTableros;
		this.contextoApp = contextoApp;
		this.contextoUsuario = contextoUsuario;
		this.sceneManager = sceneManager;
	}
	
	@FXML
	public void initialize() {
		// Se leen las líneas existentes de la base de datos
		// Se llama en bucle a instanciarListaVisual()
		
		System.out.println("Cargando el tablero principal...");
		this.actual = contextoUsuario.getIdTableroActual();
		if(this.actual == null) {
			System.err.println("Error: no se ha seleccionado ningún tablero");
			return;
		}
		TableroId id = TableroId.of(this.actual);
		
		try {
			repoTableros.buscarPorId(id).ifPresent(tablero -> {
				System.out.println("Tablero detectado: " + tablero.getNombre());
				
				repoListas.buscarPorTableroId(id).forEach(lista -> {
					instanciarListaVisual(new ListaDTO(lista));
				});
			});
		} catch(Exception e) {
			System.err.println("Error al cargar los datos del tablero desde la base de datos.");
			e.printStackTrace();
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
				//this.nListas = this.nListas + 1;
				//ListaId listaId = ListaId.of();
				System.out.println("▶ DEBUG - Nombre de la lista: " + nombre);
				System.out.println("▶ DEBUG - Email en contexto: " + contextoUsuario.getEmail());
				ListaDTO nueva = servicioLista.crearLista(actual, nombre, contextoUsuario.getEmail());
				
				//repoListas.guardar(nueva);
				instanciarListaVisual(nueva);
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
	private void instanciarListaVisual(ListaDTO lista) {
		try {
			System.out.println("Cargando vista de lista: " + lista.nombre());
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ListaView.fxml"));
			
			// JavaFX pide a Spring los controladores
			loader.setControllerFactory(contextoApp::getBean);
			
			VBox nodoLista = loader.load();
			
			// recuperar el controlador
			ListaController controlador = loader.getController();
			controlador.configurarLista(lista, this.actual);
			
			// Insertar la lista a la izquierda del botón de añadir lista
			int posicionBoton = contenedorListas.getChildren().size() - 1;
			contenedorListas.getChildren().add(posicionBoton, nodoLista);
		} catch(Exception e) {
			System.err.println("Error al cargar la vista de la lista: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@FXML
	public void accionVolverDashboard(ActionEvent evento) {
		System.out.println("Saliendo del tablero y volviendo a la pantalla de selección...");
		
		// Olvidar el tablero en el que estábamos
		contextoUsuario.setIdTableroActual(null);
		
		// Volver
		sceneManager.showDashboard();
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
