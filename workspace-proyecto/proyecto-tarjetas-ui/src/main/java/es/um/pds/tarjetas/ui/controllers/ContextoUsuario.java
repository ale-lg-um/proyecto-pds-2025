package es.um.pds.tarjetas.ui.controllers;

import org.springframework.stereotype.Component;

// Para leer el correo desde LoginView
@Component
public class ContextoUsuario {
	// Atributos
	private String email;
	private String tokenSesion;
	private String idTableroActual;
	private String nombreTableroActual;
	
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
	
	public String getIdTableroActual() {
		return this.idTableroActual;
	}
	
	public void setIdTableroActual(String idTableroActual) {
		this.idTableroActual = idTableroActual;
	}
	
	public String getNombreTableroActual() {
		return this.nombreTableroActual;
	}
	
	public void setNombreTableroActual(String nombreTableroActual) {
		this.nombreTableroActual = nombreTableroActual;
	}
}
