package es.um.pds.tarjetas.ui.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;
//import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.dto.EntryHistorialDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
//import es.um.pds.tarjetas.ui.Configuracion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;

@Controller
@Scope("prototype")
public class TableroController {
	// Atributos
	private final Map<String, VBox> nodosListas = new HashMap<>();
	private final Map<String, VBox> nodosTarjetas = new HashMap<>();
	
	private final ServicioTablero servicioTablero;
	private final ServicioLista servicioLista;
	private final ServicioHistorial servicioHistorial;
	private final RepositorioListas repoListas;
	private final RepositorioTableros repoTableros;
	private final ApplicationContext contextoApp;
	private final ContextoUsuario contextoUsuario;
	private final SceneManager sceneManager;
	private final TableroEventBridge eventBridge;
	private int nListas = 0;
	private String filtroActual = "";
	
	private String actual;
	private boolean bloqueado = false;
	
	@FXML private HBox contenedorListas;
	@FXML private VBox panelHistorial;
	@FXML private ListView<String> listaHistorial;
	@FXML private Button btnToggleHistorial;
	@FXML private Button btnBloquear;
	@FXML private TextField txtFiltroEtiqueta;
	
	// Inyectar servicio y contexto
	public TableroController(ServicioTablero servicioTablero, ServicioLista servicioLista, ServicioHistorial servicioHistorial, RepositorioListas repoListas, RepositorioTableros repoTableros, ApplicationContext contextoApp, ContextoUsuario contextoUsuario, SceneManager sceneManager, TableroEventBridge eventBridge) {
		this.servicioTablero = servicioTablero;
		this.servicioLista = servicioLista;
		this.servicioHistorial = servicioHistorial;
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
				
				// --- DEJAMOS QUE EL DOMINIO DECIDA EL ESTADO ---
				boolean estaBloqueado = tab.isBloqueado();
				this.bloqueado = estaBloqueado;
				
				if (estaBloqueado) {
					btnBloquear.setText("🔒 Tablero Bloqueado");
					btnBloquear.setDisable(true);
					
					// Calculamos el temporizador para el F5 automático
					LocalDateTime momentoFin = tab.getEstadoBloqueo().getHasta();
					if (momentoFin != null) {
						long milisRestantes = ChronoUnit.MILLIS.between(LocalDateTime.now(), momentoFin);
						if (milisRestantes > 0) {
							PauseTransition temporizador = new PauseTransition(Duration.millis(milisRestantes));
							temporizador.setOnFinished(event -> {
								System.out.println("⏰ Desbloqueo automático. Recargando...");
								sceneManager.showTablero(); 
							});
							temporizador.play();
						}
					}
				} else {
					btnBloquear.setText("🔒 Bloquear Tablero");
					btnBloquear.setDisable(false);
				}
				// -----------------------------------------------
				
				for(ListaId listaId : tab.getListas()) {
					Optional<Lista> listaOpt = repoListas.buscarPorId(listaId);
					if(listaOpt.isPresent()) {
						instanciarListaVisual(new ListaDTO(listaOpt.get()), bloqueado);
					}
				}
			}
			txtFiltroEtiqueta.textProperty().addListener((observable, valorAntiguo, valorNuevo) -> {
				filtroActual = valorNuevo.trim().toLowerCase();
				actualizarFiltroTarjetas();
			});
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
				
