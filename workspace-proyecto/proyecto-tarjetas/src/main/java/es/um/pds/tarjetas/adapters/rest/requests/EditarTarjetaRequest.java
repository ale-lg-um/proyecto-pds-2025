package es.um.pds.tarjetas.adapters.rest.requests;

import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;

public record EditarTarjetaRequest(ContenidoTarjetaCmd contenido) {

}
