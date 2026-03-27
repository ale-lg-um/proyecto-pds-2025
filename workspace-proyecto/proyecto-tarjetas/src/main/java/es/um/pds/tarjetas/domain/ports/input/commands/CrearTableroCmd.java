package es.um.pds.tarjetas.domain.ports.input.commands;

import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public record CrearTableroCmd(String nombreTablero, UsuarioId usuarioCreador, PlantillaId plantillaId, String nombrePlantilla, Plantilla plantillaCreacion) {

}
