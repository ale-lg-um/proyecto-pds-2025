package EventosDominio;

public class EventosDominio {
	TableroCreado(tableroId, actorEmail);
	TableroCreadoDesdePlantilla(tableroId, actorEmail, nombrePlantilla);
	TableroEditado(tableroId, actorEmail, nombreAntiguo, nombreNuevo);
	TableroEliminado(tableroId, actorEmail);
	TableroBloqueado(tableroId, especBloqueo, actorEmail);
	TableroDesbloqueado(tableroId, actorEmail);
	
	ListaCreada(tableroId, listaId, actorEmail);
	ListaEditada(tableroId, listaId, actorEmail, nombreAntiguo, nombreNuevo);
	ListaEliminada(tableroId, listaId, actorEmail);
	LimiteListaConfigurado(tableroId, listaId, limite, actorEmail);
	ListaEspecialDefinida(tableroId, listaId, actorEmail);
	// “Una lista define que una tarjeta tiene que haber pasado por otras listas antes”
	PrerrequisitosListaConfigurados(tableroId, listaId, actorEmail, listasPrevias);

	TarjetaCreada(tableroId, tarjetaId, listaId, actorEmail);
	TarjetaEditada(tableroId, tarjetaId, actorEmail, 
			contenidoAntiguo, contenidoNuevo);
	TarjetaEliminada(tableroId, tarjetaId, listaId, actorEmail);
	TarjetaMovida(tableroId, tarjetaId, fromListaId, toListaId, actorEmail);
	// Realmente es mover una tarjeta a la lista especial
	TarjetaCompletada(tableroId, tarjetaId, actorEmail);	
	TarjetaEtiquetada(tableroId, tarjetaId, actorEmail, nombre, color);
	EtiquetaEliminadaDeTarjeta(tableroId, tarjetaId, actorEmail, nombre, color);
	// Como son VO, realmente consta de una eliminación y una adición
	EtiquetaModificadaEnTarjeta(tableroId, tarjetaId, actorEmail, 
			nombreAntiguo, colorAntiguo, nombreNuevo, colorNuevo);
}
