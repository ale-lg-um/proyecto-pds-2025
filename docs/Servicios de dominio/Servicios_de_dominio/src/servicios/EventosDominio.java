package servicios;

public class EventosDominio {
	TableroCreado(tableroId, actorEmail, desdePlantilla?)
	TarjetaCreada(tableroId, tarjetaId, listaId, actorEmail)
	TarjetaMovida(tableroId, tarjetaId, fromListaId, toListaId, actorEmail)
	TarjetaCompletada(tableroId, tarjetaId, actorEmail)
	TableroBloqueado(tableroId, especBloqueo, actorEmail)
	PlantillaCreada(plantillaId, actorEmail)
}
