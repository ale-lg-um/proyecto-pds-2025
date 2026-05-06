package es.um.pds.tarjetas.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ChecklistDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.EtiquetaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ItemChecklistDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TareaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

@Controller
@Scope("prototype")
public class TarjetaController {
	// Atributos
	private final ApplicationContext contextoApp;
	private final ContextoUsuario contextoUsuario;
	private final ServicioTarjeta servicioTarjeta;
	private final ObservableList<ItemChecklistDTO> checklistObservable = FXCollections.observableArrayList();
	private TarjetaDTO tarjeta;
	private String descripcionActual;
	
	@FXML private Label lblTitulo;
	@FXML private FlowPane contenedorEtiquetas;
	@FXML private TextArea txtDescripcion;
	@FXML private VBox contenedorChecklist;
	@FXML private Button btnCompletar;
	@FXML private Button btnChecklist;
	
	public TarjetaController(ApplicationContext contextoApp, ContextoUsuario contextoUsuario, ServicioTarjeta servicioTarjeta) {
		this.contextoApp = contextoApp;
		this.contextoUsuario = contextoUsuario;
		this.servicioTarjeta = servicioTarjeta;
	}
	
	@FXML
	public void initialize() {
	    txtDescripcion.focusedProperty().addListener((obs, teniaFoco, tieneFoco) -> {
	    	if(!tieneFoco) {
	    		guardarDescripcionTarea();
	    	}
	    });
		
		checklistObservable.addListener((ListChangeListener<ItemChecklistDTO>) c -> {
	        redibujarChecklist();
	    });
	}
	
	// la minitarjeta llama a este método al clicar en ella
	public void configurarDetalleTarjeta(TarjetaDTO tarjeta) {
		this.tarjeta = tarjeta;
		this.lblTitulo.setText(tarjeta.titulo());
		cargarEtiquetas();
		
		// Detectar si es tarjeta de tareas o de checklist
		if(tarjeta.contenido() instanceof TareaDTO tarea) {
			mostrarModoTarea(tarea);
		} else if(tarjeta.contenido() instanceof ChecklistDTO checklist) {
			mostrarModoChecklist(checklist);
		}
	}
	
	private void mostrarModoTarea(TareaDTO tarea) {
		// Mostrar texto y ocultar VBox de checkboxes
		txtDescripcion.setVisible(true);
		txtDescripcion.setManaged(true);
		
		contenedorChecklist.setVisible(false);
		contenedorChecklist.setManaged(false);
		btnChecklist.setVisible(false);
		
		descripcionActual = tarea.descripcion();
		txtDescripcion.setText(descripcionActual);
	}
	
	private void mostrarModoChecklist(ChecklistDTO checklist) {
	    txtDescripcion.setVisible(false);
	    txtDescripcion.setManaged(false);

	    contenedorChecklist.setVisible(true);
	    contenedorChecklist.setManaged(true);
	    btnChecklist.setVisible(true);
	    btnCompletar.setVisible(false);

	    checklistObservable.setAll(checklist.items());
	}
	
