package es.um.pds.tarjetas.domain.model.tablero.model;

import java.time.LocalDateTime;

// Value object
public record EstadoBloqueo(LocalDateTime desde, LocalDateTime hasta, String descripcion) {
	// Constructor
	public EstadoBloqueo {
		if (desde == null) {
			desde = LocalDateTime.now();
		}
		
		// Si no se especifica hasta (hasta == null) es un bloqueo indefinido
		
		// No hacer esta comprobación para cuando carguemos un bloqueo no dé problemas, con la persistencia
		/*
		if (desde.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("La fecha de comienzo del bloqueo no puede ser anterior a la fecha actual");
		}
		*/
		
		if (hasta!= null && hasta.isBefore(desde)) {
			throw new IllegalArgumentException("La fecha de fin del bloqueo no puede ser anterior a la fecha de comienzo");
		}
	}
	
	// Getters
	public LocalDateTime getDesde() {
		return this.desde;
	}
	
	public LocalDateTime getHasta() {
		return this.hasta;
	}
	
	public String getDescripcion() {
		return this.descripcion;
	}
	
	// Funcionalidades
	public boolean estaActivoEn(LocalDateTime fecha) {
		if (fecha == null) {
			throw new IllegalArgumentException("La fecha no puede ser nula");
		}

		boolean empiezaYa = !fecha.isBefore(desde);
		boolean noHaTerminado = (hasta == null) || !fecha.isAfter(hasta);

		return empiezaYa && noHaTerminado;
	}
	
	public boolean estaActivoAhora() {
		return estaActivoEn(LocalDateTime.now());
	}
}
