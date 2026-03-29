package es.um.pds.tarjetas.domain.ports.input;

import es.um.pds.tarjetas.domain.ports.input.dto.EntryHistorialDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;

public interface ServicioHistorial {
	// Servicio de lectura de las entries por tablero
	PageDTO<EntryHistorialDTO> consultarPorTablero(String tableroId, int pagina, int tamano);
}