	private void guardarDescripcionTarea() {
		if((tarjeta == null) || !(tarjeta.contenido() instanceof TareaDTO)) {
			return;
		}
		
		String nuevaDesc = txtDescripcion.getText(); // Esto es lo que escribe manualmene en la vista
		
		if(nuevaDesc == null || nuevaDesc.isBlank()) {
			mostrarError("Error", "La descripción de la tarea no puede estar vacía");
			txtDescripcion.setText(descripcionActual); // Se restablece el texto al que habíia previamente
			return;
		} else if (nuevaDesc.equals(descripcionActual)) {
			return; // No hacer nada si no hay modificación
		}
		
		try {
			ContenidoTarjetaCmd cmd = new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, nuevaDesc, List.of(), contextoUsuario.getEmail());
			servicioTarjeta.editarContenidoTarjeta(contextoUsuario.getIdTableroActual(), tarjeta.listaActualId(), tarjeta.id(), cmd);
			descripcionActual = nuevaDesc;
			
			tarjeta = new TarjetaDTO(tarjeta.id(), tarjeta.titulo(), tarjeta.fechaCreacion(), tarjeta.listaActualId(), new TareaDTO(nuevaDesc), tarjeta.etiquetas(), tarjeta.listasVisitadas());
		} 
		catch (Exception e) {
	        txtDescripcion.setText(descripcionActual);
	        mostrarError("Error", "No se pudo guardar la descripción: " + e.getMessage());
	    }

	}
	
	private void redibujarChecklist() {
	    contenedorChecklist.getChildren().clear();

	    for (int i = 0; i < checklistObservable.size(); i++) {
	        ItemChecklistDTO item = checklistObservable.get(i);
	        CheckBox cb = crearCheckbox(item, i);
	        contenedorChecklist.getChildren().add(cb);
	    }
	}
	
	private CheckBox crearCheckbox(ItemChecklistDTO item, int indiceItem) {
	    CheckBox cb = new CheckBox(item.descripcion());
	    cb.setSelected(item.completado());

	    cb.setOnAction(e -> {
	        boolean nuevoEstado = cb.isSelected();

	        try {
	            if (nuevoEstado) {
	                servicioTarjeta.completarItemChecklist(
	                    contextoUsuario.getIdTableroActual(),
	                    tarjeta.listaActualId(),
	                    tarjeta.id(),
	                    indiceItem,
	                    contextoUsuario.getEmail()
	                );
	            } else {
	                servicioTarjeta.marcarItemChecklistComoPendiente(
	                    contextoUsuario.getIdTableroActual(),
	                    tarjeta.listaActualId(),
	                    tarjeta.id(),
	                    indiceItem,
	                    contextoUsuario.getEmail()
	                );
	            }

	            checklistObservable.set(
	                indiceItem,
	                new ItemChecklistDTO(item.descripcion(), nuevoEstado)
	            );

	            if (nuevoEstado && todosLosCheckboxesMarcados()) {
	                btnCompletar.getScene().getWindow().hide();
	            }

	        } catch (Exception ex) {
	            cb.setSelected(!nuevoEstado);
	            mostrarError("Error", ex.getMessage());
	        }
	    });

	    return cb;
	}
	
	private boolean todosLosCheckboxesMarcados() {
	    return checklistObservable.stream()
	            .allMatch(ItemChecklistDTO::completado);
	}
	
	@FXML
	public void accionCompletarTarjeta(ActionEvent evento) {
		Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
		confirmacion.setTitle("ComplettarTarjeta");
		confirmacion.setHeaderText("¿Quieres marcar esta tarjeta como completada?");
		confirmacion.setContentText("La tarjeta se moverá a una lista especiald entro de este tablero.");
		
		if(confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
			try {
				String tableroId = contextoUsuario.getIdTableroActual();
				String email = contextoUsuario.getEmail();
				
				servicioTarjeta.completarTarjeta(tableroId, tarjeta.listaActualId(), tarjeta.id(), email);
				
				Alert exito = new Alert(Alert.AlertType.INFORMATION);
				exito.setContentText("La tarjeta se ha compeltado correctamente");
				exito.showAndWait();
				
				btnCompletar.getScene().getWindow().hide();
			} catch(Exception e) {
				Alert error = new Alert(Alert.AlertType.ERROR);
				error.setTitle("Error de validación");
				error.setHeaderText("No se puede completar la tarjeta");
				error.setContentText(e.getMessage());
				error.showAndWait();
			}
		}
	}
	
	@FXML
	public void accionAnadirEtiqueta(ActionEvent evento) {
		Dialog<Pair<String, String>> dialogo = new Dialog<>();
		dialogo.setTitle("Nueva Etiqueta");
		dialogo.setHeaderText("Añadir etiquetas a la tarjeta '" + tarjeta.titulo() + "'");
		
		ButtonType btnAceptar = new ButtonType("Añadir", ButtonData.OK_DONE);
		dialogo.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));
		
		TextField txtNombre = new TextField();
		txtNombre.setPromptText("Ej: Bug, Importante, Pista, ...");
		
		ColorPicker picker = new ColorPicker();
		picker.setValue(Color.DARKRED);
		
		grid.add(new Label("Nombre:"), 0, 0);
		grid.add(txtNombre, 1, 0);
		grid.add(new Label("Color:"), 0, 1);
		grid.add(picker, 1, 1);
		
		dialogo.getDialogPane().setContent(grid);
		
		dialogo.setResultConverter(dialogButton -> {
			if(dialogButton == btnAceptar) {
				String hexColor = "#" + Integer.toHexString(picker.getValue().hashCode()).substring(0, 6).toUpperCase();
				return new Pair<>(txtNombre.getText(), hexColor);
			}
			return null;
		});
		
		dialogo.showAndWait().ifPresent(resultado -> {
			try {
				String nombre = resultado.getKey();
				String colorHex = resultado.getValue();
				
				servicioTarjeta.addEtiquetaATarjeta(
					contextoUsuario.getIdTableroActual(),
					tarjeta.listaActualId(),
					tarjeta.id(),
					nombre,
					colorHex,
					contextoUsuario.getEmail()
				);
				
				Label lblEtiqueta = new Label(nombre);
				// (He corregido un pequeño typo que tenías en background-radius)
				lblEtiqueta.setStyle(String.format(
						"-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 4; -fx-font-weight: bold",
						colorHex));
				contenedorEtiquetas.getChildren().add(lblEtiqueta);
				
				// 3. F5 AL TABLERO: Le decimos al SceneManager que recargue el tablero de fondo
				// Extraemos el SceneManager del contexto de Spring porque no lo teníamos inyectado en el constructor
				SceneManager manager = contextoApp.getBean(SceneManager.class);
				manager.showTablero();
				
				Alert exito = new Alert(Alert.AlertType.INFORMATION);
				exito.setContentText("Etiqueta añadida");
				exito.showAndWait();
			} catch(Exception e) {
				Alert error = new Alert(Alert.AlertType.ERROR);
				error.setContentText("No se ha podido añadir la etiqueta");
				error.showAndWait();
			}
		});
	}
	
	@FXML
	public void accionAnadirCheckbox(ActionEvent accion) {

	    if (!(tarjeta.contenido() instanceof ChecklistDTO)) {
	        mostrarError("Error", "La tarjeta no es de tipo checklist");
	        return;
	    }

	    TextInputDialog dialogo = new TextInputDialog();
	    dialogo.setTitle("Nuevo ítem");
	    dialogo.setHeaderText("Añadir ítem a la checklist");
	    dialogo.setContentText("Ítem:");

	    dialogo.showAndWait().ifPresent(texto -> {
	        try {
	            if (texto == null || texto.isBlank()) {
	                mostrarError("Error", "El ítem no puede estar vacío");
	                return;
	            }

	            List<String> items = new ArrayList<>();

	            for (ItemChecklistDTO item : checklistObservable) {
	                items.add(item.descripcion());
	            }

	            items.add(texto);

	            ContenidoTarjetaCmd contenido = new ContenidoTarjetaCmd(
	                TipoContenidoTarjeta.CHECKLIST,
	                tarjeta.titulo(),
	                items,
	                contextoUsuario.getEmail()
	            );

	            servicioTarjeta.editarContenidoTarjeta(
	                contextoUsuario.getIdTableroActual(),
	                tarjeta.listaActualId(),
	                tarjeta.id(),
	                contenido
	            );

	            checklistObservable.add(new ItemChecklistDTO(texto, false));

	        } catch (Exception e) {
	            mostrarError("Error", e.getMessage());
	        }
	    });
	}
	
	private void cargarEtiquetas() {
		contenedorEtiquetas.getChildren().clear();
		for(EtiquetaDTO etiq : tarjeta.etiquetas()) {
			Label lblEtiqueta = new Label(etiq.nombre());
			lblEtiqueta.setStyle(String.format(
					"-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-backround.radius: 4; -fx-font-weight: bold",
					etiq.color()));
			contenedorEtiquetas.getChildren().add(lblEtiqueta);
		}
	}
	
	private void mostrarError(String titulo, String mensaje) {
	    Alert alerta = new Alert(Alert.AlertType.ERROR);
	    alerta.setTitle(titulo);
	    alerta.setHeaderText(null);
	    alerta.setContentText(mensaje);
	    alerta.showAndWait();
	}
}
