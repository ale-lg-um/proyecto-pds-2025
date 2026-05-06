package es.um.pds.tarjetas.ui.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.dto.ChecklistDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.EtiquetaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ItemChecklistDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TareaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
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
	private TarjetaDTO tarjeta;
	
	@FXML private Label lblTitulo;
	@FXML private FlowPane contenedorEtiquetas;
	@FXML private TextArea txtDescripcion;
	@FXML private VBox contenedorChecklist;
	@FXML private Button btnCompletar;
	
	public TarjetaController(ApplicationContext contextoApp, ContextoUsuario contextoUsuario, ServicioTarjeta servicioTarjeta) {
		this.contextoApp = contextoApp;
		this.contextoUsuario = contextoUsuario;
		this.servicioTarjeta = servicioTarjeta;
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
		
		txtDescripcion.setText(tarea.descripcion());
	}
	
	private void mostrarModoChecklist(ChecklistDTO checklist) {
		// Ocultar texto y mostrar VBox de checkboxes
		txtDescripcion.setVisible(false);
		txtDescripcion.setManaged(false);
		
		contenedorChecklist.setVisible(true);
		contenedorChecklist.setManaged(true);
		
		contenedorChecklist.getChildren().clear();
		
		// Crear un checkbox por cada ítem
		for(ItemChecklistDTO item : checklist.items()) {
			CheckBox cb = new CheckBox(item.descripcion());
			cb.setSelected(item.completado());
			
			// Al clicar, se actualiza el dominio
			/*cb.setOnAction(e -> {
				if(cb.isSelected()) {
					item.marcarComoCompletado();
				}
				else item.marcarComoPendiente();
			});*/
			contenedorChecklist.getChildren().add(cb);
		}
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
}
