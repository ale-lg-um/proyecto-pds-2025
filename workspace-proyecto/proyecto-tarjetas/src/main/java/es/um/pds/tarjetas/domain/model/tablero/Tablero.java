package es.um.pds.tarjetas.domain.model.tablero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.um.pds.tarjetas.domain.model.usuario.UsuarioId;

//@Entity
public class Tablero {
	// Atributos
	private String nombre;						// Nombre del tablero
	private UsuarioId usuarioCreador;			// Email del usuario que ha creado el tablero
	private String url;							// URL del tablero
	private TableroId identificador;			// Identificador del tablero
	private List<Lista> listas;					// Aquí se guardan las listas del tablero
	private EspecBloqueo especificacionBloqueo;	// Motivo por el cual se bloquea el tablero
	
	// Constructor
	public Tablero(TableroId id, String nombre, UsuarioId usuarioCreador) {
		this.nombre = nombre;
		this.usuarioCreador = usuarioCreador;
		this.identificador = id;
		this.listas = new ArrayList<>();
		this.especificacionBloqueo = null;
	}
	
	// Getters y setters
	public String getNombre() {
		return this.nombre;
	}
	
	public void setNombre(String nuevoNombre, UsuarioId creador) throws Exception {
		if(!usuarioCreador.equals(creador)) {
			throw new Exception("El usuario no es el creador de este tablero. El renombrado ha fallado.");
		}
		
		this.nombre = nuevoNombre;
	}
	
	public UsuarioId getUsuarioCreador() {
		return this.usuarioCreador;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public TableroId getIdentificador() {
		return this.identificador;
	}
	
	public List<Lista> getListas() {
		return Collections.unmodifiableList(listas);
	}
	
	public boolean isBloqueado() {
		return (this.especificacionBloqueo != null);
	}
	
	// Funcionalidades
	public void anadirLista(ListaId lista, String nombre, UsuarioId user) throws Exception {
		if(this.isBloqueado()) {
			throw new Exception("No se pueden añadir listas porque el tablero está bloqueado.");
		} else if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. Abortando");
		}
		Lista nueva = new Lista(nombre, lista);
		this.listas.add(nueva);
	}
	
	public void bloquear(EspecBloqueo motivo, UsuarioId user) throws Exception{
		if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. El bloqueo se ha cancelado");
		} else if(this.isBloqueado()) {
			throw new Exception("El tablero ya está bloqueado");
		} else if(motivo == null) {
			throw new IllegalArgumentException("El motivo no puede ser nulo");
		} else {
			this.especificacionBloqueo = motivo;
		}
	}
	
	public void desbloquear(UsuarioId user) throws Exception {
		if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. El desbloqueo se ha cancelado");
		} else if(!this.isBloqueado()) {
			throw new Exception("El tablero ya está desbloqueado");
		} else {
			this.especificacionBloqueo = null;
		}
	}
	
	public void renombrarLista(ListaId lista, String nuevoNombre, UsuarioId user) throws Exception{
		if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. No se modificará la lista");
		}
		
		Lista destino = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		destino.renombrar(nuevoNombre);
	}
	
	public void eliminarLista(ListaId lista, UsuarioId user) throws Exception {
		if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. No se modificará la lista");
		}
		
		Lista eliminada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		this.listas.remove(eliminada);
	}
	
	public void definirListaEspecial(ListaId lista, UsuarioId user) throws Exception {
		if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. No se modificará la lista");
		}
		
		Lista seleccionada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		seleccionada.hacerEspecial();
	}
	
	public void configurarLimiteLista(ListaId lista, int limite, UsuarioId user) throws Exception {
		if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. No se modificará la lista");
		}
		
		Lista seleccionada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		seleccionada.configurarLimite(limite);
	}
	
	public void configurarPrerrequisitosLista(ListaId lista, List<ListaId> prerrequisitos, UsuarioId user) throws Exception {
		if(!this.usuarioCreador.equals(user)) {
			throw new Exception("El usuario no es el creador de este tablero. No se modificará la lista");
		}
		
		Lista seleccionada = this.listas.stream()
				.filter(l -> l.getId().equals(lista))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
		
		seleccionada.configurarPrerrequisitos(prerrequisitos);
	}
}
