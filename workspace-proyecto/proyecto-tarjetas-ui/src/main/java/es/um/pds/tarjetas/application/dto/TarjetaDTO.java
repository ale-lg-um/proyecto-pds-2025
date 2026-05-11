package es.um.pds.tarjetas.application.dto;

import java.util.List;
import java.util.Set;

public record TarjetaDTO(String id, String titulo, String fechaCreacion, String listaActualId,
						 ContenidoTarjetaDTO contenido, List<EtiquetaDTO> etiquetas, Set<String> listasVisitadas) {

}
