package es.um.pds.tarjetas.domain.ports.input.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;

public record TarjetaDTO(String id, String titulo, LocalDate fechaCreacion, String listaActualId, int posicionEnLista,
		ContenidoTarjetaDTO contenido, List<EtiquetaDTO> etiquetas, Set<String> listasVisitadas) {
	public TarjetaDTO(Tarjeta tarjeta) {
		this(tarjeta.getIdentificador().getId(), tarjeta.getTitulo(), tarjeta.getFechaCreacion(),
				tarjeta.getListaActual().getId(), tarjeta.getPosicionEnLista(),
				ContenidoTarjetaDTO.fromDomain(tarjeta.getContenido()),
				tarjeta.getEtiquetas().stream().map(EtiquetaDTO::new).toList(), tarjeta.getListasVisitadas().stream()
						.map(ListaId::getId).collect(java.util.stream.Collectors.toUnmodifiableSet()));
	}
}