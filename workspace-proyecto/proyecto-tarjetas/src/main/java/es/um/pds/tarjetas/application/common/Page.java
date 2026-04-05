package es.um.pds.tarjetas.application.common;

import java.util.List;

/*
 * Clase utilizada para mostrar contenido paginado en el historial o filtrado
 * de tarjetas por etiquetas
 */
public class Page<T> {

	private final List<T> contenido;
	private final int pagina;
	private final int tamano;
	private final int totalPaginas;
	private final long totalElementos;
	
	public Page(List<T> contenido, int pagina, int tamano, int totalPaginas, long totalElementos) {
	    this.contenido = contenido;
	    this.pagina = pagina;
	    this.tamano = tamano;
	    this.totalPaginas = totalPaginas;
	    this.totalElementos = totalElementos;
	}
	
	public List<T> getContenido() {
	    return contenido;
	}
	
	public int getPagina() {
	    return pagina;
	}
	
	public int getTamano() {
	    return tamano;
	}
	
	public int getTotalPaginas() {
	    return totalPaginas;
	}
	
	public long getTotalElementos() {
	    return totalElementos;
	}
}