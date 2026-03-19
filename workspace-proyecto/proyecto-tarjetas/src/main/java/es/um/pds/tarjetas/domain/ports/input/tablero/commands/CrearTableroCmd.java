package es.um.pds.tarjetas.domain.ports.input.tablero.commands;

import es.um.pds.tarjetas.domain.model.plantilla.Plantilla;

public record CrearTableroCmd(String nombreTablero, String emailCreador, Plantilla plantillaCreacion) {

}
