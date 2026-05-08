package es.um.pds.tarjetas.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.application.dto.PlantillaDTO;
import es.um.pds.tarjetas.application.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.application.dto.TableroDTO;
import es.um.pds.tarjetas.ui.infrastructure.api.DashboardApiClient;
import es.um.pds.tarjetas.ui.infrastructure.api.PlantillaApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Controller
@Scope("prototype")
public class DashboardController {
	// Atributos

	private final PlantillaApiClient plantillaApi;
	private final DashboardApiClient dashboardApi;
	private final ContextoUsuario contextoUsuario;
	private final SceneManager sceneManager;
	
	@FXML private ListView<TableroItem> listaTableros;
	@FXML private Button btnCrear;
	@FXML private Button btnUnirsePorUrl;
	@FXML private Button btnEntrar;
	@FXML private Button btnBorrar;
	
	// Constructor

	public DashboardController(PlantillaApiClient plantillaApi, DashboardApiClient dashboardApi, ContextoUsuario contextoUsuario, SceneManager sceneManager) {
		this.plantillaApi = plantillaApi;
		this.dashboardApi = dashboardApi;
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
		try {
		listaTableros.getItems().clear();
		System.out.println("Buscando tableros para el usuario: " + contextoUsuario.getEmail());
		
		List<TableroDTO> tablerosDto = dashboardApi.obtenerTableros(contextoUsuario.getTokenSesion());
		
		for(TableroDTO dto : tablerosDto) {
			TableroItem item = new TableroItem(dto.id(), dto.nombre());
			listaTableros.getItems().add(item);
		}
				
		actualizarEstadoBotones();
		} catch(Exception e) {
			mostrarError("Error", "No se pueden obtener los tableros: " + e.getMessage());
		}
	}
	
	@FXML
	public void accionCrearTablero(ActionEvent evento) {
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nuevo Tablero");
		dialogo.setHeaderText("Crear Tablero");
		dialogo.setContentText("Nombre del tablero:");
		
		dialogo.showAndWait().ifPresent(nombre -> {
			try {
				ResultadoCrearTableroDTO resultado = dashboardApi.crearTablero(nombre, contextoUsuario.getEmail(), null, null, null, contextoUsuario.getTokenSesion());
				
				Alert info = new Alert(AlertType.INFORMATION);
				info.setTitle("Tablero Creado");
				info.setHeaderText("Tablero creado con éxito");
				info.setContentText("Comparte esta URL para que otros puedan entrar:\n\n" + resultado.tokenUrl());
				info.showAndWait();
				
				cargarTableros();
			} catch(Exception e) {
				mostrarError("Error", "No se pudo crear el tablero: " + e.getMessage());
			}
		});
	}
	
