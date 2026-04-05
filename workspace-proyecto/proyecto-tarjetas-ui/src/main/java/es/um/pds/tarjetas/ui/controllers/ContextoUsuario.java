package es.um.pds.tarjetas.ui.controllers;

import org.springframework.stereotype.Component;

// Para leer el correo desde LoginView
@Component
public class ContextoUsuario {
	// Atributos
	private String email;
	private String tokenSesion;
	
	// Getters y setters
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTokenSesion() {
		return this.tokenSesion;
	}
	
	public void setTokensSesion(String tokenSesion) {
		this.tokenSesion = tokenSesion;
	}
}
