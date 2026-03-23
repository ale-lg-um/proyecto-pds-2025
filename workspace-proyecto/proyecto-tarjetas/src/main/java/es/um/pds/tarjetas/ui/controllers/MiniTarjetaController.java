package es.um.pds.tarjetas.ui.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.model.tarjeta.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.Tarea;
import es.um.pds.tarjetas.domain.model.tarjeta.Tarjeta;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Controller
@Scope("prototype")
public class MiniTarjetaController {
	// Atributos
	private final ApplicationContext contextoApp;
	private Tarjeta tarjetaDominio;
	
	@FXML private Label lblTitulo;
	@FXML private Label lblIconoTipo;
	@FXML private FlowPane contenedorEtiquetas;
	
	public MiniTarjetaController(ApplicationContext contextoApp) {
		this.contextoApp = contextoApp;
	}
	
	// Inyectar datos reales
	public void configurarMinitarjeta(Tarjeta tarjeta) {
		this.tarjetaDominio = tarjeta;
		this.lblTitulo.setText(tarjeta.getTitulo());
		
		if(tarjeta.getContenido() instanceof Tarea) {
			this.lblIconoTipo.setText("📝: Tarea");
		} else if(tarjeta.getContenido() instanceof Checklist) {
			this.lblIconoTipo.setText("☑: Checklist");
		}
		
		// cargarEtiquetasVisuales()
	}
	
	// Al hacer clic en la tarjeta
	@FXML
	public void accionAbrirDetalle(MouseEvent evento) {
		System.out.println("Abriendo detalle de la tarjeta: " + tarjetaDominio.getIdentificador());
		try {
			// Cargar vista de Tarjeta
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TarjetaView.fxml"));
			
			// pedir el controlador a Spring
			loader.setControllerFactory(contextoApp::getBean);
			Parent raizDetalle = loader.load();
			
			// recuperar el controlador de la vista de tarjeta
			TarjetaController controlador = loader.getController();
			controlador.configurarDetalleTarjeta(tarjetaDominio);
			
			// Crear ventana
			Stage ventana = new Stage();
			ventana.setTitle("Detalle de la tarjeta: " + tarjetaDominio.getTitulo());
			
			// Bloquear la ventana del tablero hasta que se cierre la de la tarjeta
			ventana.initModality(Modality.APPLICATION_MODAL);
			
			// Por si queremos que no se peuda redimensionar la ventana
			// ventana.setResizeable(false);
			
			// Aplicar escena y mostrar
			Scene escena = new Scene(raizDetalle);
			ventana.setScene(escena);
			ventana.showAndWait();
		} catch(Exception e) {
			System.err.println("Error al abrir la vista de tarjeta: " + e.getMessage());
		}
	}
}
