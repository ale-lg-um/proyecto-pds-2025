package es.um.pds.tarjetas.domain.model.tablero;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.common.exceptions.TableroInvalidoException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.EstadoBloqueo;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

class TableroTest {

    private Tablero tableroBase() {
        return Tablero.of(TableroId.of("tablero"), "Proyecto", "token", UsuarioId.of("a@um.es"));
    }

    @Test
    void crearTablero_valido() {
        Tablero tablero = tableroBase();

        assertEquals("tablero", tablero.getIdentificador().getId());
        assertEquals("Proyecto", tablero.getNombre());
        assertEquals("token", tablero.getTokenURL());
        assertEquals(UsuarioId.of("a@um.es"), tablero.getCreador());
        assertTrue(tablero.getListas().isEmpty());
        assertNull(tablero.getListaEspecial());
        assertFalse(tablero.isBloqueado());
    }

    @Test
    void crearTablero_invalidos_fallan() {
        UsuarioId usuario = UsuarioId.of("a@um.es");

        assertThrows(TableroInvalidoException.class, () -> Tablero.of(null, "T", "token", usuario));
        assertThrows(TableroInvalidoException.class, () -> Tablero.of(TableroId.of("t"), null, "token", usuario));
        assertThrows(TableroInvalidoException.class, () -> Tablero.of(TableroId.of("t"), " ", "token", usuario));
        assertThrows(TableroInvalidoException.class, () -> Tablero.of(TableroId.of("t"), "T", null, usuario));
        assertThrows(TableroInvalidoException.class, () -> Tablero.of(TableroId.of("t"), "T", " ", usuario));
        assertThrows(TableroInvalidoException.class, () -> Tablero.of(TableroId.of("t"), "T", "token", null));
    }

    @Test
    void reconstruirTablero_validoEInvalidos() {
        TableroId tableroId = TableroId.of("tablero");
        UsuarioId usuario = UsuarioId.of("a@um.es");
        ListaId l1 = ListaId.of("l1");
        ListaId l2 = ListaId.of("l2");

        Tablero tablero = Tablero.reconstruir(tableroId, "T", "token", usuario, Set.of(l1, l2), l2, null);

        assertEquals(l2, tablero.getListaEspecial());

        assertThrows(TableroInvalidoException.class,
                () -> Tablero.reconstruir(tableroId, "T", "token", usuario, null, null, null));

        Set<ListaId> listasConNull = new HashSet<>();
        listasConNull.add(null);

        assertThrows(TableroInvalidoException.class,
                () -> Tablero.reconstruir(tableroId, "T", "token", usuario, listasConNull, null, null));

        assertThrows(TableroInvalidoException.class,
                () -> Tablero.reconstruir(tableroId, "T", "token", usuario, Set.of(l1), l2, null));
    }

    @Test
    void anadirYEliminarLista() {
        Tablero tablero = tableroBase();
        ListaId lista = ListaId.of("lista");

        tablero.anadirLista(lista);

        assertTrue(tablero.getListas().contains(lista));

        assertThrows(IllegalArgumentException.class, () -> tablero.anadirLista(null));
        assertThrows(ListaInvalidaException.class, () -> tablero.anadirLista(lista));

        tablero.eliminarLista(lista);

        assertFalse(tablero.getListas().contains(lista));

        assertThrows(IllegalArgumentException.class, () -> tablero.eliminarLista(null));
        assertThrows(IllegalArgumentException.class, () -> tablero.eliminarLista(lista));
    }

    @Test
    void definirListaEspecial_ramas() {
        Tablero tablero = tableroBase();
        ListaId especial = ListaId.of("especial");

        tablero.definirListaEspecial(especial);

        assertEquals(especial, tablero.getListaEspecial());

        assertThrows(IllegalStateException.class, () -> tablero.definirListaEspecial(especial));
        assertThrows(IllegalStateException.class, () -> tablero.definirListaEspecial(ListaId.of("otra")));
    }

    @Test
    void eliminarListaEspecial_limpiaEspecial() {
        Tablero tablero = tableroBase();
        ListaId especial = ListaId.of("especial");

        tablero.anadirLista(especial);
        tablero.definirListaEspecial(especial);
        tablero.eliminarLista(especial);

        assertNull(tablero.getListaEspecial());
    }

    @Test
    void bloquearYDesbloquear() {
        Tablero tablero = tableroBase();
        EstadoBloqueo bloqueo = new EstadoBloqueo(
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(5),
                "Motivo");

        tablero.bloquear(bloqueo);

        assertTrue(tablero.isBloqueado());

        assertThrows(IllegalStateException.class, () -> tablero.bloquear(bloqueo));

        tablero.desbloquear();

        assertFalse(tablero.isBloqueado());

        assertThrows(IllegalStateException.class, tablero::desbloquear);
        assertThrows(IllegalArgumentException.class, () -> tablero.bloquear(null));
    }

    @Test
    void bloqueoExpirado_seLimpia() {
        Tablero tablero = tableroBase();

        tablero.bloquear(new EstadoBloqueo(
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(1),
                "Expirado"));

        assertFalse(tablero.isBloqueado());
        assertNull(tablero.getEstadoBloqueo());
    }

    @Test
    void renombrarTablero() {
        Tablero tablero = tableroBase();

        tablero.renombrar("Nuevo");

        assertEquals("Nuevo", tablero.getNombre());

        assertThrows(IllegalArgumentException.class, () -> tablero.renombrar(null));
        assertThrows(IllegalArgumentException.class, () -> tablero.renombrar(" "));
    }

    @Test
    void estadoBloqueo_ramas() {
        LocalDateTime desde = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime hasta = desde.plusHours(1);

        EstadoBloqueo bloqueo = new EstadoBloqueo(desde, hasta, "Motivo");

        assertFalse(bloqueo.estaActivoEn(desde.minusSeconds(1)));
        assertTrue(bloqueo.estaActivoEn(desde));
        assertTrue(bloqueo.estaActivoEn(hasta));
        assertFalse(bloqueo.estaActivoEn(hasta.plusSeconds(1)));

        assertThrows(IllegalArgumentException.class, () -> bloqueo.estaActivoEn(null));
        assertThrows(IllegalArgumentException.class, () -> new EstadoBloqueo(hasta, desde, "Mal"));

        EstadoBloqueo indefinido = new EstadoBloqueo(desde, null, "Indefinido");

        assertTrue(indefinido.estaActivoEn(desde.plusYears(1)));

        EstadoBloqueo desdeNull = new EstadoBloqueo(null, null, "Sin desde");

        assertNotNull(desdeNull.getDesde());
        assertNull(desdeNull.getHasta());
    }

    @Test
    void equalsYHashCodePorId() {
        Tablero a = Tablero.of(TableroId.of("t"), "A", "token1", UsuarioId.of("a@um.es"));
        Tablero b = Tablero.of(TableroId.of("t"), "B", "token2", UsuarioId.of("b@um.es"));
        Tablero c = Tablero.of(TableroId.of("otro"), "C", "token3", UsuarioId.of("c@um.es"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, "t");
    }
}