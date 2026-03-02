package es.um.pds.tarjetas.domain.model.tablero;

import java.util.List;

import es.um.pds.tarjetas.domain.model.tarea.TareaId;

//@Entity
public class Lista {
	// Atributos
	private ListaId identificador;
	private String nombreLista;
	private List<TareaId> listaTareas;
}
