package es.um.pds.tarjetas.adapters.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import es.um.pds.tarjetas.adapters.jpa.embeddable.EtiquetaEmbeddable;
import es.um.pds.tarjetas.adapters.jpa.embeddable.ItemChecklistEmbeddable;
import es.um.pds.tarjetas.adapters.jpa.entity.TarjetaEntity;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Checklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Etiqueta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ItemChecklist;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarea;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;

/**
 * Mapper entre el modelo de dominio Tarjeta y su representación JPA
 */
public class TarjetaMapperJPA {

	private static final String TIPO_TAREA = "TAREA";
	private static final String TIPO_CHECKLIST = "CHECKLIST";

	// Constructor vacío, solo queremos los métodos, clase no instanciable
	private TarjetaMapperJPA() {
	}

	public static TarjetaEntity toEntity(Tarjeta domain) {
		if (domain == null) {
			return null;
		}

		String tipoContenido;
		String tareaDescripcion = null;
		List<ItemChecklistEmbeddable> itemsChecklist = List.of();

		ContenidoTarjeta contenido = domain.getContenido();

		switch (contenido) {
		case Tarea tarea -> {
			tipoContenido = TIPO_TAREA;
			tareaDescripcion = tarea.getDescripcion();
		}
		case Checklist checklist -> {
			tipoContenido = TIPO_CHECKLIST;
			itemsChecklist = checklist.getItems().stream().map(TarjetaMapperJPA::toEmbeddableItemChecklist).toList();
		}
		default -> throw new IllegalStateException("Tipo de contenido de tarjeta no soportado");
		}

		List<EtiquetaEmbeddable> etiquetas = domain.getEtiquetas().stream().map(TarjetaMapperJPA::toEmbeddableEtiqueta)
				.toList();

		Set<String> listasVisitadas = domain.getListasVisitadas().stream().map(ListaId::getId)
				.collect(Collectors.toSet());

		return new TarjetaEntity(domain.getIdentificador().getId(), domain.getTitulo(), domain.getFechaCreacion(),
				domain.getListaActual().getId(), domain.getTablero() != null ? domain.getTablero().getId() : null,
				tipoContenido, tareaDescripcion, domain.isCompletada(), etiquetas, listasVisitadas, itemsChecklist);
	}

	public static Tarjeta toDomain(TarjetaEntity entity) {
		if (entity == null) {
			return null;
		}

		ContenidoTarjeta contenido = toDomainContenido(entity);

		List<Etiqueta> etiquetas = entity.getEtiquetas().stream().map(TarjetaMapperJPA::toDomainEtiqueta).toList();

		Set<ListaId> listasVisitadas = entity.getListasVisitadas().stream().map(ListaId::of)
				.collect(Collectors.toSet());

		return Tarjeta.reconstruir(TarjetaId.of(entity.getId()), entity.getTitulo(), entity.getFechaCreacion(),
				ListaId.of(entity.getListaActualId()),
				entity.getTableroId() != null ? TableroId.of(entity.getTableroId()) : null, contenido, etiquetas,
				listasVisitadas, entity.isCompletada());
	}

	// Métodos auxiliares

	private static ContenidoTarjeta toDomainContenido(TarjetaEntity entity) {
		if (TIPO_TAREA.equals(entity.getTipoContenido())) {
			return new Tarea(entity.getTareaDescripcion());
		}

		if (TIPO_CHECKLIST.equals(entity.getTipoContenido())) {
			List<ItemChecklist> items = entity.getItemsChecklist().stream().map(TarjetaMapperJPA::toDomainItemChecklist)
					.toList();

			return Checklist.of(items);
		}

		throw new IllegalStateException("Tipo de contenido no soportado: " + entity.getTipoContenido());
	}

	private static EtiquetaEmbeddable toEmbeddableEtiqueta(Etiqueta etiqueta) {
		return new EtiquetaEmbeddable(etiqueta.nombre(), etiqueta.color());
	}

	private static Etiqueta toDomainEtiqueta(EtiquetaEmbeddable embeddable) {
		return new Etiqueta(embeddable.getNombre(), embeddable.getColor());
	}

	private static ItemChecklistEmbeddable toEmbeddableItemChecklist(ItemChecklist item) {
		return new ItemChecklistEmbeddable(item.getDescripcion(), item.isCompletado());
	}

	private static ItemChecklist toDomainItemChecklist(ItemChecklistEmbeddable embeddable) {
		ItemChecklist item = ItemChecklist.of(embeddable.getDescripcion());

		if (embeddable.isCompletado()) {
			item.marcarComoCompletado();
		}

		return item;
	}
}