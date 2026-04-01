package es.um.pds.tarjetas.domain.ports.input.dto;

import java.util.List;
import java.util.function.Function;

import es.um.pds.tarjetas.application.common.Page;

public record PageDTO<T>(List<T> contenido, int pagina, int tamano, int totalPaginas, long totalElementos) {
	// Convierte Page<EntryHistorial>  --->  PageDTO<EntryHistorialDTO>
	public static <S, T> PageDTO<T> fromPage(Page<S> page, Function<S, T> mapper) {
		return new PageDTO<>(page.getContenido().stream().map(mapper).toList(), page.getPagina(), page.getTamano(),
				page.getTotalPaginas(), page.getTotalElementos());
	}
}