				instanciarListaVisual(nueva, bloqueado);
			} catch(Exception e) {
				mostrarError("Error", "No se pudo crear la lista: " + e.getMessage());
			}
		});
	}
	
	// Fusión de JavaFX con Spring
	private void instanciarListaVisual(ListaDTO lista, boolean tableroBloqueado) {
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
			
			controlador.setTableroBloqueado(tableroBloqueado);
			
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
	
	/*public void alCompletarTarjeta(TarjetaCompletada evento) {
		Platform.runLater(() -> {
			VBox nodo = nodosTarjetas.get(evento.tarjetaId().getId());
			VBox listaDest = nodosListas.get(evento.listaId().getId());
			
			if(nodo != null && listaDest != null) {
				((VBox) nodo.getParent()).getChildren().remove(nodo);
				listaDest.getChildren().add(nodo);
				System.out.println("Moviendo tarjeta a lista especial...");
			}
		});
	}*/
	
	public void alCompletarTarjeta(TarjetaCompletada evento) {
		System.out.println("🎯 TABLERO: Orden de mover recibida. Recargando pantalla...");
		
		Platform.runLater(() -> {
			try {
				// Simplemente le decimos al SceneManager que vuelva a cargar el FXML.
				// Esto destruirá la vista antigua y ejecutará el initialize() de nuevo,
				// pintando todo exactamente como está en la base de datos ahora mismo.
				sceneManager.showTablero();
				
				System.out.println("✅ Pantalla recargada con éxito.");
			} catch (Exception e) {
				System.out.println("❌ Error al recargar la pantalla: " + e.getMessage());
				e.printStackTrace();
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
	
	@FXML
	public void accionToggleHistorial(ActionEvent evento) {
		boolean isVisible = panelHistorial.isVisible();
		
		panelHistorial.setVisible(!isVisible);
		panelHistorial.setManaged(!isVisible);
		
		if(!isVisible) {
			btnToggleHistorial.setText("Ocultar Historial");
			cargarHistorial();
		} else {
			btnToggleHistorial.setText("Ver historial");
		}
	}
	
	@FXML
	public void accionBloquearTablero(ActionEvent evento) {
		TextInputDialog dialogo = new TextInputDialog("60");
		dialogo.setTitle("Bloquear Tablero");
		dialogo.setHeaderText("Bloqueo temporal del tablero");
		dialogo.setContentText("Minutos de bloqueo:");
		
		dialogo.showAndWait().ifPresent(minutosStr -> {
			try {
				Long minutos = Long.parseLong(minutosStr);
				LocalDateTime desde = LocalDateTime.now();
				LocalDateTime hasta = desde.plusMinutes(minutos);
				
				servicioTablero.bloquearTablero(this.actual, desde, hasta, "Bloqueo por el usuario", contextoUsuario.getEmail());
				sceneManager.showTablero();
			} catch (Exception e) {
				mostrarError("Error al bloquear", e.getMessage());
			}
		});
	}
	
	private void cargarHistorial() {
		listaHistorial.getItems().clear();
		
		try {
			// Pedir página 0 con un máximo de 20 entradas
			PageDTO<EntryHistorialDTO> pagina = servicioHistorial.consultarPorTablero(actual, 0, 20);
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
			
			for(EntryHistorialDTO entrada : pagina.contenido()) {
				String fecha = entrada.timestamp().format(formatter);
				String texto = "[" + fecha + "] " + entrada.usuario() + " -> " + entrada.detalles();
				listaHistorial.getItems().add(texto);
			}
		} catch(Exception e) {
			System.err.println("Erorr al cargar el historial" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void actualizarFiltroTarjetas() {
		System.out.println("Aplicando filtro: '" + filtroActual + "'");
		
		for(int i = 0; i < contenedorListas.getChildren().size(); i++) {
			var nodo = contenedorListas.getChildren().get(i);
			
			if(nodo instanceof VBox listaVBox) {
				VBox contenedorTarjetas = (VBox) listaVBox.lookup("#contenedorTarjetas");
				
				if(contenedorTarjetas != null) {
					for(var tarjeta : contenedorTarjetas.getChildren()) {
						if(tarjeta instanceof VBox tarjetaVBox) {
							boolean mostrar = true;
							
							if(!filtroActual.isEmpty()) {
								FlowPane contenedorEtiquetas = (FlowPane) tarjetaVBox.lookup("#contenedorEtiquetas");
								
								if(contenedorEtiquetas != null) {
									boolean tieneEtiqueta = contenedorEtiquetas.getChildren().stream()
											.filter(e -> e instanceof Label)
											.map(e -> ((Label) e).getText().toLowerCase())
											.anyMatch(texto -> texto.contains(filtroActual));
									
									mostrar = tieneEtiqueta;
								} else {
									mostrar = false;
								}
							}
							
							tarjetaVBox.setVisible(mostrar);
							tarjetaVBox.setManaged(mostrar);
						}
					}
				}
			}
		}
		
		System.out.println("✅ Filtro aplicado");
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
