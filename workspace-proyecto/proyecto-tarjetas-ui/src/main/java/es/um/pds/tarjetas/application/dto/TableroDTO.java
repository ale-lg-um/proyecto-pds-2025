package es.um.pds.tarjetas.application.dto;

import java.util.Set;

public record TableroDTO(String id, String nombre, String tokenUrl, boolean bloqueado, EstadoBloqueoDTO estadoBloqueo,
		Set<String> listaIds) {
}
