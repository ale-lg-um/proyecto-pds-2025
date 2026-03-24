package es.um.pds.tarjetas.domain.ports.input.commands;

import es.um.pds.tarjetas.domain.model.plantilla.model.Plantilla;

public record CrearTableroCmd(String nombreTablero, String emailCreador, Plantilla plantillaCreacion) {

}
