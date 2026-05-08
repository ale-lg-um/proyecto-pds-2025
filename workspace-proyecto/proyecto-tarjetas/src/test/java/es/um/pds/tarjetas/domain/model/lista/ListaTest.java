package es.um.pds.tarjetas.domain.model.lista;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;

class ListaTest {

    @Test
    void crearLista_valida() {
        Lista lista = Lista.of(ListaId.of("l1"), "TODO");

        assertEquals("l1", lista.getIdentificador().getId());
        assertEquals("TODO", lista.getNombreLista());
        assertTrue(lista.getListaTarjetas().isEmpty());
        assertFalse(lista.isEspecial());
        assertNull(lista.getLimite());
        assertTrue(lista.getPrerrequisitos().isEmpty());
    }

    @Test
    void crearLista_datosInvalidos_fallan() {
        assertThrows(ListaInvalidaException.class,
                () -> Lista.of(null, "TODO"));

        assertThrows(ListaInvalidaException.class,
                () -> Lista.of(ListaId.of("l1"), null));

        assertThrows(ListaInvalidaException.class,
                () -> Lista.of(ListaId.of("l1"), " "));
    }

    @Test
    void reconstruirLista_valida() {
        ListaId id = ListaId.of("l1");
        TarjetaId tarjeta = TarjetaId.of("t1");
        ListaId prerrequisito = ListaId.of("pre");
        TableroId tablero = TableroId.of("tablero");

        Lista lista = Lista.reconstruir(
                id,
                "TODO",
                List.of(tarjeta),
                false,
                5,
                Set.of(prerrequisito),
                tablero
        );

        assertEquals(id, lista.getIdentificador());
        assertEquals("TODO", lista.getNombreLista());
        assertEquals(List.of(tarjeta), lista.getListaTarjetas());
        assertEquals(5, lista.getLimite());
        assertTrue(lista.getPrerrequisitos().contains(prerrequisito));
        assertEquals(tablero, lista.getTablero());
        assertFalse(lista.isEspecial());
    }

