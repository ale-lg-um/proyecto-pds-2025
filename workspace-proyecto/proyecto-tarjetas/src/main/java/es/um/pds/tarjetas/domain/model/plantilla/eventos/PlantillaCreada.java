package es.um.pds.tarjetas.domain.model.plantilla.eventos;

import java.time.LocalDateTime;

import es.um.pds.tarjetas.common.events.EventoDominio;
import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public record PlantillaCreada(PlantillaId plantillaId, UsuarioId usuarioId, LocalDateTime timestamp,
		String nombrePlantilla, String contenidoYaml) implements EventoDominio {

}
