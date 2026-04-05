package es.um.pds.tarjetas.domain.model.lista.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;

public class Lista {
	// Atributos
	private final ListaId identificador;
	private String nombreLista;
	
	// Lo modelamos como lista porque consideramos la posición de la tarjeta
	// NO consideramos añadir dos tarjetas con el mismo ID en el servicio de aplicación
	private final List<TarjetaId> listaTarjetas;
	private boolean especial;
	
	// Integer para poder tener listas infinitas en caso de que no se configure límite (int no permite nulo, Integer sí)
	private Integer limite;
	
	// Listas por las que ha tenido que pasar antes la tarjeta para estar ahí
	private final Set<ListaId> prerrequisitos;
	private TableroId tablero;
	
	// Constructor para creación nueva
	private Lista(ListaId identificador, String nombre) {
		this.identificador = identificador;
		this.nombreLista = nombre;
		this.listaTarjetas = new ArrayList<>();
		this.especial = false;					// Cuando se crea una lista aún no es especial
		this.limite = null; 					// Al principio, una lista puede ser infinita
		this.prerrequisitos = new HashSet<>();
	}
	
	// Constructor para reconstrucción
	private Lista(ListaId identificador, String nombre, List<TarjetaId> listaTarjetas, boolean especial, Integer limite,
			Set<ListaId> prerrequisitos, TableroId tablero) {
		this.identificador = identificador;
		this.nombreLista = nombre;
		this.listaTarjetas = new ArrayList<>(listaTarjetas);
		this.especial = especial;
		this.limite = limite;
		this.prerrequisitos = new HashSet<>(prerrequisitos);
		this.tablero = tablero;
	}
	
	// Método factoría de nueva creación
	public static Lista of(ListaId identificador, String nombre) {
		validarDatosBasicos(identificador, nombre);
		
		return new Lista(identificador, nombre);
	}
	
	// Método factoría de reconstrucción
	public static Lista reconstruir(ListaId identificador, String nombre, List<TarjetaId> listaTarjetas,
			boolean especial, Integer limite, Set<ListaId> prerrequisitos, TableroId tablero) {
		validarDatosBasicos(identificador, nombre);

		if (listaTarjetas == null) {
			throw new ListaInvalidaException("La lista de tarjetas no puede ser nula");
		}

		if (listaTarjetas.contains(null)) {
			throw new ListaInvalidaException("La lista no puede contener identificadores de tarjeta nulos");
		}

		if (prerrequisitos == null) {
			throw new ListaInvalidaException("Los prerrequisitos no pueden ser nulos");
		}

		if (prerrequisitos.contains(null)) {
			throw new ListaInvalidaException("La lista no puede contener prerrequisitos nulos");
		}

		if (especial && limite != null) {
			throw new ListaInvalidaException("Una lista especial no puede tener límite");
		}

		if (limite != null && limite <= 0) {
			throw new ListaInvalidaException("El límite debe ser un entero positivo");
		}

		if (limite != null && listaTarjetas.size() > limite) {
			throw new ListaInvalidaException("La lista contiene más tarjetas que el límite configurado");
		}

		return new Lista(identificador, nombre, listaTarjetas, especial, limite, prerrequisitos, tablero);
	}
	
	// Métodos auxiliares
	private static void validarDatosBasicos(ListaId identificador, String nombre) {
		if (identificador == null) {
			throw new ListaInvalidaException("La lista debe tener un identificador");
		}

		if (nombre == null || nombre.isBlank()) {
			throw new ListaInvalidaException("La lista debe tener un nombre no vacío");
		}
	}
	
	// Getters
	public ListaId getIdentificador() {
		return this.identificador;
	}
	
	public TableroId getTablero() {
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
		
		// Se añade al final
		this.listaTarjetas.add(nueva);
	}
	
	public void eliminarTarjeta(TarjetaId tarjeta) {
		if (tarjeta == null) {
			throw new IllegalArgumentException("La tarjeta que se desea eliminar no puede ser nula");
		}
		
		if (!listaTarjetas.contains(tarjeta)) {
			throw new IllegalArgumentException("La tarjeta que se desea eliminar no existe en esta lista");
		}
		
		this.listaTarjetas.remove(tarjeta);
	}
	
	public void renombrar(String nuevoNombre) {
		if (nuevoNombre == null || nuevoNombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
		}
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
		if (this.especial) {
			throw new IllegalStateException("No se puede establecer límite para una lista especial");
		}
		
		if (limite != null && limite <= 0) {
			throw new IllegalArgumentException("El límite debe ser un entero positivo");
		}
		
		if (limite!= null && listaTarjetas.size() > limite) {
			throw new IllegalStateException("No se puede configurar el límite en esta lista porque ya contiene más tarjetas que el límite");
		}
		
		this.limite = limite;
	}
	
	public void configurarPrerrequisitos(Set<ListaId> prerrequisitos) {
		this.prerrequisitos.clear();
		if (prerrequisitos != null) {
			this.prerrequisitos.addAll(prerrequisitos);
		}
	}
	
	public void asignarATablero(TableroId tablero) {
		if (tablero == null) {
			throw new IllegalArgumentException("El tablero no puede ser nulo");
		}
		this.tablero = tablero;
	}
}