    @Test
    void reconstruirLista_clasesInvalidas_fallan() {
        ListaId id = ListaId.of("l1");
        TarjetaId tarjeta = TarjetaId.of("t1");

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        null,
                        false,
                        null,
                        Set.of(),
                        null
                ));

        List<TarjetaId> tarjetasConNull = new ArrayList<>();
        tarjetasConNull.add(null);

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        tarjetasConNull,
                        false,
                        null,
                        Set.of(),
                        null
                ));

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        List.of(tarjeta),
                        false,
                        null,
                        null,
                        null
                ));

        Set<ListaId> prerrequisitosConNull = new HashSet<>();
        prerrequisitosConNull.add(null);

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        List.of(tarjeta),
                        false,
                        null,
                        prerrequisitosConNull,
                        null
                ));

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        List.of(tarjeta),
                        true,
                        3,
                        Set.of(),
                        null
                ));

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        List.of(tarjeta),
                        false,
                        0,
                        Set.of(),
                        null
                ));

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        List.of(tarjeta),
                        false,
                        -1,
                        Set.of(),
                        null
                ));

        assertThrows(ListaInvalidaException.class,
                () -> Lista.reconstruir(
                        id,
                        "TODO",
                        List.of(tarjeta, TarjetaId.of("t2")),
                        false,
                        1,
                        Set.of(),
                        null
                ));
    }

    @Test
    void anadirEliminarYMoverTarjetas() {
        Lista lista = Lista.of(ListaId.of("l1"), "TODO");

        TarjetaId t1 = TarjetaId.of("t1");
        TarjetaId t2 = TarjetaId.of("t2");

        lista.anadirTarjeta(t1);
        lista.anadirTarjeta(t2);

        assertEquals(List.of(t1, t2), lista.getListaTarjetas());

        lista.moverTarjeta(t2, 0);

        assertEquals(List.of(t2, t1), lista.getListaTarjetas());

        lista.eliminarTarjeta(t1);

        assertEquals(List.of(t2), lista.getListaTarjetas());
    }

    @Test
    void operacionesTarjeta_invalidas_fallan() {
        Lista lista = Lista.of(ListaId.of("l1"), "TODO");
        TarjetaId t1 = TarjetaId.of("t1");

        assertThrows(IllegalArgumentException.class,
                () -> lista.anadirTarjeta(null));

        lista.anadirTarjeta(t1);

        assertThrows(IllegalArgumentException.class,
                () -> lista.anadirTarjeta(t1));

        assertThrows(IllegalArgumentException.class,
                () -> lista.eliminarTarjeta(null));

        assertThrows(IllegalArgumentException.class,
                () -> lista.eliminarTarjeta(TarjetaId.of("otra")));

        assertThrows(IllegalArgumentException.class,
                () -> lista.moverTarjeta(null, 0));

        assertThrows(IllegalArgumentException.class,
                () -> lista.moverTarjeta(TarjetaId.of("otra"), 0));

        assertThrows(IllegalArgumentException.class,
                () -> lista.moverTarjeta(t1, -1));

        assertThrows(IllegalArgumentException.class,
                () -> lista.moverTarjeta(t1, 1));
    }

    @Test
    void configurarLimite_casosValidosEInvalidos() {
        Lista lista = Lista.of(ListaId.of("l1"), "TODO");

        assertThrows(IllegalArgumentException.class,
                () -> lista.configurarLimite(0));

        assertThrows(IllegalArgumentException.class,
                () -> lista.configurarLimite(-1));

        lista.configurarLimite(2);

        assertEquals(2, lista.getLimite());

        lista.anadirTarjeta(TarjetaId.of("t1"));
        lista.anadirTarjeta(TarjetaId.of("t2"));

        assertThrows(IllegalArgumentException.class,
                () -> lista.anadirTarjeta(TarjetaId.of("t3")));

        assertThrows(IllegalStateException.class,
                () -> lista.configurarLimite(1));

        lista.configurarLimite(null);

        assertNull(lista.getLimite());
    }

    @Test
    void listaEspecial_noPermiteLimite() {
        Lista lista = Lista.of(ListaId.of("l1"), "DONE");

        lista.hacerEspecial();

        assertTrue(lista.isEspecial());

        assertThrows(IllegalStateException.class,
                () -> lista.configurarLimite(3));

        lista.quitarEspecial();

        assertFalse(lista.isEspecial());

        lista.configurarLimite(3);

        assertEquals(3, lista.getLimite());
    }

    @Test
    void renombrarAsignarTableroYPrerrequisitos() {
        Lista lista = Lista.of(ListaId.of("l1"), "Antigua");

        TableroId tablero = TableroId.of("tablero");
        ListaId prerrequisito = ListaId.of("pre");

        lista.renombrar("Nueva");
        lista.asignarATablero(tablero);
        lista.configurarPrerrequisitos(Set.of(prerrequisito));

        assertEquals("Nueva", lista.getNombreLista());
        assertEquals(tablero, lista.getTablero());
        assertEquals(Set.of(prerrequisito), lista.getPrerrequisitos());

        lista.configurarPrerrequisitos(null);

        assertTrue(lista.getPrerrequisitos().isEmpty());

        assertThrows(IllegalArgumentException.class,
                () -> lista.renombrar(null));

        assertThrows(IllegalArgumentException.class,
                () -> lista.renombrar(" "));

        assertThrows(IllegalArgumentException.class,
                () -> lista.asignarATablero(null));
    }

    @Test
    void coleccionesSonInmutables() {
        Lista lista = Lista.of(ListaId.of("l1"), "TODO");

        lista.anadirTarjeta(TarjetaId.of("t1"));
        lista.configurarPrerrequisitos(Set.of(ListaId.of("pre")));

        assertThrows(UnsupportedOperationException.class,
                () -> lista.getListaTarjetas().add(TarjetaId.of("t2")));

        assertThrows(UnsupportedOperationException.class,
                () -> lista.getPrerrequisitos().add(ListaId.of("otra")));
    }

    @Test
    void equalsYHashCodePorId() {
        Lista a = Lista.of(ListaId.of("l1"), "A");
        Lista b = Lista.of(ListaId.of("l1"), "B");
        Lista c = Lista.of(ListaId.of("l2"), "C");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, "l1");
    }
}