package es.um.pds.tarjetas.application.common;

//TODO Revisar
public class PageRequest {

	private final int page;
	private final int size;

	public PageRequest(int page, int size) {
		if (page < 0) {
			throw new IllegalArgumentException("El número de página no puede ser negativo");
		}
		if (size <= 0) {
			throw new IllegalArgumentException("El tamaño de página debe ser positivo");
		}
		this.page = page;
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public int getSize() {
		return size;
	}
}