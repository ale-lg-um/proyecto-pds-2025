package es.um.pds.tarjetas.ui.controllers;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;

@Controller
@Scope("prototype")
public class DashboardController {
	// Atributos
	private final RepositorioTableros repoTableros;
	private final ServicioTablero servicioTablero;
	private final ContextoUsuario contextoUsuario;
	private final SceneManager sceneManager;
	
	@FXML private ListView<TableroItem> listaTableros;
	@FXML private Button btnCrear;
	@FXML private Button btnUnirsePorUrl;
	@FXML private Button btnEntrar;
	@FXML private Button btnBorrar;
	
	// Constructor
	public DashboardController(RepositorioTableros repoTableros, ServicioTablero servicioTablero, ContextoUsuario contextoUsuario, SceneManager sceneManager) {
		this.repoTableros = repoTableros;
		this.servicioTablero = servicioTablero;
		this.contextoUsuario = contextoUsuario;
		this.sceneManager = sceneManager;
	}
	
	@FXML
	public void initialize() {
		// Permitir selección multiple
		listaTableros.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// Escuchar cuando el usuario hace clic en un elemento de la lista para podoer seleccionarlo
		listaTableros.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TableroItem>) c -> {
			actualizarEstadoBotones();
		});
		
		cargarTableros();
	}
	
	// habilitar botones
	private void actualizarEstadoBotones() {
		int seleccionados = listaTableros.getSelectionModel().getSelectedItems().size();
		
		btnCrear.setDisable(seleccionados > 0); // Activo solo con 0 seleccionados
		btnEntrar.setDisable(seleccionados != 1); // Activo cuando tenemos uno solo seleccionado
		btnBorrar.setDisable(seleccionados == 0); // Activo cuando tenemos al menos uno seleccionado
	}
	
	private void cargarTableros() {
		listaTableros.getItems().clear();
		UsuarioId creador = UsuarioId.of(contextoUsuario.getEmail());
		System.out.println("Buscando tableros para el usuario: " + contextoUsuario.getEmail());
		
		List<TableroId> ids = repoTableros.listarIdsPorUsuario(creador);
		for(TableroId id : ids) {
			repoTableros.buscarPorId(id).ifPresent(tablero -> {
				listaTableros.getItems().add(new TableroItem(id.getId(), tablero.getNombre()));
			});
		}
		
		actualizarEstadoBotones();
	}
	
	@FXML
	public void accionCrearTablero(ActionEvent evento) {
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nuevo Tablero");
		dialogo.setHeaderText("Crear Tablero");
		dialogo.setContentText("Nombre del tablero:");
		
		dialogo.showAndWait().ifPresent(nombre -> {
			try {
				CrearTableroCmd cmd = new CrearTableroCmd(nombre, contextoUsuario.getEmail(), null, null, null);
				
				ResultadoCrearTableroDTO resultado = servicioTablero.crearTablero(cmd);
				
				Alert info = new Alert(AlertType.INFORMATION);
				info.setTitle("Tablero Creado");
				info.setHeaderText("Tablero creado con éxito");
				info.setContentText("Comparte esta URL para que otros puedan entrar:\n\n" + resultado.tokenUrl());
				info.showAndWait();
				
				cargarTableros();
			} catch(Exception e) {
				mostrarError("Error", "No se pudo crear el tablero: " + e.getMessage());;
			}
		});
	}
	/*
	public void accionCrearTablero() {
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nuevo Tablero");
		dialogo.setHeaderText("Crear un nuevo tablero");
		dialogo.setContentText("Nombre del tablero:");
		
		Optional<String> resultado = dialogo.showAndWait();
		resultado.ifPresent(nombre -> {
			if(!nombre.isBlank()) {
				TableroId nuevoId = TableroId.of();
				UsuarioId creador = UsuarioId.of(contextoUsuario.getEmail());
				String tokenUnico = UUID.randomUUID().toString();
				
				
				Tablero nuevo = Tablero.of(nuevoId, nombre, tokenUnico, creador);
				repoTableros.guardar(nuevo);
				
				cargarTableros();
			}
		});
	}*/
	
	@FXML
	public void accionEntrarTablero() {
		TableroItem seleccionado = listaTableros.getSelectionModel().getSelectedItem();
		if(seleccionado != null) {
			contextoUsuario.setIdTableroActual(seleccionado.getId());
			sceneManager.showTablero();
		}
	}
	
	@FXML
	public void accionUnirsePorUrl(ActionEvent evento) {
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Acceso por URL");
		dialogo.setHeaderText("Unirse a un tablero existente");
		dialogo.setContentText("Introduce el código de acceso:");

		dialogo.showAndWait().ifPresent(token -> {
			try {
				String tokenLimpio = token.trim();
				System.out.println("Buscando token: [" + tokenLimpio + "]"); // DEBUG
				
				repoTableros.buscarPorURL(tokenLimpio).ifPresentOrElse(tablero -> {
					
					System.out.println("¡Tablero encontrado!: " + tablero.getNombre()); // DEBUG
					
					contextoUsuario.setIdTableroActual(tablero.getIdentificador().getId());
					sceneManager.showTablero();
					
				}, () -> {
					System.out.println("Fallo: Token no encontrado en la BD."); // DEBUG
					mostrarError("Acceso Denegado", "El código introducido no existe.");
				});
				
			} catch (Exception e) {
				System.err.println("Error en la consulta: " + e.getMessage()); // DEBUG
				e.printStackTrace();
				mostrarError("Error", "Hubo un problema al buscar el tablero.");
			}
		});
	}
	
	@FXML
	public void accionBorrarTableros() {
		ObservableList<TableroItem> seleccionados = listaTableros.getSelectionModel().getSelectedItems();
		for(TableroItem item : seleccionados) {
			repoTableros.eliminarPorId(TableroId.of(item.getId()));
		}
		
		cargarTableros();
	}
	
	// Clase auxiliar
	private static class TableroItem {
		// Atributos
		private final String id;
		private final String nombre;
		
		public TableroItem(String id, String nombre) {
			this.id = id;
			this.nombre = nombre;
		}
		
		public String getId() {
			return this.id;
		}
		
		@Override
		public String toString() {
			return this.nombre;
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
