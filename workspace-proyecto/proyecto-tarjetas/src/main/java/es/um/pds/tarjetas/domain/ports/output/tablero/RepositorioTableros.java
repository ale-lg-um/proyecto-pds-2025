package es.um.pds.tarjetas.domain.ports.output.tablero;

import java.util.Optional;

import es.um.pds.tarjetas.domain.model.tablero.Tablero;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;

public interface RepositorioTableros {
	// Métodos
	void guardar(Tablero tablero);
	Optional<Tablero> buscarPorId(TableroId tableroId);
	void eliminarPorId(TableroId tableroId);
}
