package es.um.pds.tarjetas.domain.model.tablero.model;


import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.application.common.exceptions.TableroInvalidoException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;


//@Entity
public class Tablero {
	// Atributos
	private final TableroId identificador;			// Identificador del tablero
	private String nombre;							// Nombre del tablero
	private final String tokenUrl;					// Token de la URL que se genera del tablero
	private final Set<ListaId> listas;				// Aquí se guardan las listas del tablero
	private ListaId listaEspecial;					// Lista de completadas, no definida al principio
	private EstadoBloqueo estadoBloqueo;			// Desde, Hasta y Descripción
	private UsuarioId creador;						// para lo de la lista de ids por usuario en el repositorio
	
	// Constructor
	private Tablero(TableroId identificador, String nombre, String tokenUrl, UsuarioId creador) {
		this.identificador = identificador;
		this.nombre = nombre;
		this.tokenUrl = tokenUrl;
		this.listas = new HashSet<>();
		this.listaEspecial = null;	// Empieza sin lista especial
		this.estadoBloqueo = null;	// Empieza sin estar bloqueado
		this.creador = creador;
	}
	
	// Método factoría
	public static Tablero of(TableroId identificador, String nombre, String tokenUrl, UsuarioId creador) throws TableroInvalidoException {
		if (identificador == null) {
			throw new TableroInvalidoException("El tablero debe tener un identificador");
		}
		
		if (nombre == null || nombre.isBlank()) {
			throw new TableroInvalidoException("El tablero debe tener un nombre");
		}
		
		if (tokenUrl == null || tokenUrl.isBlank()) {
			throw new TableroInvalidoException("El tablero debe tener un token para su URL");
		}
		
		if (creador == null) {
			throw new TableroInvalidoException("El tablero debe tener un usuario creador");
		}
		
		return new Tablero(identificador, nombre, tokenUrl, creador);
	}
	
	// Getters
	public TableroId getIdentificador() {
		return this.identificador;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public UsuarioId getCreador() {
		return this.creador;
	}
	
	public String getTokenUrl() {
		return this.tokenUrl;
	}
	
	public Set<ListaId> getListas() {
		return Collections.unmodifiableSet(listas);
	}
	
	public ListaId getListaEspecial() {
		return this.listaEspecial;
	}
	
	public EstadoBloqueo getEstadoBloqueo() {
		return this.estadoBloqueo;
	}
	
	public boolean isBloqueado() {
		return (this.estadoBloqueo != null);
	}
	
	// Overrides
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof Tablero other)) return false;
	    return Objects.equals(this.identificador, other.identificador);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(identificador);
	}
	
	// Funcionalidades
	public void anadirLista(ListaId nuevaLista) {
		
		if (nuevaLista == null) {
			throw new IllegalArgumentException("La lista que se desea añadir no puede ser nula");
		}
		
		if (this.listas.contains(nuevaLista)) {
			throw new ListaInvalidaException("La lista que se desea añadir ya existe");
		}
		
		this.listas.add(nuevaLista);
	}
	
	public void eliminarLista(ListaId lista) {	
		if (lista == null) {
			throw new IllegalArgumentException("La lista que se desea eliminar no puede ser nula");	
		}
		
		if (!listas.contains(lista)) {
			throw new IllegalArgumentException("La lista que se desea eliminar no existe");
		}
		
		if (lista.equals(listaEspecial)) {
			this.listaEspecial = null;
		}
		
		this.listas.remove(lista);
	}
	
	public void definirListaEspecial(ListaId nuevaLista) {
		if (nuevaLista != null && nuevaLista.equals(listaEspecial)) {
			throw new IllegalStateException("Esta lista ya es especial");
		}
		
		if (this.listaEspecial != null) {
		    throw new IllegalStateException("Ya existe una lista especial");
		}
		
		this.listaEspecial = nuevaLista;
	}
	
	public void bloquear(EstadoBloqueo bloqueo) {
		if(this.isBloqueado()) {
			throw new IllegalStateException("El tablero ya está bloqueado");
		} else if(bloqueo == null) {
			throw new IllegalArgumentException("El bloqueo no puede ser nulo");
		} else {
			this.estadoBloqueo = bloqueo;
		}
	}
	
	public void desbloquear() {
		if (!this.isBloqueado()) {
			throw new IllegalStateException("El tablero ya está desbloqueado");
		} else {
			this.estadoBloqueo = null;
		}
	}
	
	public void renombrar(String nuevoNombre) {
		if (nuevoNombre == null || nuevoNombre.isBlank()) {
			throw new IllegalArgumentException("El nombre del tablero no puede estar vacío");
		}
		
		this.nombre = nuevoNombre;
	}
}
