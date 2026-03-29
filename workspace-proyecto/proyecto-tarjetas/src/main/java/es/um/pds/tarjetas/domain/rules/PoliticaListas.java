package es.um.pds.tarjetas.domain.rules;

import java.util.Set;

import org.springframework.stereotype.Component;

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;

@Component
public class PoliticaListas {
	// Validar que todas las listas que se quieran configurar como prerrequisitos existan en el tablero (R10)
	public void validarPrerrequisitosConfigurados(Tablero tablero, Set<ListaId> prerrequisitos)
			throws ListaInvalidaException {
		
		if (tablero == null) {
			throw new IllegalArgumentException("El tablero no puede ser nulo");
		}

		if (prerrequisitos == null) {
			throw new IllegalArgumentException("Los prerrequisitos no pueden ser nulos");
		}
		
		Set<ListaId> listasTablero = tablero.getListas();
		for (ListaId prerrequisitoId : prerrequisitos) {
			if (prerrequisitoId == null) {
				throw new IllegalArgumentException("No puede haber listas nulas en los prerrequisitos");
			}
			
			if (!listasTablero.contains(prerrequisitoId)) {
				throw new ListaInvalidaException
					("La lista prerrequisito con ID " + prerrequisitoId + " no existe en el tablero");
			}
		}
	}
}
