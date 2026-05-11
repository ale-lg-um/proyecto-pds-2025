package serviciosDominio;

public class PoliticaTarjetas {
	
	// Hace uso de los métodos validarBloqueo, validarLimite y validarCrearEnListaConPrerrequisitos para
	// validar todas las reglas de negocio a la hora de crear la tarjeta (R1, R2, HH1)
	public void validarCreacion(Tablero tablero, Lista listaDestino);
	
	// Hace uso de los métodos validarLimite y validarPrerrequisitos para validar todas las reglas de negocio
	// a la hora de mover una tarjeta de una lista a otra. Si el tablero está bloqueado se pueden mover las tarjetas (R2, HH1)
	public void validarMovimientoEntreListas(Tarjeta tarjeta, Lista listaDestino);
	
	// En esencia, mover una tarjeta a la lista especial
	public void validarCompletar(Lista listaEspecial, Tarjeta tarjeta);
	
	// Comprobar que las tarjetas de una lista cumplen con los prerrequisitos antes de ser configurados (HB3)
	public void validarConfiguracionPrerrequisitos(List<Tarjeta> tarjetasLista, Set<ListaId> prerrequisitos);
}
