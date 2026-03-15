package es.um.pds.tarjetas.domain.model.tablero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.um.pds.tarjetas.domain.model.tarjeta.Tarjeta;

//@Entity
public class Lista {
	// Atributos
	private ListaId id;
	private String nombreLista;
	private List<Tarjeta> listaTarjetas;
	private boolean especial;
	private Integer limite; // Lo pongo como Integer para poder tener listas infinitas en caso de que no se configure límite (int no permite nulo, Integer sí)
	private List<ListaId> prerrequisitos;
	
	// Constructor
	public Lista(String nombre, ListaId id) {
		this.id = id;
		this.nombreLista = nombre;
		this.listaTarjetas = new ArrayList<>();
		this.especial = false;
		this.limite = null; // Al principio, una lista puede ser infinita
		this.prerrequisitos = new ArrayList<>();
	}
	
	// Getters
	public ListaId getId() {
		return this.id;
	}
	
	public String getNombreLista() {
		return this.nombreLista;
	}
	
	public List<Tarjeta> getListaTarjetas() {
		return Collections.unmodifiableList(this.listaTarjetas);
	}
	
	public Integer getLimite() {
		return this.limite;
	}
	
	public List<ListaId> getPrerrequisitos() {
		return Collections.unmodifiableList(prerrequisitos);
	}
	
	public boolean isEspecial() {
		return this.especial;
	}
	
	// Funcionalidades
	public void anadirTarjeta(Tarjeta nueva) {
		this.listaTarjetas.add(nueva);
	}
	
	public void quitarTarjeta(Tarjeta tarjeta) {
		this.listaTarjetas.remove(tarjeta);
	}
	
	public void renombrar(String nombre) {
		this.nombreLista = nombre;
	}
	
	public void hacerEspecial() {
		this.especial = true;
	}
	
	public void configurarLimite(Integer limite) {
		if(limite != null || limite <= 0) {
			throw new IllegalArgumentException("El límite debe ser un entero positivo");
		}
		this.limite = limite;
	}
	
	public void configurarPrerrequisitos(List<ListaId> prerrequisitos) {
		if(prerrequisitos == null) {
			this.prerrequisitos = new ArrayList<>();
		}
		this.prerrequisitos = prerrequisitos;
	}
}
