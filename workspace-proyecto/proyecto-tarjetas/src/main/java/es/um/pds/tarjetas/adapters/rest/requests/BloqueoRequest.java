package es.um.pds.tarjetas.adapters.rest.requests;

import java.time.LocalDateTime;

public record BloqueoRequest(LocalDateTime inicio, LocalDateTime fin, String motivo) {

}
