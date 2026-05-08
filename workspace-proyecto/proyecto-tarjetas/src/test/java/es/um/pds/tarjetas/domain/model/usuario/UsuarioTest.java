package es.um.pds.tarjetas.domain.model.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.common.exceptions.UsuarioInvalidoException;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.model.usuario.model.Usuario;

class UsuarioTest {

    @Test
    void usuarioId_emailValido_seNormaliza() {
        UsuarioId id = UsuarioId.of("  TEST@UM.ES  ");

        assertEquals("test@um.es", id.getCorreo());
    }

    @Test
    void usuarioId_emailInvalido_falla() {
        assertThrows(UsuarioInvalidoException.class, () -> UsuarioId.of(null));
        assertThrows(UsuarioInvalidoException.class, () -> UsuarioId.of(" "));
        assertThrows(UsuarioInvalidoException.class, () -> UsuarioId.of("correo"));
        assertThrows(UsuarioInvalidoException.class, () -> UsuarioId.of("a@b"));
    }

    @Test
    void usuarioId_equalsYHashCode() {
        UsuarioId a = UsuarioId.of("a@um.es");
        UsuarioId b = UsuarioId.of("A@UM.ES");
        UsuarioId c = UsuarioId.of("b@um.es");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, "a@um.es");
    }

    @Test
    void usuario_crearYCambiarNombre() {
        Usuario usuario = Usuario.of(UsuarioId.of("a@um.es"), "Alejandro");

        assertEquals("Alejandro", usuario.getNombre());

        usuario.cambiarNombre("Nuevo");

        assertEquals("Nuevo", usuario.getNombre());
    }

    @Test
    void usuario_datosInvalidos_fallan() {
        assertThrows(UsuarioInvalidoException.class, () -> Usuario.of(null, "Nombre"));
        assertThrows(UsuarioInvalidoException.class, () -> Usuario.of(UsuarioId.of("a@um.es"), null));
        assertThrows(UsuarioInvalidoException.class, () -> Usuario.of(UsuarioId.of("a@um.es"), " "));

        Usuario usuario = Usuario.of(UsuarioId.of("a@um.es"), "Nombre");

        assertThrows(IllegalArgumentException.class, () -> usuario.cambiarNombre(null));
        assertThrows(IllegalArgumentException.class, () -> usuario.cambiarNombre(" "));
    }

    @Test
    void usuario_equalsYHashCodePorIdentificador() {
        Usuario a = Usuario.of(UsuarioId.of("a@um.es"), "A");
        Usuario b = Usuario.of(UsuarioId.of("a@um.es"), "B");
        Usuario c = Usuario.of(UsuarioId.of("b@um.es"), "C");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, "usuario");
    }
}