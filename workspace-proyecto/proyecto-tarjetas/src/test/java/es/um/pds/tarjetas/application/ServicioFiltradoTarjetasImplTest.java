package es.um.pds.tarjetas.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.application.usecases.ServicioFiltradoTarjetasImpl;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;

class ServicioFiltradoTarjetasImplTest {

    private ServicioFiltradoTarjetasImpl servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioFiltradoTarjetasImpl(mock(RepositorioTarjetas.class));
    }

    @Test
    void filtrarPorEtiquetas_sinEtiquetas_falla() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.filtrarPorEtiquetas("tablero", null, null, 0, 10));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.filtrarPorEtiquetas("tablero", List.of(), null, 0, 10));
    }

    @Test
    void filtrarPorEtiquetas_etiquetasInvalidas_fallan() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.filtrarPorEtiquetas("tablero", List.of("bug", " "), null, 0, 10));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.filtrarPorEtiquetas("tablero", Arrays.asList("bug", null), null, 0, 10));
    }

    @Test
    void filtrarPorEtiquetas_paginacionInvalida_falla() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.filtrarPorEtiquetas("tablero", List.of("bug"), null, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.filtrarPorEtiquetas("tablero", List.of("bug"), null, 0, 0));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.filtrarPorEtiquetas("tablero", List.of("bug"), null, 0, 21));
    }
}