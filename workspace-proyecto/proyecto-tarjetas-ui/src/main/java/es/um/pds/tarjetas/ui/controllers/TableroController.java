package es.um.pds.tarjetas.ui.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.application.common.exceptions.TableroBloqueadoException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCompletada;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
//import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import javafx.application.Platform;
//import es.um.pds.tarjetas.ui.Configuracion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

@Controller
@Scope("prototype")
public class TableroController {
	// Atributos
	private final Map<String, VBox> nodosListas = new HashMap<>();
	private final Map<String, VBox> nodosTarjetas = new HashMap<>();
	
	private final ServicioTablero servicioTablero;
	private final ServicioLista servicioLista;
	private final RepositorioListas repoListas;
	private final RepositorioTableros repoTableros;
	private final ApplicationContext contextoApp;
	private final ContextoUsuario contextoUsuario;
	private final SceneManager sceneManager;
	private final TableroEventBridge eventBridge;
	private int nListas = 0;
	
	private String actual;
	
	@FXML private HBox contenedorListas;
	
	// Inyectar servicio y contexto
	public TableroController(ServicioTablero servicioTablero, ServicioLista servicioLista, RepositorioListas repoListas, RepositorioTableros repoTableros, ApplicationContext contextoApp, ContextoUsuario contextoUsuario, SceneManager sceneManager, TableroEventBridge eventBridge) {
		this.servicioTablero = servicioTablero;
		this.servicioLista = servicioLista;
		this.repoListas = repoListas;
		this.repoTableros = repoTableros;
		this.contextoApp = contextoApp;
		this.contextoUsuario = contextoUsuario;
		this.sceneManager = sceneManager;
		this.eventBridge = eventBridge;
	}
	
	@FXML
	public void initialize() {
		// Se leen las líneas existentes de la base de datos
		// Se llama en bucle a instanciarListaVisual()
		
		/*System.out.println("Cargando el tablero principal...");
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
		}*/
		
		eventBridge.conectarConPantalla(this::alCompletarTarjeta);
		
		System.out.println("Cargando el tablero principal...");
		this.actual = contextoUsuario.getIdTableroActual();
		
		if(this.actual == null) {
			System.err.println("Error: no se ha seleccionado ningún tablero");
			return;
		}
		
		TableroId id = TableroId.of(this.actual);
		
		try {
			Optional<Tablero> tableroOpt = repoTableros.buscarPorId(id);
			if(tableroOpt.isPresent()) {
				Tablero tab = tableroOpt.get();
				System.out.println("Tablero detectado: " + tab.getNombre());
				
				for(ListaId listaId : tab.getListas()) {
					Optional<Lista> listaOpt = repoListas.buscarPorId(listaId);
					if(listaOpt.isPresent()) {
						instanciarListaVisual(new ListaDTO(listaOpt.get()));
					}
				}
			}
		} catch(Exception e) {
			System.err.println("Error al cargar los datos del tablero desde la base de datos");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void accionAnadirLista(ActionEvent evento) {
		/*TextInputDialog dialogo = new TextInputDialog();
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
		});*/
		
		// Buscar si hay una lista especial ya
		boolean yaHayEspecial = repoListas.buscarPorTableroId(TableroId.of(this.actual)).stream()
				.anyMatch(l -> l.isEspecial());
		
		Dialog<Pair<String, Boolean>> dialogo = new Dialog<>();
		dialogo.setTitle("Nueva lista");
		dialogo.setHeaderText("Crear una nueva lista");
		
		ButtonType btnAceptar = new ButtonType("Crear", ButtonData.OK_DONE);
		dialogo.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));
		
		TextField txtNombre = new TextField();
		txtNombre.setPromptText("Nombre de la lista");
		
		ComboBox<String> cbTipo = new ComboBox<>();
		cbTipo.getItems().addAll("NORMAL", "ESPECIAL");
		cbTipo.getSelectionModel().selectFirst();
		
		grid.add(new Label("Nombre: "), 0, 0);
		grid.add(txtNombre, 1, 0);
		
		// Añadir la selección de tipo si no existe ya una lista especial
		if(!yaHayEspecial) {
			grid.add(new Label("Tipo: "), 0, 1);
			grid.add(cbTipo, 1, 1);
		}
		
		dialogo.getDialogPane().setContent(grid);
		
		dialogo.setResultConverter(dialogButton -> {
			if(dialogButton == btnAceptar) {
				boolean esEspecial = !yaHayEspecial && cbTipo.getValue().equals("ESPECIAL");
				return new Pair<>(txtNombre.getText(), esEspecial);
			}
			return null;
		});
		
		dialogo.showAndWait().ifPresent(resultado -> {
			try {
				String nombre = resultado.getKey();
				boolean marcarComoEspecial = resultado.getValue();
				String email = contextoUsuario.getEmail();
				
				ListaDTO nueva = servicioLista.crearLista(this.actual, nombre, email);
				
				if(marcarComoEspecial) {
					servicioLista.definirListaEspecial(actual, nueva.id(), email);
					// Recargar para que la vista sepa que la lista es especial
					nueva = new ListaDTO(nueva.id(), nueva.nombre(), true, nueva.limite(), nueva.tarjetaIds(), nueva.prerrequisitoIds());
				}
				
				instanciarListaVisual(nueva);
			} catch(Exception e) {
				mostrarError("Error", "No se pudo crear la lista: " + e.getMessage());
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
			
			VBox contenedorInterno = (VBox) nodoLista.lookup("#contenedorTarjeta");
			nodosListas.put(lista.id(), contenedorInterno);
			
			// recuperar el controlador
			ListaController controlador = loader.getController();
			
			controlador.setOnTarjetaCreada((id, nodo) -> {
				nodosTarjetas.put(id, nodo);
			});
			controlador.configurarLista(lista, this.actual);
			
			// Borrar la lista de la pantalla
			controlador.setFuncionEliminarDeLaVista(() -> {
				contenedorListas.getChildren().remove(nodoLista);
			});
			
			// Insertar la lista a la izquierda del botón de añadir lista
			int posicionBoton = contenedorListas.getChildren().size() - 1;
			contenedorListas.getChildren().add(posicionBoton, nodoLista);
		} catch(Exception e) {
			System.err.println("Error al cargar la vista de la lista: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void alCompletarTarjeta(TarjetaCompletada evento) {
		Platform.runLater(() -> {
			VBox nodo = nodosTarjetas.get(evento.tarjetaId().getId());
			VBox listaDest = nodosListas.get(evento.listaId().getId());
			
			if(nodo != null && listaDest != null) {
				((VBox) nodo.getParent()).getChildren().remove(nodo);
				listaDest.getChildren().add(nodo);
				System.out.println("Moviendo tarjeta a lista especial...");
			}
		});
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
