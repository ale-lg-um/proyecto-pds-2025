package es.um.pds.tarjetas.ui.controllers;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Controller;

import es.um.pds.tarjetas.ui.infrastructure.api.LoginApiClient;
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
	private final LoginApiClient loginApi;
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
	public LoginController(LoginApiClient loginApi, SceneManager sceneManager, ContextoUsuario contexto) {
		this.loginApi = loginApi;
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
			loginApi.loginUsuario(email);
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
			Optional<String> tokenOptional = loginApi.verificarCodigo(emailTemp, codigo);
			
			if(tokenOptional.isEmpty()) {
				throw new NoSuchElementException("No se ha podido extraer el token.");
			}
			
			String tokenSesion = tokenOptional.get();
			
			System.out.println("Inicio de sesión exitoso. Token generado: " + tokenSesion);
			
			contexto.setEmail(emailTemp);
			contexto.setTokensSesion(tokenSesion);
			
			sceneManager.showDashboard(); // Cambiar a vista de dashboard
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
