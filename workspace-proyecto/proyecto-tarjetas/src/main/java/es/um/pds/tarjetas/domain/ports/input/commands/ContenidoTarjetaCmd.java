package es.um.pds.tarjetas.domain.ports.input.commands;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;

public record ContenidoTarjetaCmd(TipoContenidoTarjeta tipoContenido, String descripcionTarea,
		List<String> itemsChecklist, String emailUsuario) {
	
}
