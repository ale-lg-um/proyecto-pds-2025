package es.um.pds.tarjetas.domain.ports.input.dto;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.domain.model.entryHistorial.model.EntryHistorial;

public record EntryHistorialDTO(String entryId, String tableroId, String tipo, String usuario, LocalDateTime timestamp,
		String detalles) {
	public EntryHistorialDTO(EntryHistorial entry) {
		this(entry.getIdentificador().getId(), entry.getTableroId().getId(), entry.getTipo().name(),
				entry.getUsuario().getCorreo(), entry.getTimestamp(), entry.getDetalles());
	}
}