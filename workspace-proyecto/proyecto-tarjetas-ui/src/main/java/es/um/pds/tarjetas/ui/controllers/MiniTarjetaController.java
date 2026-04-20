package es.um.pds.tarjetas.ui.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.dto.ChecklistDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.EtiquetaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TareaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
	private final ServicioTarjeta servicioTarjeta;
	private final ContextoUsuario contextoUsuario;
	private TarjetaDTO tarjetaDominio;
	private Runnable funcionEliminarDeLaVista;
	
	@FXML private Label lblTitulo;
	@FXML private Label lblIconoTipo;
	@FXML private FlowPane contenedorEtiquetas;
	
	public MiniTarjetaController(ApplicationContext contextoApp, ServicioTarjeta servicioTarjeta, ContextoUsuario contextoUsuario) {
		this.contextoApp = contextoApp;
		this.servicioTarjeta = servicioTarjeta;
		this.contextoUsuario = contextoUsuario;
	}
	
	public void setFuncionEliminarDeLaVista(Runnable funcion) {
		this.funcionEliminarDeLaVista = funcion;
	}
	
	// Inyectar datos reales
	public void configurarMiniTarjeta(TarjetaDTO tarjeta) {
		this.tarjetaDominio = tarjeta;
		this.lblTitulo.setText(tarjeta.titulo());
		cargarEtiquetas();
		
		if(tarjeta.contenido() instanceof TareaDTO) {
			this.lblIconoTipo.setText("📝: Tarea");
		} else if(tarjeta.contenido() instanceof ChecklistDTO) {
			this.lblIconoTipo.setText("☑: Checklist");
		}
		
		// cargarEtiquetasVisuales()
	}
	
	// Al hacer clic en la tarjeta
	@FXML
	public void accionAbrirDetalle(MouseEvent evento) {
		System.out.println("Abriendo detalle de la tarjeta: " + tarjetaDominio.titulo());
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
			ventana.setTitle("Detalle de la tarjeta: " + tarjetaDominio.titulo());
			
			// Bloquear la ventana del tablero hasta que se cierre la de la tarjeta
			ventana.initModality(Modality.APPLICATION_MODAL);
			ventana.setWidth(700);
			ventana.setHeight(500);
			
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
	
	@FXML
	public void accionHoverEntrar(MouseEvent evento) {
		Node nodoTarjeta = (Node) evento.getSource();
		nodoTarjeta.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5; -fx-border-color: #b0b0b0; -fx-border-radius: 5; -fx-cursor: hand;");
	}
	
	@FXML
	public void accionEliminarTarjeta(ActionEvent evento) {
		evento.consume(); //Para que no se abra la tarjeta
		
		Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
		alerta.setTitle("Eliminar Tarjeta");
		alerta.setHeaderText(null);
		alerta.setContentText("¿Estás seguro de que quieres eliminar esta tarjeta?");
		
		if(alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
			try {
				// Borrar de la base de datos
				servicioTarjeta.eliminarTarjeta(contextoUsuario.getIdTableroActual(), tarjetaDominio.listaActualId(), tarjetaDominio.id(), contextoUsuario.getEmail());
				
				if(funcionEliminarDeLaVista != null) {
					funcionEliminarDeLaVista.run();
				}
				
				System.out.println("Tarjeta eliminada");
			} catch (Exception e) {
				e.printStackTrace();
				Alert error = new Alert(Alert.AlertType.ERROR);
				error.setContentText("No se pudo eliminar la tarjeta: " + e.getMessage());
				error.showAndWait();
			}
		}
	}
	
	@FXML
	public void accionHoverSalir(MouseEvent evento) {
		Node nodoTarjeta = (Node) evento.getSource();
		nodoTarjeta.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-cursor: hand;");
	}
	
	private void cargarEtiquetas() {
		contenedorEtiquetas.getChildren().clear();
		for(EtiquetaDTO etiq : tarjetaDominio.etiquetas()) {
			Label lblEtiqueta = new Label(etiq.nombre());
			lblEtiqueta.setStyle(String.format(
					"-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-backround.radius: 4; -fx-font-weight: bold",
					etiq.color()));
			contenedorEtiquetas.getChildren().add(lblEtiqueta);
		}
	}
}
