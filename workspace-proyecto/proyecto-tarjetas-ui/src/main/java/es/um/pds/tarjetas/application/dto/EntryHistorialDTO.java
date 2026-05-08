package es.um.pds.tarjetas.application.dto;

import java.time.LocalDateTime;

public record EntryHistorialDTO(String entryId, String tableroId, String tipo, String usuario, LocalDateTime timestamp, String detalles) {

}
