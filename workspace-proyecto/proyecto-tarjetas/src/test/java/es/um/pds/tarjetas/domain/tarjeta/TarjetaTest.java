package es.um.pds.tarjetas.domain.tarjeta;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;

class TarjetaTest {

    @Test
    @DisplayName("Una tarjeta con checklist se completa automáticamente al completar todos sus ítems")
    void marcarItemsChecklist_actualizaEstadoDeCompletitud() {
        Tarjeta tarjeta = Tarjeta.of(
                TarjetaId.of(),
                "Checklist",
                ListaId.of(),
                Checklist.of(List.of(ItemChecklist.of("Primero"), ItemChecklist.of("Segundo"))));

        assertFalse(tarjeta.isCompletada());

        tarjeta.marcarItemChecklistComoCompletado(0);
        assertFalse(tarjeta.isCompletada());

        tarjeta.marcarItemChecklistComoCompletado(1);
        assertTrue(tarjeta.isCompletada());

        tarjeta.marcarItemChecklistComoPendiente(1);
        assertFalse(tarjeta.isCompletada());
    }
}