	@FXML
	public void accionCrearPlantilla(ActionEvent evento) {
		Dialog<Void> dialogo = new Dialog<>();
		dialogo.setTitle("Plantillas");
		dialogo.setHeaderText("Gestión de plantillas");
		
		ButtonType btnCerrar = new ButtonType("Cerrar", ButtonData.CANCEL_CLOSE);
		dialogo.getDialogPane().getButtonTypes().add(btnCerrar);
		
		VBox contenedor = new VBox();
		contenedor.setSpacing(12);
		contenedor.setPadding(new Insets(15));
		
		Label lblTitulo = new Label("Plantillas disponibles");
		
		ListView<PlantillaItem> listaPlantillas = new ListView<>();
		listaPlantillas.setPrefWidth(420);
		listaPlantillas.setPrefHeight(250);
		

		Button btnCrearTablero = new Button("Crear tablero con plantilla seleccionada");
    	btnCrearTablero.setDisable(true);
    	btnCrearTablero.setMaxWidth(Double.MAX_VALUE);

    	Button btnImportarYaml = new Button("Importar plantilla YAML...");
    	btnImportarYaml.setMaxWidth(Double.MAX_VALUE);
    	
    	contenedor.getChildren().addAll(lblTitulo, listaPlantillas, btnCrearTablero, btnImportarYaml);
    	
    	dialogo.getDialogPane().setContent(contenedor);
    	
    	cargarPlantillasEnLista(listaPlantillas);
    	
    	listaPlantillas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
    		btnCrearTablero.setDisable(seleccionado == null);
    	});
    	
    	btnCrearTablero.setOnAction(e -> {
    		PlantillaItem seleccionada = listaPlantillas.getSelectionModel().getSelectedItem();
    		
    		if(seleccionada == null) {
    			mostrarError("Error", "Selecciona una plantilla.");
    			return;
    		}
    		
    		crearTableroDesdePlantilla(seleccionada);
    	});
    	
    	btnImportarYaml.setOnAction(e -> {
    		importarPlantillaYaml(listaPlantillas);
    	});
    	dialogo.showAndWait();
	}
	
	private void cargarPlantillasEnLista(ListView<PlantillaItem> listaPlantillas) {
		try {
			List<PlantillaDTO> plantillas = plantillaApi.obtenerTodas(contextoUsuario.getTokenSesion());
			var items = FXCollections.<PlantillaItem>observableArrayList();
			
			for(PlantillaDTO plantilla : plantillas) {
				items.add(new PlantillaItem(plantilla.id(), plantilla.nombre()));
			}
			
			listaPlantillas.setItems(items);
		} catch (Exception e) {
			mostrarError("Error", "No se pudieron cargar las plantillas: " + e.getMessage());
        	e.printStackTrace();
		}
	}
	
	private void crearTableroDesdePlantilla(PlantillaItem plantilla) {
		TextInputDialog dialogoNombre = new TextInputDialog(plantilla.getNombre());
		dialogoNombre.setTitle("CrearTablero");
		dialogoNombre.setHeaderText("Crear tablero desde plantilla seleccionada.");
		dialogoNombre.setContentText("Nombre del nuevo tablero:");
		
		dialogoNombre.showAndWait().ifPresent(nombre -> {
			try {
				if (nombre == null || nombre.isBlank()) {
					mostrarError("Error", "El nombre del tablero no puede estar vacío.");
                	return;
				}
				
				PlantillaDTO plantillaDto = plantillaApi.obtenerPorId(plantilla.getId(), contextoUsuario.getTokenSesion());
				ResultadoCrearTableroDTO resultado = dashboardApi.crearTablero(nombre.trim(), contextoUsuario.getEmail(), plantilla.getId(), plantilla.getNombre(), plantillaDto, contextoUsuario.getTokenSesion());
				

				Alert info = new Alert(AlertType.INFORMATION);
				info.setTitle("Tablero creado");
				info.setHeaderText("Tablero creado desde plantilla");
				info.setContentText(
						"Tablero creado correctamente:\n\n"
								+ resultado.nombre()
								+ "\n\nURL de acceso:\n"
								+ resultado.tokenUrl()
						);
				info.showAndWait();
				
				cargarTableros();
			} catch(Exception e) {
				mostrarError("Error", "No se pudo crear el tablero desde la plantilla: " + e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	private void importarPlantillaYaml(ListView<PlantillaItem> listaPlantillas) {
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Importar plantilla YAML");
		
		filechooser.getExtensionFilters().add(new ExtensionFilter("Archivos YAML", "*.yaml", "*.yml"));
		File archivo = filechooser.showOpenDialog(listaTableros.getScene().getWindow());
		
		if(archivo == null) {
			return;
		}
		
		try {
			String yaml = Files.readString(archivo.toPath(), StandardCharsets.UTF_8);
			
			PlantillaDTO plantilla = plantillaApi.crearPlantilla(yaml, contextoUsuario.getTokenSesion());
			listaPlantillas.getItems().add(new PlantillaItem(plantilla.id(), plantilla.nombre()));
			

			Alert info = new Alert(AlertType.INFORMATION);
			info.setTitle("Plantilla importada");
			info.setHeaderText("Plantilla importada correctamente");
			info.setContentText("Plantilla: " + plantilla.nombre());
        	info.showAndWait();
		} 
		catch (IOException e) {
	        mostrarError("Error", "No se pudo leer el archivo YAML.");
	        e.printStackTrace();

	    } catch (Exception e) {
	        mostrarError("Plantilla inválida", e.getMessage());
	        e.printStackTrace();
	    }
	}
	
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
				
				TableroDTO tablero = dashboardApi.obtenerTableroPorUrl(tokenLimpio, token);
				contextoUsuario.setIdTableroActual(tablero.id());
				sceneManager.showTablero();	
			} catch (Exception e) {
				System.err.println("Error en la consulta: " + e.getMessage()); // DEBUG
				e.printStackTrace();
				mostrarError("Error", "Hubo un problema al buscar el tablero.");
			}
		});
	}
	
	@FXML
	public void accionBorrarTableros() {
		try {
			ObservableList<TableroItem> seleccionados = listaTableros.getSelectionModel().getSelectedItems();
			for(TableroItem item : seleccionados) {
				dashboardApi.eliminarTablero(item.getId(), contextoUsuario.getTokenSesion());
			}
			
			cargarTableros();
		} catch(Exception e) {
			mostrarError("Error", "Hubo un problema al eliminar un tablero: " + e.getMessage());
		}
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
	
	private static class PlantillaItem {
		private final String id;
		private final String nombre;
		
		public PlantillaItem(String id, String nombre) {
			this.id = id;
			this.nombre = nombre;
		}
		
		public String getId() {
			return this.id;
		}
		
		public String getNombre() {
			return this.nombre;
		}

		@Override
		public String toString() {
			return nombre;
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
