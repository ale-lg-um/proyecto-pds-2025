package es.um.pds.tarjetas.adapters.rest.requests;

import java.util.List;

import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;

public record FiltradoRequest(List<String> nombresEtiquetas, ModoFiltradoEtiquetas modo, int pagina, int tamano) {

}
