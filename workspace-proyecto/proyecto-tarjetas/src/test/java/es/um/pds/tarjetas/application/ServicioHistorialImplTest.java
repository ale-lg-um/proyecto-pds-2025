package es.um.pds.tarjetas.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.application.usecases.ServicioHistorialImpl;
import es.um.pds.tarjetas.domain.ports.output.RepositorioEntryHistorial;

class ServicioHistorialImplTest {

    private ServicioHistorialImpl servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioHistorialImpl(mock(RepositorioEntryHistorial.class));
    }

    @Test
    void consultarPorTablero_paginaNegativa_falla() {
        assertThrows(IllegalArgumentException.class, () -> servicio.consultarPorTablero("tablero", -1, 10));
    }

    @Test
    void consultarPorTablero_tamanoNoPositivo_falla() {
        assertThrows(IllegalArgumentException.class, () -> servicio.consultarPorTablero("tablero", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> servicio.consultarPorTablero("tablero", 0, -1));
    }

    @Test
    void consultarPorTablero_tamanoMayorQueMaximo_falla() {
        assertThrows(IllegalArgumentException.class, () -> servicio.consultarPorTablero("tablero", 0, 21));
    }
}