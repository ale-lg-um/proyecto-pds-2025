package es.um.pds.tarjetas.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.common.exceptions.EntryHistorialInvalidaException;
import es.um.pds.tarjetas.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.common.exceptions.PlantillaInvalidaException;
import es.um.pds.tarjetas.common.exceptions.TableroInvalidoException;
import es.um.pds.tarjetas.common.exceptions.TarjetaInvalidaException;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.plantilla.id.PlantillaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;

class IdsDominioTest {

    @Test
    void tableroId_clasesValidasEInvalidas() {
        assertThrows(TableroInvalidoException.class, () -> TableroId.of(null));

        TableroId id = TableroId.of("tablero-1");

        assertEquals("tablero-1", id.getId());
        assertEquals(TableroId.of("tablero-1"), id);
        assertEquals(TableroId.of("tablero-1").hashCode(), id.hashCode());
        assertNotEquals(TableroId.of("tablero-2"), id);
        assertNotEquals("tablero-1", id);
        assertNotNull(TableroId.of().getId());
    }

    @Test
    void listaId_clasesValidasEInvalidas() {
        assertThrows(ListaInvalidaException.class, () -> ListaId.of(null));

        ListaId id = ListaId.of("lista-1");

        assertEquals("lista-1", id.getId());
        assertEquals(ListaId.of("lista-1"), id);
        assertEquals(ListaId.of("lista-1").hashCode(), id.hashCode());
        assertNotEquals(ListaId.of("lista-2"), id);
        assertNotEquals("lista-1", id);
        assertNotNull(ListaId.of().getId());
    }

    @Test
    void tarjetaId_clasesValidasEInvalidas() {
        assertThrows(TarjetaInvalidaException.class, () -> TarjetaId.of(null));

        TarjetaId id = TarjetaId.of("tarjeta-1");

        assertEquals("tarjeta-1", id.getId());
        assertEquals(TarjetaId.of("tarjeta-1"), id);
        assertEquals(TarjetaId.of("tarjeta-1").hashCode(), id.hashCode());
        assertNotEquals(TarjetaId.of("tarjeta-2"), id);
        assertNotEquals("tarjeta-1", id);
        assertNotNull(TarjetaId.of().getId());
    }

    @Test
    void plantillaId_clasesValidasEInvalidas() {
        assertThrows(PlantillaInvalidaException.class, () -> PlantillaId.of(null));

        PlantillaId id = PlantillaId.of("plantilla-1");

        assertEquals("plantilla-1", id.getId());
        assertEquals(PlantillaId.of("plantilla-1"), id);
        assertEquals(PlantillaId.of("plantilla-1").hashCode(), id.hashCode());
        assertNotEquals(PlantillaId.of("plantilla-2"), id);
        assertNotEquals("plantilla-1", id);
        assertNotNull(PlantillaId.of().getId());
    }

    @Test
    void entryHistorialId_clasesValidasEInvalidas() {
        assertThrows(EntryHistorialInvalidaException.class, () -> EntryHistorialId.of(null));

        EntryHistorialId id = EntryHistorialId.of("entry-1");

        assertEquals("entry-1", id.getId());
        assertEquals(EntryHistorialId.of("entry-1"), id);
        assertEquals(EntryHistorialId.of("entry-1").hashCode(), id.hashCode());
        assertNotEquals(EntryHistorialId.of("entry-2"), id);
        assertNotEquals("entry-1", id);
        assertNotNull(EntryHistorialId.of().getId());
    }
}