package es.um.pds.tarjetas.domain.model.tablero;

import java.util.ArrayList;
import java.util.List;

import es.um.pds.tarjetas.domain.model.tarjeta.Tarjeta;

//@Entity
public class Lista {
	// Atributos
	private ListaId identificador;
	private String nombreLista;
	private List<Tarjeta> listaTarjetas;
	
	// Constructor
	public Lista(String nombre, ListaId id) {
		this.identificador = id;
		this.nombreLista = nombre;
		this.listaTarjetas = new ArrayList<>();
	}
	
	// Funcionalidades
	public void anadirTarjeta(Tarjeta nueva) {
		this.listaTarjetas.add(nueva);
	}
}
