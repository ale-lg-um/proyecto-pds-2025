package es.um.pds.tarjetas.domain.services;

import java.util.List;
import java.util.Set;

import es.um.pds.tarjetas.application.common.exceptions.LimiteListaSuperadoException;
import es.um.pds.tarjetas.application.common.exceptions.ListaEspecialInvalidaException;
import es.um.pds.tarjetas.application.common.exceptions.NoExisteListaEspecialException;
import es.um.pds.tarjetas.application.common.exceptions.PrerrequisitosNoCumplidosException;
import es.um.pds.tarjetas.application.common.exceptions.TableroBloqueadoException;
import es.um.pds.tarjetas.application.common.exceptions.TarjetaYaCompletadaException;
import es.um.pds.tarjetas.domain.model.lista.Lista;
import es.um.pds.tarjetas.domain.model.lista.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.EstadoBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.Tarjeta;

public class PoliticaTarjetas {
	
	// Para acceder a los tableros, listas y tarjetas llamaremos en la implementación de los servicios de aplicación
	// a los métodos buscar de los repositorios. Por eso aquí trabajamos con las entidades y no con los IDs.
	
	// Método auxiliar (no se utiliza)
	
	/*
	private Lista buscarLista(Set<Lista> listas, ListaId id) throws ListaInvalidaException {
	    return listas.stream()
	            .filter(l -> l.getIdentificador().equals(id))
	            .findFirst()
	            .orElseThrow(() -> new ListaInvalidaException(
	                    "La lista especificada no se encuentra en este tablero"
	            ));
	}
	*/
	
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
	
	// Valida que exista lista especial de completadas
	private Lista validarListaEspecial(Set<Lista> listasTablero) throws NoExisteListaEspecialException, ListaEspecialInvalidaException {

	    List<Lista> listasEspeciales = listasTablero.stream()
	            .filter(Lista::isEspecial)
	            .toList();

	    if (listasEspeciales.isEmpty()) {
	        throw new NoExisteListaEspecialException("El tablero no contiene ninguna lista especial");
	    }

	    if (listasEspeciales.size() > 1) {
	        throw new ListaEspecialInvalidaException("El tablero contiene más de una lista especial");
	    }

	    Lista listaEspecial = listasEspeciales.get(0);

	    if (listaEspecial.getLimite() != null) {
	        throw new ListaEspecialInvalidaException("La lista especial no puede tener límite de tarjetas");
	    }
	    
	    // Devuelve el ID de la única lista especial que hay
	    return listasEspeciales.get(0);
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
	
	// Comprobar que la tarjeta no esté ya en la lista especial
	public void validarCompletar(Set<Lista> listasTablero, Tarjeta tarjeta) 
			throws NoExisteListaEspecialException, ListaEspecialInvalidaException, TarjetaYaCompletadaException, PrerrequisitosNoCumplidosException {
		
		if (listasTablero == null) {
			throw new IllegalArgumentException("El tablero no puede estar vacío");
		}
		
		if (tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta no puede ser nula");
		}
		
		Lista listaEspecial = validarListaEspecial(listasTablero);
		
		if (tarjeta.getListaActual().equals(listaEspecial.getIdentificador())) {
			throw new TarjetaYaCompletadaException("La tarjeta ya está en la lista especial de completadas");
		}
		
		// Se podría hacer también con validarMovimientoEntreListas porque completar una tarjeta es moverla a la lista de especiales
		validarPrerrequisitos(tarjeta, listaEspecial);
	}
}
