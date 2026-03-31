package es.um.pds.tarjetas.domain.model.tarjeta.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.application.common.exceptions.TarjetaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;

//@Entity
public class Tarjeta {
	// Atributos
	private final TarjetaId identificador;		
	private String titulo;					
	private final LocalDate fechaCreacion;
	private ListaId listaActual;
	private TableroId tablero;
	//private int posicionEnLista;				Se puede obtener viendo List<TarjetaId> en Lista
	private ContenidoTarjeta contenido;			// Puede ser una Tarea o un Checklist
	private final List<Etiqueta> etiquetas;		// Lista de etiquetas de la tarjeta (puede haber repetidas)
	private final Set<ListaId> listasVisitadas;	// Conjunto de listas por las que ha pasado la tarjeta
	
	// Constructor
	private Tarjeta(TarjetaId identificador, String titulo, ListaId listaActual, ContenidoTarjeta contenido) {
		this.identificador = identificador;
		this.titulo = titulo;
		this.fechaCreacion = LocalDate.now();
		this.listaActual = listaActual;
		this.contenido = contenido;
		this.etiquetas = new ArrayList<>();
		this.listasVisitadas = new HashSet<>();
		
		this.listasVisitadas.add(listaActual);
	}
	
	// Método 'of' aplicando patrón creador y método factoría
	public static Tarjeta of(TarjetaId identificador, String titulo, ListaId listaActual, ContenidoTarjeta contenido) throws TarjetaInvalidaException {
        if (identificador == null) {
            throw new TarjetaInvalidaException("La tarjeta debe tener un identificador");
        }
        
        // La tarjeta puede no tener título, no comprobamos el título == null
        
        if (contenido == null) {
            throw new TarjetaInvalidaException("La tarjeta no puede estar vacía, debe contener una tarea o una checklist");
        }
        
        if (listaActual == null) {
        	throw new TarjetaInvalidaException("La tarjeta tiene que ser creada dentro de una lista");
        }
        
        if (!(contenido instanceof Tarea) && !(contenido instanceof Checklist)) {
            throw new TarjetaInvalidaException("El contenido de la tarjeta debe ser una TAREA o una CHECKLIST.");
        }
         
        return new Tarjeta(identificador, titulo, listaActual, contenido);
    }
	
	// Getters
	public TarjetaId getIdentificador() {
		return this.identificador;
	}
	
	public String getTitulo() {
		return this.titulo;
	}
	
	public LocalDate getFechaCreacion() {
		return this.fechaCreacion;
	}
	
	public ListaId getListaActual() {
		return this.listaActual;
	}
	
	public TableroId getTablero() {
		return this.tablero;
	}
	
	public ContenidoTarjeta getContenido() {
		return this.contenido;
	}
	
	public List<Etiqueta> getEtiquetas() {
		return Collections.unmodifiableList(etiquetas);
	}
	
	public Set<ListaId> getListasVisitadas() {
		return Collections.unmodifiableSet(listasVisitadas);
	}
	
	// Overrides
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof Tarjeta other)) return false;
	    return Objects.equals(this.identificador, other.identificador);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(identificador);
	}
	
	
	// Funcionalidades
	public void cambiarListaActual(ListaId nuevaLista) {
		if (nuevaLista == null) {
			throw new IllegalArgumentException("La lista en la que se desea colocar la tarjeta no puede ser nula");
		}
		this.listaActual = nuevaLista;
		this.listasVisitadas.add(nuevaLista);
	}
	
	/*
	// El servicio de aplicación debe llamar a este método después de haber reorganizado el resto de tarjetas dentro de la lista
    public void cambiarPosicionEnLista(int nuevaPosicion) {
        if (nuevaPosicion < 0) throw new IllegalArgumentException("La posición no puede ser negativa");
        this.posicionEnLista = nuevaPosicion;
    }
    */
	
	public void anadirEtiqueta(Etiqueta nueva) {
		if (nueva == null) {
			throw new IllegalArgumentException("La etiqueta que se desea añadir no puede ser nula");
		}
		this.etiquetas.add(nueva);
	}
	
	public void eliminarEtiqueta(Etiqueta eliminada) {
		if (eliminada == null) {
			throw new IllegalArgumentException("La etiqueta que se desea eliminar no puede ser nula");
		}
		
		if (!etiquetas.contains(eliminada)) {
			throw new IllegalArgumentException("La etiqueta que se desea eliminar no existe");
		}
		
		this.etiquetas.remove(eliminada);
	}
	
	public void cambiarTitulo(String nuevoTitulo) {
		titulo = nuevoTitulo;
	}
	
	public void editarContenido(ContenidoTarjeta nuevoContenido) {
		if (nuevoContenido == null) {
			throw new IllegalArgumentException("El nuevo contenido no puede ser nulo");
		}
		
		this.contenido = nuevoContenido;
	}
	
	public void asignarATablero(TableroId tablero) {
		if (tablero == null) {
			throw new IllegalArgumentException("El identificador del tablero no puede ser nulo");
		}
		
		this.tablero = tablero;
	}
    
    /** Operaciones como servicios de aplicación orquestadores:
     * TarjetaId crearTarjeta(TableroId id, ListaId listaId, ContenidoTarjeta contenido, Actor actor);
     * void editarTarjeta(TableroId id, TarjetaId tarjetaId, ContenidoTarjeta nuevoContenido, Actor actor);
     * void eliminarTarjeta(TableroId id, TarjetaId tarjetaId, Actor actor);
     * void moverTarjeta(TableroId id, TarjetaId tarjetaId, ListaId
     * listaDestino, Actor actor);
     * // completarTarjeta realmente es mover una tarjeta a la lista especial
     * void completarTarjeta(TableroId id, TarjetaId id, Actor actor);

     */
    
	
}
