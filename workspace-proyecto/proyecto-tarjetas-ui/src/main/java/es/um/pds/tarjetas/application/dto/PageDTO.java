package es.um.pds.tarjetas.application.dto;

import java.util.List;

public record PageDTO<T>(List<T> contenido, int pagina, int tamano, int totalPaginas, long totalElementos) {

}
