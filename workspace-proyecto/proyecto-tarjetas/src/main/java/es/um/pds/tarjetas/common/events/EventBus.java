package es.um.pds.tarjetas.common.events;

public interface EventBus {
	public void publicar(EventoDominio evento);
	
	public void publicarTodos(AgregadoConEventos agregado);
}
