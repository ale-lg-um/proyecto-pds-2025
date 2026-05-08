package es.um.pds.tarjetas.application.dto;

import java.util.List;

public record ContenidoTarjetaCmd(TipoContenidoTarjeta tipoContenido, String descripcionTarea,
		List<String> itemsChecklist, String emailUsuario) {}
