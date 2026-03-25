package es.um.pds.tarjetas.ui.controllers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Etiqueta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

@Controller
@Scope("prototype")
public class TarjetaController {
	// Atributos
	private Tarjeta tarjeta;
	
	@FXML private Label lblTitulo;
	@FXML private FlowPane contenedorEtiquetas;
	@FXML private TextArea txtDescripcion;
	@FXML private VBox contenedorChecklist;
	
	// la minitarjeta llama a este método al clicar en ella
	public void configurarDetalleTarjeta(Tarjeta tarjeta) {
		this.tarjeta = tarjeta;
		this.lblTitulo.setText(tarjeta.getTitulo());
		cargarEtiquetas();
		
		// Detectar si es tarjeta de tareas o de checklist
		if(tarjeta.getContenido() instanceof Tarea tarea) {
			mostrarModoTarea(tarea);
		} else if(tarjeta.getContenido() instanceof Checklist checklist) {
			mostrarModoChecklist(checklist);
		}
	}
	
	private void mostrarModoTarea(Tarea tarea) {
		// Mostrar texto y ocultar VBox de checkboxes
		txtDescripcion.setVisible(true);
		txtDescripcion.setManaged(true);
		
		contenedorChecklist.setVisible(false);
		contenedorChecklist.setManaged(false);
		
		txtDescripcion.setText(tarea.getDescripcion());
	}
	
	private void mostrarModoChecklist(Checklist checklist) {
		// Ocultar texto y mostrar VBox de checkboxes
		txtDescripcion.setVisible(false);
		txtDescripcion.setManaged(false);
		
		contenedorChecklist.setVisible(true);
		contenedorChecklist.setManaged(true);
		
		contenedorChecklist.getChildren().clear();
		
		// Crear un checkbox por cada ítem
		for(ItemChecklist item : checklist.getItems()) {
			CheckBox cb = new CheckBox(item.getDescripcion());
			cb.setSelected(item.isCompletado());
			
			// Al clicar, se actualiza el dominio
			cb.setOnAction(e -> {
				if(cb.isSelected()) {
					item.marcarComoCompletado();
				}
				else item.marcarComoPendiente();
			});
			contenedorChecklist.getChildren().add(cb);
		}
	}
	
	private void cargarEtiquetas() {
		contenedorEtiquetas.getChildren().clear();
		for(Etiqueta etiq : tarjeta.getEtiquetas()) {
			Label lblEtiqueta = new Label(etiq.nombre());
			lblEtiqueta.setStyle(String.format(
					"-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-backround.radius: 4; -fx-font-weight: bold",
					etiq.color()));
			contenedorEtiquetas.getChildren().add(lblEtiqueta);
		}
	}
}
