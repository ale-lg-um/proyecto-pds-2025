package es.um.pds.tarjetas.domain.rules;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import es.um.pds.tarjetas.application.common.exceptions.LimiteListaSuperadoException;
import es.um.pds.tarjetas.application.common.exceptions.ListaEspecialInvalidaException;
import es.um.pds.tarjetas.application.common.exceptions.PrerrequisitosNoCumplidosException;
import es.um.pds.tarjetas.application.common.exceptions.TableroBloqueadoException;
import es.um.pds.tarjetas.application.common.exceptions.TarjetaYaCompletadaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.model.EstadoBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;

@Component
public class PoliticaTarjetas {
	
	// Para acceder a los tableros, listas y tarjetas llamaremos en la implementación de los servicios de aplicación
	// a los métodos buscar de los repositorios. Por eso aquí trabajamos con las entidades y no con los IDs.
	
	// Comprueba si un tablero está bloqueado
	private void validarBloqueo(Tablero tablero) throws TableroBloqueadoException {
		EstadoBloqueo bloqueo = tablero.getEstadoBloqueo();

	    if (bloqueo != null && bloqueo.estaActivoAhora()) {
	        throw new TableroBloqueadoException("El tablero está bloqueado temporalmente y no permite crear nuevas tarjetas");
	    }
	}
	
	// Comprueba que el límite de la lista no se ha alcanzado
	private void validarLimite(Lista listaDestino) throws LimiteListaSuperadoException {
		Integer limite = listaDestino.getLimite();

		if (limite != null && listaDestino.getListaTarjetas().size() >= limite) {
			throw new LimiteListaSuperadoException("La lista ha alcanzado su límite máximo de tarjetas");
		}
	}

	// Consiste en que hay ciertas listas que establecen que para que una tarjeta esté en esa lista haya
	// tenido que pasar previamente por otra(s) lista(s)
	private void validarCrearEnListaConPrerrequisitos(Lista listaDestino)
	        throws PrerrequisitosNoCumplidosException {

			if (!listaDestino.getPrerrequisitos().isEmpty()) {
				throw new PrerrequisitosNoCumplidosException("No se puede crear una tarjeta en una lista que tiene prerrequisitos");
		}
	}
	
	private void validarPrerrequisitos(Tarjeta tarjeta, Lista lista) throws PrerrequisitosNoCumplidosException {
		for (ListaId prerrequisito : lista.getPrerrequisitos()) {
			if (!tarjeta.getListasVisitadas().contains(prerrequisito)) {
				throw new PrerrequisitosNoCumplidosException("La tarjeta no ha pasado por todas las listas necesarias para moverse a la lista destino");
			}
		}
	}
	
	// Hace uso de los métodos validarBloqueo, validarLimite y validarCrearEnListaConPrerrequisitos para validar todas las reglas
	// de negocio a la hora de crear la tarjeta (R1, R2, HH1)
	public void validarCreacion(Tablero tablero, Lista listaDestino)
			throws TableroBloqueadoException, LimiteListaSuperadoException, PrerrequisitosNoCumplidosException {
		
		if (tablero == null) {
			throw new IllegalArgumentException("El tablero no puede ser nulo");
		}
		
		if (listaDestino == null) {
			throw new IllegalArgumentException("La lista destino no puede ser nula");
		}
		
		validarBloqueo(tablero);
		validarLimite(listaDestino);
		validarCrearEnListaConPrerrequisitos(listaDestino);
	}
	
	// Hace uso de los métodos validarLimite y validarPrerrequisitos para validar todas las reglas de negocio
	// a la hora de mover una tarjeta de una lista a otra. Si el tablero está bloqueado se pueden mover las tarjetas (R2, HH1)
	public void validarMovimientoEntreListas(Tarjeta tarjeta, Lista listaDestino)
			throws LimiteListaSuperadoException, PrerrequisitosNoCumplidosException {
		
		if (tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta no puede ser nula");
		}
		
		if (listaDestino == null) {
			throw new IllegalArgumentException("La lista destino no puede ser nula");
		}
		
		validarLimite(listaDestino);
		validarPrerrequisitos(tarjeta, listaDestino);	
	}
	
	public void validarCompletar(Lista listaEspecial, Tarjeta tarjeta) {

		if (listaEspecial == null) {
			throw new IllegalArgumentException("La lista especial no puede ser nula");
		}

		if (tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta no puede ser nula");
		}

		if (!listaEspecial.isEspecial()) {
			throw new ListaEspecialInvalidaException("La lista indicada no es una lista especial");
		}

		if (listaEspecial.getLimite() != null) {
			throw new ListaEspecialInvalidaException("La lista especial no puede tener límite de tarjetas");
		}

		if (tarjeta.getListaActual().equals(listaEspecial.getIdentificador())) {
			throw new TarjetaYaCompletadaException("La tarjeta ya está en la lista especial de completadas");
		}

		// Completar una tarjeta es, en esencia, moverla a la lista especial
		validarPrerrequisitos(tarjeta, listaEspecial);
	}
	
	public void validarConfiguracionPrerrequisitos(List<Tarjeta> tarjetasLista, Set<ListaId> prerrequisitos)
			throws PrerrequisitosNoCumplidosException {

		if (tarjetasLista == null) {
			throw new IllegalArgumentException("La lista de tarjetas no puede ser nula");
		}

		if (prerrequisitos == null) {
			throw new IllegalArgumentException("Los prerrequisitos no pueden ser nulos");
		}

		for (Tarjeta tarjeta : tarjetasLista) {
			if (tarjeta == null) {
				throw new IllegalArgumentException("No puede haber tarjetas nulas en la lista");
			}

			for (ListaId prerrequisito : prerrequisitos) {
				if (prerrequisito == null) {
					throw new IllegalArgumentException("No puede haber prerrequisitos nulos");
				}

				if (!tarjeta.getListasVisitadas().contains(prerrequisito)) {
					throw new PrerrequisitosNoCumplidosException(
							"No se pueden configurar los prerrequisitos porque la tarjeta " + tarjeta.getIdentificador()
									+ " no ha pasado por la lista prerrequisito " + prerrequisito);
				}
			}
		}
	}
}
