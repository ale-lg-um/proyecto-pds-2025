package es.um.pds.tarjetas.domain.model.tarjeta;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.common.exceptions.TareaInvalidaException;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Etiqueta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;

class ContenidoTarjetaTest {

    @Test
    void tarea_validaEInvalida() {
        Tarea tarea = Tarea.of("Descripción");

        assertEquals("Descripción", tarea.getDescripcion());
        assertEquals(TipoContenidoTarjeta.TAREA, tarea.getTipo());
        assertEquals("Tarea: Descripción", tarea.toString());

        assertThrows(TareaInvalidaException.class, () -> Tarea.of(null));
        assertThrows(TareaInvalidaException.class, () -> Tarea.of(" "));
    }

    @Test
    void itemChecklist_validoEstadosEInvalidos() {
        ItemChecklist item = ItemChecklist.of("Item");

        assertEquals("Item", item.getDescripcion());
        assertFalse(item.isCompletado());
        assertEquals("Item (pendiente)", item.toString());

        item.marcarComoCompletado();

        assertTrue(item.isCompletado());
        assertEquals("Item (completado)", item.toString());

        item.marcarComoPendiente();

        assertFalse(item.isCompletado());

        ItemChecklist reconstruido = ItemChecklist.of("Reconstruido", true);

        assertTrue(reconstruido.isCompletado());

        assertThrows(IllegalArgumentException.class, () -> ItemChecklist.of(null));
        assertThrows(IllegalArgumentException.class, () -> ItemChecklist.of(" "));
        assertThrows(IllegalArgumentException.class, () -> ItemChecklist.of(null, true));
        assertThrows(IllegalArgumentException.class, () -> ItemChecklist.of(" ", true));
    }

    @Test
    void checklist_validoEInvalidos() {
        ItemChecklist uno = ItemChecklist.of("Uno");
        ItemChecklist dos = ItemChecklist.of("Dos", true);

        Checklist checklist = Checklist.of(List.of(uno, dos));

        assertEquals(TipoContenidoTarjeta.CHECKLIST, checklist.getTipo());
        assertFalse(checklist.todosCompletados());
        assertTrue(checklist.toString().contains("Uno"));
        assertTrue(checklist.toString().contains("Dos"));

        assertThrows(IllegalArgumentException.class, () -> Checklist.of(null));
        assertThrows(IllegalArgumentException.class, () -> Checklist.of(List.of()));
        assertThrows(UnsupportedOperationException.class, () -> checklist.getItems().add(ItemChecklist.of("Tres")));
    }

    @Test
    void checklist_agregarEliminar() {
        Checklist checklist = Checklist.of(List.of(ItemChecklist.of("Uno", true)));
        ItemChecklist dos = ItemChecklist.of("Dos", true);

        checklist.agregarItem(dos);

        assertEquals(2, checklist.getItems().size());
        assertTrue(checklist.todosCompletados());

        checklist.eliminarItem(dos);

        assertEquals(1, checklist.getItems().size());

        assertThrows(IllegalArgumentException.class, () -> checklist.agregarItem(null));
        assertThrows(IllegalArgumentException.class, () -> checklist.eliminarItem(null));
    }

    @Test
    void etiqueta_validaEInvalida() {
        Etiqueta etiqueta = new Etiqueta("bug", "rojo");

        assertEquals("bug", etiqueta.nombre());
        assertEquals("rojo", etiqueta.color());

        assertThrows(IllegalArgumentException.class, () -> new Etiqueta(null, "rojo"));
        assertThrows(IllegalArgumentException.class, () -> new Etiqueta(" ", "rojo"));
        assertThrows(IllegalArgumentException.class, () -> new Etiqueta("bug", null));
        assertThrows(IllegalArgumentException.class, () -> new Etiqueta("bug", " "));
    }
}