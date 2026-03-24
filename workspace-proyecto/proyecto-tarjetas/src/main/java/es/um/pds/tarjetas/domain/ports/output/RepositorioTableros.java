package es.um.pds.tarjetas.domain.ports.output;

import java.util.List;
import java.util.Optional;

import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public interface RepositorioTableros {

	void guardar(Tablero tablero);

    Optional<Tablero> buscarPorId(TableroId tableroId);

    void eliminarPorId(TableroId tableroId);

    List<TableroId> listarIdsPorUsuario(UsuarioId usuarioId);
}