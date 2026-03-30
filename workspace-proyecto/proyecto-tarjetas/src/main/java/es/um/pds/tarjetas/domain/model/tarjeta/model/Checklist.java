package es.um.pds.tarjetas.domain.model.tarjeta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Checklist extends ContenidoTarjeta {

    // Atributos
    private final List<ItemChecklist> items;

    // Constructor
    private Checklist(List<ItemChecklist> items) {
        this.items = new ArrayList<>(items);
    }

    // Método factoría
    public static Checklist of(List<ItemChecklist> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El checklist debe tener al menos un ítem");
        }
        return new Checklist(items);
    }

    // Getter
    public List<ItemChecklist> getItems() {
        return Collections.unmodifiableList(items);
    }

    // Comportamiento de dominio

    public void agregarItem(ItemChecklist item) {
        if (item == null) {
            throw new IllegalArgumentException("El ítem no puede ser nulo");
        }
        items.add(item);
    }

    public void eliminarItem(ItemChecklist item) {
        if (item == null) {
            throw new IllegalArgumentException("El ítem no puede ser nulo");
        }
        items.remove(item);
    }

    /**
     * Un checklist se considera completo si todos sus ítems están completados
     * Esto es lógica interna del contenido, NO de la tarjeta
     */
    public boolean todosCompletados() {
        return items.stream().allMatch(ItemChecklist::isCompletado);
    }

	@Override
	public TipoContenidoTarjeta getTipo() {
		return TipoContenidoTarjeta.CHECKLIST;
	}
}