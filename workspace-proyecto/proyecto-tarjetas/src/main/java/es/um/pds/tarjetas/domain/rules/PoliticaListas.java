package es.um.pds.tarjetas.domain.rules;

import java.util.Set;

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;

public class PoliticaListas {

	// Inyección de dependecias necesarias
	private final RepositorioListas repoListas;

	public PoliticaListas(RepositorioListas repoListas) {
		this.repoListas = repoListas;
	}

	// Validar que todas las listas que se quieran configurar como prerrequisito existan en el tablero (R10)
	public void validarPrerrequisitosConfigurados(Set<ListaId> listasTablero, Set<ListaId> prerrequisitos) {

		// listasTablero puede ser null, si aún no existen listas en el tablero

		if (prerrequisitos == null) {
			throw new IllegalArgumentException("Los prerrequisitos no pueden ser nulos");
		}

		for (ListaId prerrequisitoId : prerrequisitos) {
			if (prerrequisitoId == null) {
				throw new IllegalArgumentException("No puede haber listas nulas en los prerrequisitos");
			}

			if (!listasTablero.contains(prerrequisitoId)) {
				throw new ListaInvalidaException(
						"La lista prerrequisito con ID " + prerrequisitoId + " no existe en el tablero");
			}
		}
	}

	// Validar que todas las listas de un tablero tienen nombre único (R11)
	public void validarNombreUnicoEnTablero(TableroId tablero, String nombreLista) {

		if (tablero == null) {
			throw new IllegalArgumentException("El tablero no puede ser nulo");
		}

		if (nombreLista == null) {
			throw new IllegalArgumentException("El nombre de la lista no puede ser nulo");
		}

		Set<Lista> listasTablero = repoListas.buscarPorTableroId(tablero);

		boolean yaExiste = listasTablero.stream().anyMatch(l -> l.getNombreLista().equals(nombreLista));

		if (yaExiste) {
			throw new ListaInvalidaException("Ya existe una lista con el nombre " + nombreLista);
		}

	}
}
