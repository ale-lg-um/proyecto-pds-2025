package es.um.pds.tarjetas.domain.services;

import es.um.pds.tarjetas.domain.model.lista.Lista;
import es.um.pds.tarjetas.domain.model.lista.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.TarjetaId;

public class PoliticaTarjetas {
	public void validarCreacion(Tablero tablero, ListaId destino) throws Exception {
		if(tablero.isBloqueado()) {
			throw new Exception("El tablero está bloqueado. Cancelando...");
		}
		
		Lista dest = buscarLista(tablero, destino);
		
		if(dest.getLimite() != null && dest.getListaTarjetas().size() >= dest.getLimite()) {
			throw new Exception("La lista ha alcanzado el límite. Cancelando...");
		}
		
		if(!dest.getPrerrequisitos().isEmpty()) {
			throw new Exception("No se puede crear una tarjeta directamente en una lista con prerrequisitos. Cancelando...");
		}
	}
	
	public void validarMovimiento(Tablero tablero, TarjetaId tarjeta, ListaId destino) throws Exception {
		if(tablero.isBloqueado()) {
			throw new Exception("El tablero está bloqueado. Cancelando...");
		}
		
		Lista dest = buscarLista(tablero, destino);
		
		if (dest.getLimite() != null && dest.getListaTarjetas().size() >= dest.getLimite()) {
			throw new Exception("No se puede mover la tarjeta: la lista de destino está llena.");
		}
	}
	
	
	public void validarCompletar(Tablero tablero, TarjetaId tarjeta) throws Exception {
		if(tablero.isBloqueado()) {
			throw new Exception("El tablero está bloqueado. Cancelando...");
		}
		
		boolean hayEspecial = tablero.getListas().stream()
				.anyMatch(Lista::isEspecial);
		
		if(!hayEspecial) {
			throw new Exception("El tablero no contiene listas especiales. Cancelando...");
		}
	}
	
	// Método auxiliar
	private Lista buscarLista(Tablero tablero, ListaId id) throws Exception {
		Lista lista = tablero.getListas().stream()
				.filter(l -> l.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new Exception("La lista especificada no se encuentra en este tablero"));
		
		return lista;
	}
}
