package es.um.pds.tarjetas.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.application.usecases.ServicioSesionImpl;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioSesiones;

class ServicioSesionImplTest {

    private RepositorioSesiones repoSesiones;
    private ServicioSesionImpl servicio;

    @BeforeEach
    void setUp() {
        repoSesiones = mock(RepositorioSesiones.class);
        servicio = new ServicioSesionImpl(repoSesiones);
    }

    @Test
    void validarYRenovarToken_tokenAusente_falla() {
        assertThrows(IllegalArgumentException.class, () -> servicio.validarYRenovarToken(null));
        assertThrows(IllegalArgumentException.class, () -> servicio.validarYRenovarToken(" "));
    }

    @Test
    void validarYRenovarToken_tokenInvalido_falla() {
        when(repoSesiones.buscarUsuarioPorTokenVigente("token")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> servicio.validarYRenovarToken("token"));

        verify(repoSesiones, never()).extenderExpiracion(anyString(), any(Instant.class));
    }

    @Test
    void validarYRenovarToken_tokenValido_extiendeSesion() {
        UsuarioId usuarioId = UsuarioId.of("a@um.es");

        when(repoSesiones.buscarUsuarioPorTokenVigente("token")).thenReturn(Optional.of(usuarioId));

        UsuarioId resultado = servicio.validarYRenovarToken("token");

        assertEquals(usuarioId, resultado);
        verify(repoSesiones).extenderExpiracion(eq("token"), any(Instant.class));
    }
}
