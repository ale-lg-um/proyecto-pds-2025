package es.um.pds.tarjetas.ui.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;
import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TareaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

@Controller
@Scope("prototype")
public class ListaController {
	// Atributos
	private final ServicioGestionTablero servicioTablero;
	private final ApplicationContext contextoApp;
	private ListaDTO listaDominio;		// Entidad real
	private String tableroId;
	
	@FXML private Label lblNombreLista;
	@FXML private Label lblLimite;
	@FXML private VBox contenedorTarjetas;
	
	// Aquí se inyecta el servicio
	public ListaController(ServicioGestionTablero servicioTablero, ApplicationContext contextoApp) {
		this.servicioTablero = servicioTablero;
		this.contextoApp = contextoApp;
	}
	
	// El tablero llama a este método depsués de crear la lista
	public void configurarLista(ListaDTO lista, String tableroId) {
		this.listaDominio = lista;
		this.lblNombreLista.setText(lista.nombre());
		this.tableroId = tableroId;
		
		// Revisar si la lista tiene límite
		if(lista.limite() != null) {
			this.lblLimite.setText("(0/" + lista.limite() + ")");
		} else {
			this.lblLimite.setText("(\u221E)");
		}
	}
	
	@FXML
	public void accionAnadirTarjeta(ActionEvent evento) {
		System.out.println("Botón 'Añadir Tarjeta' pulsado en la lista: " + listaDominio.nombre());
		TextInputDialog dialogo = new TextInputDialog();
		dialogo.setTitle("Nueva tarea");
		dialogo.setHeaderText("Añadir tarjeta a: " + listaDominio.nombre());
		dialogo.setContentText("Título:");
		
		dialogo.showAndWait().ifPresent(titulo -> {
			try {
				System.out.println("Creando tarjeta: " + titulo);
				
				//TarjetaDTO tarjetaDTO = new TarjetaDTO(null, titulo, null, listaDominio.getIdentificador().getId(), 0, new TareaDTO(""), List.of(), Set.of());
				
				// CAMBIAR: tenemos que detectar al usuario que está con la sesión iniciada
				ContenidoTarjetaCmd contenido = new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, titulo, List.of(), "usuario@ejemplo.com");
				
				//TarjetaDTO nuevaTarjeta = servicioTablero.crearTarjeta(tableroId, listaDominio.getIdentificador().getId(), tarjetaDTO, "usuario@ejemplo.com");
				TarjetaDTO nuevaTarjeta = servicioTablero.crearTarjeta(tableroId, listaDominio.id(), titulo, contenido);
				
				// cargar el FXML del post-it con Spring
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MiniTarjetaView.fxml"));
				loader.setControllerFactory(contextoApp::getBean);
				
				VBox nodoTarjeta = loader.load();
				
				// pasar datos a post-it
				MiniTarjetaController controlador = loader.getController();
				controlador.configurarMiniTarjeta(nuevaTarjeta);
				
				// Inyectar en la lista
				contenedorTarjetas.getChildren().add(nodoTarjeta);
				
				System.out.println("TARJETA AÑADIDA");
			} catch(Exception e) {
				e.printStackTrace();
				mostrarError("Error", "No se pudo crear la tarjeta: " +  e.getMessage());
			}
		});
	}
	
	private void mostrarError(String titulo, String mensaje) {
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}
}
