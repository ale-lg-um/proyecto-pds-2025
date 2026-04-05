package es.um.pds.tarjetas.ui.controllers;

import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.domain.ports.input.ServicioAutenticacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

@Controller
public class LoginController {
	// Atributos
	private final ServicioAutenticacion servicioAutenticacion;
	private final SceneManager sceneManager;
	private final ContextoUsuario contexto;
	private String emailTemp; // Guardar el email
	
	// Cosas FXML para enviar correo
	@FXML private VBox panelEmail;
	@FXML private TextField txtEmail;
	@FXML private Button btnEnviar;
	
	// Cosas FXML para enviar código
	@FXML private VBox panelCodigo;
	@FXML private TextField txtCodigo;
	@FXML private Button btnVerificar;
	
	// Constructor
	public LoginController(ServicioAutenticacion servicioAutenticacion, SceneManager sceneManager, ContextoUsuario contexto) {
		this.servicioAutenticacion = servicioAutenticacion;
		this.sceneManager = sceneManager;
		this.contexto = contexto;
	}
	
	@FXML
	public void accionEnviarCorreo(ActionEvent evento) {
		String email = txtEmail.getText().trim();
		
		if(email.isEmpty()) {
			mostrarError("Email vacío", "Por favor, introduce un correo electtrónico válido.");
			return;
		}
		
		try {
			servicioAutenticacion.enviarCodigoLogin(email);
			emailTemp = email;
			panelEmail.setVisible(false);
			panelEmail.setManaged(false);
			
			panelCodigo.setVisible(true);
			panelCodigo.setManaged(true);;
		} catch(Exception e) {
			mostrarError("Error", e.getMessage());
		}
	}
	
	@FXML
	public void accionVerificarCodigo(ActionEvent evento) {
		String codigo = txtCodigo.getText().trim();
		
		if(codigo.isEmpty()) {
			mostrarError("Código vacío", "Por favor, introduzca el código de 6 dígitos que le hemos enviado.");
			return;
		}
		
		try {
			String tokenSesion = servicioAutenticacion.verificarCodigoLogin(emailTemp, codigo);
			System.out.println("Inicio de sesión exitoso. Token generado: " + tokenSesion);
			
			contexto.setEmail(emailTemp);
			contexto.setTokensSesion(tokenSesion);
			
			sceneManager.showTablero(); // Cambiar a vista de tablero
		} catch(Exception e) {
			mostrarError("Error", e.getMessage());
		}
	}
	
	private void mostrarError(String titulo, String mensaje) {
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);;
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}
}
