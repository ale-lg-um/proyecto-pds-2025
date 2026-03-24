package es.um.pds.tarjetas.domain.ports.output;

import java.util.List;
import java.util.Optional;

import es.um.pds.tarjetas.domain.model.tablero.Tablero;
import es.um.pds.tarjetas.domain.model.tablero.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

public interface RepositorioTableros {

	void guardar(Tablero tablero);

    Optional<Tablero> buscarPorId(TableroId tableroId);

    void eliminarPorId(TableroId tableroId);

    List<TableroId> listarIdsPorUsuario(UsuarioId usuarioId);
}