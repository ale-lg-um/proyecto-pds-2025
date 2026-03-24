package es.um.pds.tarjetas.ui.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ch.qos.logback.core.util.Loader;
import es.um.pds.tarjetas.domain.model.lista.Lista;
import es.um.pds.tarjetas.domain.ports.input.tablero.ServicioGestionTablero;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

@Controller
@Scope("prototype")	// Le indica a Spring que se tiene que crear una lista nueva para cada columna del tablero
public class ListaController {
	// Atributos
	private final ServicioGestionTablero servicioTablero;
	private final ApplicationContext contextoApp;
	private Lista listaDominio;		// Entidad real
	
	@FXML private Label lblNombreLista;
	@FXML private Label lblLimite;
	@FXML private VBox contenedorTarjetas;
	
	// Aquí se inyecta el servicio
	public ListaController(ServicioGestionTablero servicioTablero, ApplicationContext contextoApp) {
		this.servicioTablero = servicioTablero;
		this.contextoApp = contextoApp;
	}
	
	// El tablero llama a este método depsués de crear la lista
	public void configurarLista(Lista lista) {
		this.listaDominio = lista;
		this.lblNombreLista.setText(lista.getNombreLista());
		
		// Revisar si la lista tiene límite
		if(lista.getLimite() != null) {
			this.lblLimite.setText("(0/" + lista.getLimite() + ")");
		} else {
			this.lblLimite.setText("(\u221E)");
		}
	}
	
	@FXML
	public void accionAnadirTarjeta(ActionEvent evento) {
		System.out.println("Botón 'Añadir Tarjeta' pulsado en la lista: " + listaDominio.getNombreLista());
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nueva tarea");
		dialogo.setHeaderText("Añadir tarjeta a: " + listaDominio.getNombreLista());
		dialogo.setContentText("Título:");
		
		dialogo.showAndWait().ifPresent(titulo -> {
			try {
				// Llamar al dominio... por implementar...
				
				System.out.println("Creando tarjeta: " + titulo);
				
				// cargar el FXML del post-it con Spring
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MiniTarjetaView.fxml"));
				loader.setControllerFactory(contextoApp::getBean);
				
				VBox nodoTarjeta = loader.load();
				
				// pasar datos a post-it
				MiniTarjetaController controlador = loader.getController();
				//controlador.configurarMiniTarjeta(nuevaTarjeta);
				
				// Inyectar en la lista
				contenedorTarjetas.getChildren().add(nodoTarjeta);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
