package es.um.pds.tarjetas.domain.model.tablero.model;


import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.common.exceptions.TableroInvalidoException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public class Tablero {
	// Atributos
	private final TableroId identificador;			// Identificador del tablero
	private String nombre;							// Nombre del tablero
	private final String tokenUrl;					// Token de la URL que se genera del tablero
	private final Set<ListaId> listas;				// Aquí se guardan las listas del tablero
	private ListaId listaEspecial;					// Lista de completadas, no definida al principio
	private EstadoBloqueo estadoBloqueo;			// Desde, Hasta y Descripción
	private UsuarioId creador;						// Identificador del usuario que creó el tablero
	
	// Constructor para creación de un tablero nuevo
	private Tablero(TableroId identificador, String nombre, String tokenUrl, UsuarioId creador) {
		this.identificador = identificador;
		this.nombre = nombre;
		this.tokenUrl = tokenUrl;
		this.listas = new HashSet<>();
		this.listaEspecial = null;	// Empieza sin lista especial
		this.estadoBloqueo = null;	// Empieza sin estar bloqueado
		this.creador = creador;
	}
	
	// Constructor para reconstrucción desde persistencia
	private Tablero(TableroId identificador, String nombre, String tokenUrl, UsuarioId creador, Set<ListaId> listas,
			ListaId listaEspecial, EstadoBloqueo estadoBloqueo) {
		this.identificador = identificador;
		this.nombre = nombre;
		this.tokenUrl = tokenUrl;
		this.creador = creador;
		this.listas = new HashSet<>(listas);
		this.listaEspecial = listaEspecial;
		this.estadoBloqueo = estadoBloqueo;
	}
	
	// Método factoría de nueva creación
	public static Tablero of(TableroId identificador, String nombre, String tokenUrl, UsuarioId creador) {
		validarDatosBasicos(identificador, nombre, tokenUrl, creador);
		
		return new Tablero(identificador, nombre, tokenUrl, creador);
	}
	
	// Método factoría de reconstrucción
	public static Tablero reconstruir(TableroId identificador, String nombre, String tokenUrl, UsuarioId creador,
			Set<ListaId> listas, ListaId listaEspecial, EstadoBloqueo estadoBloqueo) {
		validarDatosBasicos(identificador, nombre, tokenUrl, creador);

		if (listas == null) {
			throw new TableroInvalidoException("Las listas del tablero no pueden ser nulas");
		}

		if (listas.contains(null)) {
			throw new TableroInvalidoException("El tablero no puede contener identificadores de lista nulos");
		}

		if (listaEspecial != null && !listas.contains(listaEspecial)) {
			throw new TableroInvalidoException("La lista especial debe pertenecer al conjunto de listas del tablero");
		}

		return new Tablero(identificador, nombre, tokenUrl, creador, listas, listaEspecial, estadoBloqueo);
	}
	
	// Métodos auxiliares
	private static void validarDatosBasicos(TableroId identificador, String nombre, String tokenUrl, UsuarioId creador) {
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
	
	public String getTokenURL() {
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
		if(this.estadoBloqueo == null) {
			return false;
		}
		
		if(!this.estadoBloqueo.estaActivoAhora()) {
			this.estadoBloqueo = null;
			return false;
		}
		
		return true;
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
