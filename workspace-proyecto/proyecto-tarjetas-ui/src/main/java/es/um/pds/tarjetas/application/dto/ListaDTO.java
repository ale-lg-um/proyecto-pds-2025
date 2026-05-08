package es.um.pds.tarjetas.application.dto;

import java.util.List;
import java.util.Set;

public record ListaDTO(String id, String nombre, boolean especial, Integer limite, List<String> tarjetaIds, Set<String> prerrequisitoIds) {

}
