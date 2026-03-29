package es.um.pds.tarjetas.domain.ports.input.dto;

import java.util.List;
import java.util.Set;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;

public record ListaDTO(String id, String nombre, boolean especial, Integer limite, List<String> tarjetaIds,
		Set<String> prerrequisitoIds) {
	public ListaDTO(Lista lista) {
		this(lista.getIdentificador().getId(), lista.getNombreLista(), lista.isEspecial(), lista.getLimite(),
				lista.getListaTarjetas().stream().map(TarjetaId::getId).toList(),
				lista.getPrerrequisitos().stream().map(ListaId::getId)
						.collect(java.util.stream.Collectors.toUnmodifiableSet()));
	}
}