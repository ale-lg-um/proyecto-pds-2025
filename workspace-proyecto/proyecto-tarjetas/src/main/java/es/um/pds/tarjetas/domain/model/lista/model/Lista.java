package es.um.pds.tarjetas.domain.model.lista.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;

//@Entity
public class Lista {
	// Atributos
	private final ListaId identificador;
	private String nombreLista;
	private final List<TarjetaId> listaTarjetas;
	private boolean especial;
	private Integer limite;	// Integer para poder tener listas infinitas en caso de que no se configure límite (int no permite nulo, Integer sí)
	private final Set<ListaId> prerrequisitos;	// Listas por las que ha tenido que pasar antes la tarjeta para estar ahí
	private Tablero tablero; // necesario para el eliminarPorTableroId de repoListas
	
	// Constructor
	private Lista(ListaId identificador, String nombre) {
		this.identificador = identificador;
		this.nombreLista = nombre;
		this.listaTarjetas = new ArrayList<>();
		this.especial = false;	// Cuando se crea una lista aún no es especial
		this.limite = null; // Al principio, una lista puede ser infinita
		this.prerrequisitos = new HashSet<>();
	}
	
	// Método factoría
	public static Lista of(ListaId identificador, String nombre) throws ListaInvalidaException {
		if (identificador == null) {
			throw new ListaInvalidaException("La lista debe tener un identificador");
		}
		
		// Permitimos que la lista no tenga nombre, no comprobamos si nombre es nulo o blanco
		
		return new Lista(identificador, nombre);
	}
	
	// Getters
	public ListaId getIdentificador() {
		return this.identificador;
	}
	
	public Tablero getTablero() {
		return this.tablero;
	}
	
	public String getNombreLista() {
		return this.nombreLista;
	}
	
	public List<TarjetaId> getListaTarjetas() {
		return Collections.unmodifiableList(this.listaTarjetas);
	}
	
	public Integer getLimite() {
		return this.limite;
	}
	
	public Set<ListaId> getPrerrequisitos() {
		return Collections.unmodifiableSet(prerrequisitos);
	}
	
	public boolean isEspecial() {
		return this.especial;
	}
	
	// Overrides
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof Lista other)) return false;
	    return Objects.equals(this.identificador, other.identificador);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(identificador);
	}
	
	// Funcionalidades
	public void anadirTarjeta(TarjetaId nueva) {
		if (nueva == null) {
			throw new IllegalArgumentException("La tarjeta que se desea añadir no puede ser nula");
		}
		
		if (listaTarjetas.contains(nueva)) {
			throw new IllegalArgumentException("La tarjeta ya pertenece a la lista");
		}
		
		// Se podría comprobar también en el servicio de aplicación
		if (limite != null && listaTarjetas.size() >= limite) {
			throw new IllegalArgumentException("Se ha superado el límite de tarjetas de la lista");
		}
		
		this.listaTarjetas.add(nueva);
	}
	
	public void quitarTarjeta(TarjetaId tarjeta) {
		if (tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta que se desea eliminar no puede ser nula");
		}
		
		if (!listaTarjetas.contains(tarjeta)) {
			throw new IllegalArgumentException("La tarjeta que se desea eliminar no existe en esta lista");
		}
		
		this.listaTarjetas.remove(tarjeta);
	}
	
	public void renombrar(String nuevoNombre) {
		this.nombreLista = nuevoNombre;
	}
	
	public void hacerEspecial() {
		this.especial = true;
	}
	
	public void quitarEspecial() {
		this.especial = false;
	}
	
	public void moverTarjeta(TarjetaId tarjeta, int nuevaPosicion) {
		if (tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta que se desea mover no puede ser nula");
		}

		if (!listaTarjetas.contains(tarjeta)) {
			throw new IllegalArgumentException("La tarjeta que se desea mover no pertenece a la lista");
		}
		
		if (nuevaPosicion < 0 || nuevaPosicion >= listaTarjetas.size()) {
			throw new IllegalArgumentException("La nueva posición a la que se desea mover la tarjeta no es válida");
		}

		listaTarjetas.remove(tarjeta);
		listaTarjetas.add(nuevaPosicion, tarjeta);
    }
	
	public void configurarLimite(Integer limite) {
		if(limite != null && limite <= 0) {
			throw new IllegalArgumentException("El límite debe ser un entero positivo");
		}
		this.limite = limite;
	}
	
	public void configurarPrerrequisitos(List<ListaId> prerrequisitos) {
		this.prerrequisitos.clear();
		if (prerrequisitos != null) {
			this.prerrequisitos.addAll(prerrequisitos);
		}
	}
}
