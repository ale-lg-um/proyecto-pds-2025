package es.um.pds.tarjetas.domain.model.tablero;

import java.util.ArrayList;
import java.util.List;

import es.um.pds.tarjetas.domain.model.usuario.Email;

//@Entity
public class Tablero {
	// Atributos
	private String nombre;				// Nombre del tablero
	private Email usuarioCreador;		// Email del usuario que ha creado el tablero
	private String url;					// URL del tablero
	private TableroId identificador;	// Identificador del tablero
	private List<Lista> listas;			// Aquí se guardan las listas del tablero
	
	// Constructor
	public Tablero(TableroId id, String nombre, Email usuarioCreador) {
		this.nombre = nombre;
		this.usuarioCreador = usuarioCreador;
		this.identificador = id;
		this.listas = new ArrayList<>();
	}
}
