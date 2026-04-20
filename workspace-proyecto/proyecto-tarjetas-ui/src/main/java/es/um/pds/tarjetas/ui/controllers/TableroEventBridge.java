package es.um.pds.tarjetas.ui.controllers;

import java.util.function.Consumer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import es.um.pds.tarjetas.domain.model.tarjeta.eventos.TarjetaCompletada;

@Component
public class TableroEventBridge {
	// Atributos
	private Consumer<TarjetaCompletada> accionPantalla; // Se guarda la conexión con la pantalla
	
	public void conectarConPantalla(Consumer<TarjetaCompletada> accion) {
		this.accionPantalla = accion;
	}
	
	@EventListener
	public void recibirEvento(TarjetaCompletada evento) {
		System.out.println("📡 PUENTE: Evento recibido desde Spring. Tarjeta: " + evento.tarjetaId().getId());
		if(accionPantalla != null) {
			accionPantalla.accept(evento);
		} else {
			System.out.println("⚠️ PUENTE: Error, no hay ninguna pantalla conectada.");
		}
	}
}
