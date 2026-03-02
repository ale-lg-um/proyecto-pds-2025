package es.um.pds.tarjetas.domain.model.tablero;

import es.um.pds.tarjetas.domain.model.usuario.Usuario;

//@Entity
public class Tablero {
	// Atributos
	private String nombre;
	private Usuario usuarioCreador;
	private String url;
	private TableroId identificador;
}
