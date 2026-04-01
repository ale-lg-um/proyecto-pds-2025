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
	private boolean completada;					// Podría prescindirse pero es más cómodo trabajar con el atributo
	
	// Constructor
	private Tarjeta(TarjetaId identificador, String titulo, ListaId listaActual, ContenidoTarjeta contenido) {
		this.identificador = identificador;
		this.titulo = titulo;
		this.fechaCreacion = LocalDate.now();
		this.listaActual = listaActual;
		this.contenido = contenido;
		this.etiquetas = new ArrayList<>();
		this.listasVisitadas = new HashSet<>();
		this.completada = false;
		
		this.listasVisitadas.add(listaActual);
		actualizarEstadoCompletitudSegunContenido();
	}
	
	// Método 'of' aplicando patrón creador y método factoría
	public static Tarjeta of(TarjetaId identificador, String titulo, ListaId listaActual, ContenidoTarjeta contenido) {
        if (identificador == null) {
            throw new TarjetaInvalidaException("La tarjeta debe tener un identificador");
        }
        
        // Se podrían permitir nombres vacíos
        if (titulo == null || titulo.isBlank()) {
        	throw new TarjetaInvalidaException("El nombre de la tarjeta no puede estar vacío");
        }
        
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
	
	public boolean isCompletada() {
		return this.completada;
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
	
	// Operación explícita para completar la tarjeta manualmente
	public void marcarComoCompletada() {
		this.completada = true;
	}
	
	// Por si en el futuro se reabre la tarjeta
	public void marcarComoPendiente() {
		this.completada = false;
	}

	// Marcar un ítem de checklist y autocompletar la tarjeta si procede
	public void marcarItemChecklistComoCompletado(int indice) {
		if (!(contenido instanceof Checklist checklist)) {
			throw new IllegalStateException("La tarjeta no contiene una checklist");
		}

		List<ItemChecklist> items = checklist.getItems();

		if (indice < 0 || indice >= items.size()) {
			throw new IllegalArgumentException("El índice del ítem está fuera de rango");
		}

		items.get(indice).marcarComoCompletado();
		actualizarEstadoCompletitudSegunContenido();
	}

	public void marcarItemChecklistComoPendiente(int indice) {
		if (!(contenido instanceof Checklist checklist)) {
			throw new IllegalStateException("La tarjeta no contiene una checklist");
		}

		List<ItemChecklist> items = checklist.getItems();

		if (indice < 0 || indice >= items.size()) {
			throw new IllegalArgumentException("El índice del ítem está fuera de rango");
		}

		items.get(indice).marcarComoPendiente();
		actualizarEstadoCompletitudSegunContenido();
	}

	private void actualizarEstadoCompletitudSegunContenido() {
		if (contenido instanceof Checklist checklist) {
			this.completada = checklist.todosCompletados();
		} else {
			this.completada = false;
		}
	}
}
