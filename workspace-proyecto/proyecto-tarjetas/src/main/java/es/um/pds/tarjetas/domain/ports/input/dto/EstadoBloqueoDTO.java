package es.um.pds.tarjetas.domain.ports.input.dto;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.domain.model.tablero.model.EstadoBloqueo;

public record EstadoBloqueoDTO(LocalDateTime desde, LocalDateTime hasta, String descripcion) {
	public EstadoBloqueoDTO(EstadoBloqueo estadoBloqueo) {
		this(estadoBloqueo.desde(), estadoBloqueo.hasta(), estadoBloqueo.descripcion());
	}
}