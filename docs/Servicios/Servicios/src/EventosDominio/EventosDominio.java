package EventosDominio;

public class EventosDominio {
	TableroCreado(TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreTablero);
	TableroCreadoDesdePlantilla(TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreTablero, String nombrePlantilla);
	TableroEditado(TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreAnterior, String nombreNuevo);
	TableroEliminado(TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreTablero);
	TableroBloqueado(TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String motivo);
	TableroDesbloqueado(TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp);
	
	ListaCreada(ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreLista);
	ListaEditada(ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreAnterior, String nombreNuevo);
	ListaEliminada(ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreLista);
	LimiteListaConfigurado(ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, Integer limiteAnterior, Integer limiteNuevo, String nombreLista);
	ListaEspecialDefinida(ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreLista);
	// “Una lista define que una tarjeta tiene que haber pasado por otras listas antes”
	PrerrequisitosListaConfigurados(ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, Set<PrerrequisitoInfo> prerrequisitos, String nombreLista);

	TarjetaCreada(TarjetaId tarjetaId, ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreTarjeta, String nombreLista);
	TarjetaEditada(TarjetaId tarjetaId, String nombreTarjeta, ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, ContenidoTarjeta contenidoAnterior, ContenidoTarjeta contenidoNuevo);
	TarjetaRenombrada(TarjetaId tarjetaId, ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreAnterior, String nombreNuevo);
	TarjetaEliminada(TarjetaId tarjetaId, ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String titulo);
	TarjetaMovida(TarjetaId tarjetaId, ListaId listaOrigenId, String listaOrigen, ListaId listaDestinoId, String listaDestino, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreTarjeta);
	// Realmente es mover una tarjeta a la lista especial
	TarjetaCompletada(TarjetaId tarjetaId, ListaId listaId, String nombreLista, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, String nombreTarjeta);	
	EtiquetaAnadidaATarjeta(TarjetaId tarjetaId, ListaId listaId, TableroId tableroId, UsuarioId usuarioId, LocalDateTime timestamp, Etiqueta etiqueta, String nombreTarjeta);
	EtiquetaEliminadaDeTarjeta(tableroId, tarjetaId, actorEmail, nombre, color);
	// Como son VO, realmente consta de una eliminación y una adición
	EtiquetaModificadaEnTarjeta(tableroId, tarjetaId, actorEmail, 
			nombreAntiguo, colorAntiguo, nombreNuevo, colorNuevo);
}
