package es.um.pds.tarjetas.domain.ports.input.dto;

import java.util.Set;

import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;

public record TableroDTO(String id, String nombre, String tokenUrl, boolean bloqueado, EstadoBloqueoDTO estadoBloqueo,
		Set<String> listaIds) {
	public TableroDTO(Tablero tablero) {
		this(tablero.getIdentificador().getId(), tablero.getNombre(), tablero.getTokenUrl(), tablero.isBloqueado(),
				tablero.getEstadoBloqueo() == null ? null : new EstadoBloqueoDTO(tablero.getEstadoBloqueo()),
				tablero.getListas().stream().map(listaId -> listaId.getId())
						.collect(java.util.stream.Collectors.toUnmodifiableSet()));
	}
}