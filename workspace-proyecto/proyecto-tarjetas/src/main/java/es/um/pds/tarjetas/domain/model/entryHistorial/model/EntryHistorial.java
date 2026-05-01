package es.um.pds.tarjetas.domain.model.entryHistorial.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import es.um.pds.tarjetas.common.exceptions.EntryHistorialInvalidaException;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.PrerrequisitoInfo;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.ContenidoTarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Etiqueta;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;

public class EntryHistorial {

	// Atributos
	private final EntryHistorialId identificador;
	private final TableroId tableroId;
	private final TipoEntryHistorial tipo;
	private final UsuarioId usuario;
	private final LocalDateTime timestamp;
	private final String detalles;

	// Constructor
	private EntryHistorial(EntryHistorialId identificador, TableroId tableroId, TipoEntryHistorial tipo,
			UsuarioId usuario, LocalDateTime timestamp, String detalles) {
		this.identificador = identificador;
		this.tableroId = tableroId;
		this.tipo = tipo;
		this.usuario = usuario;
		this.timestamp = timestamp;
		this.detalles = detalles;
	}

	// Método factoría GENERAL
	public static EntryHistorial of(EntryHistorialId identificador, TableroId tableroId, TipoEntryHistorial tipo,
			UsuarioId usuario, LocalDateTime timestamp, String detalles) {

		if (identificador == null) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe tener identificador");
		}

		if (tableroId == null) {
			throw new EntryHistorialInvalidaException(
					"La entry del historial del historial debe estar asociado a un tablero");
		}

		if (tipo == null) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe tener un tipo");
		}

		if (usuario == null) {
			throw new EntryHistorialInvalidaException(
					"La entry del historial del historial debe registrar un usuario que haya hecho la acción");
		}

		if (timestamp == null) {
			timestamp = LocalDateTime.now();
		}

		if (detalles == null || detalles.isBlank()) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe tener detalles");
		}

		return new EntryHistorial(identificador, tableroId, tipo, usuario, timestamp, detalles);
	}

	// Getters
	public EntryHistorialId getIdentificador() {
		return identificador;
	}

	public TableroId getTableroId() {
		return tableroId;
	}

	public TipoEntryHistorial getTipo() {
		return tipo;
	}

	public UsuarioId getUsuario() {
		return usuario;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getDetalles() {
		return detalles;
	}

	// Overrides
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EntryHistorial other))
			return false;
		return Objects.equals(this.identificador, other.identificador);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identificador);
	}

	// Definimos métodos factoría por cada tipo para ser reutilizados

	// ---------- TABLEROS ----------
	// TABLERO CREADO
	public static EntryHistorial tableroCreado(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, String nombreTablero) {

		return of(id, tableroId, TipoEntryHistorial.TABLERO_CREADO, usuario, timestamp,
				"nombreTablero=" + nombreTablero);
	}

	// TABLERO CREADO DESDE PLANTILLA
	public static EntryHistorial tableroCreadoDesdePlantilla(EntryHistorialId id, TableroId tableroId,
			UsuarioId usuario, LocalDateTime timestamp, String nombreTablero, String nombrePlantilla) {

		String detalles = "nombreTablero=" + nombreTablero + ", modo=plantilla" + ", plantillaNombre="
				+ nombrePlantilla;

		return of(id, tableroId,
				// Usa la misma que tablero creado, aunque podría hacerse una concreta para este caso
				TipoEntryHistorial.TABLERO_CREADO, usuario, timestamp, detalles);
	}

	// TABLERO EDITADO
	public static EntryHistorial tableroEditado(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, String nombreAnterior, String nombreNuevo) {

		String detalles = "antiguo=" + nombreAnterior + ", nuevo=" + nombreNuevo;

		return of(id, tableroId, TipoEntryHistorial.TABLERO_EDITADO, usuario, timestamp, detalles);
	}

	// TABLERO ELIMINADO
	public static EntryHistorial tableroEliminado(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp) {

		String detalles = "tableroId=" + tableroId.getId();

		return of(id, tableroId, TipoEntryHistorial.TABLERO_ELIMINADO, usuario, timestamp, detalles);
	}

	// TABLERO BLOQUEADO
	public static EntryHistorial tableroBloqueado(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, String descripcion) {

		String detalles = "descripcion=" + descripcion;

		return of(id, tableroId, TipoEntryHistorial.TABLERO_BLOQUEADO, usuario, timestamp, detalles);
	}

	// TABLERO DESBLOQUEADO
	public static EntryHistorial tableroDesbloqueado(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp) {

		return of(id, tableroId, TipoEntryHistorial.TABLERO_DESBLOQUEADO, usuario, timestamp, "Tablero desbloqueado");
	}

	// ---------- LISTAS ----------
	// LISTA CREADA
	public static EntryHistorial listaCreada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, ListaId listaId, String nombreLista) {

		String detalles = "listaId=" + listaId.getId() + ", nombre=" + nombreLista;

		return of(id, tableroId, TipoEntryHistorial.LISTA_CREADA, usuario, timestamp, detalles);
	}

	// LISTA EDITADA
	public static EntryHistorial listaEditada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, ListaId listaId, String nombreAnterior, String nombreNuevo) {

		String detalles = "listaId=" + listaId.getId() + ", antiguo=" + nombreAnterior + ", nuevo=" + nombreNuevo;

		return of(id, tableroId, TipoEntryHistorial.LISTA_EDITADA, usuario, timestamp, detalles);
	}

	// LISTA ELIMINADA
	public static EntryHistorial listaEliminada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, ListaId listaId, String nombreLista) {

		String detalles = "listaId=" + listaId.getId() + ", nombre=" + nombreLista;

		return of(id, tableroId, TipoEntryHistorial.LISTA_ELIMINADA, usuario, timestamp, detalles);
	}

	// LIMITE LISTA CONFIGURADO
	public static EntryHistorial limiteListaConfigurado(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, ListaId listaId, Integer limiteAnterior, Integer limiteNuevo) {

		String detalles = "listaId=" + listaId.getId() + ", limiteAnterior="
				+ (limiteAnterior == null ? "-" : limiteAnterior) + ", limiteNuevo="
				+ (limiteNuevo == null ? "-" : limiteNuevo);

		return of(id, tableroId, TipoEntryHistorial.LIMITE_LISTA_CONFIGURADO, usuario, timestamp, detalles);
	}

	// LISTA ESPECIAL DEFINIDA
	public static EntryHistorial listaEspecialDefinida(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, ListaId listaId, boolean esEspecial) {

		String detalles = "listaId=" + listaId.getId() + ", esEspecial=" + esEspecial;

		return of(id, tableroId, TipoEntryHistorial.LISTA_ESPECIAL_DEFINIDA, usuario, timestamp, detalles);
	}

	// PRERREQUISITOS LISTA CONFIGURADOS
	public static EntryHistorial prerrequisitosListaConfigurados(EntryHistorialId id, TableroId tableroId,
			UsuarioId usuario, LocalDateTime timestamp, ListaId listaId, Set<PrerrequisitoInfo> prerrequisitos) {

		String prerrequisitosStr = prerrequisitos == null || prerrequisitos.isEmpty() ? "-"
				: prerrequisitos.stream().map(p -> p.nombreLista() + "(" + p.listaId().getId() + ")")
						.reduce((a, b) -> a + " | " + b).orElse("-");

		String detalles = "listaId=" + listaId.getId() + ", prerrequisitos=" + prerrequisitosStr;

		return of(id, tableroId, TipoEntryHistorial.PRERREQUISITOS_LISTA_CONFIGURADOS, usuario, timestamp, detalles);
	}

	// ---------- TARJETAS ----------
	// TARJETA CREADA
	public static EntryHistorial tarjetaCreada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, ListaId listaId) {

		String detalles = "tarjetaId=" + tarjetaId.getId() + ", listaId=" + listaId.getId();

		return of(id, tableroId, TipoEntryHistorial.TARJETA_CREADA, usuario, timestamp, detalles);
	}

	// TARJETA EDITADA
	public static EntryHistorial tarjetaEditada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, ContenidoTarjeta contenidoAnterior,
			ContenidoTarjeta contenidoNuevo) {

		String detalles = "tarjetaId=" + tarjetaId.getId() + ", contenidoAnterior=" + contenidoAnterior.toString()
				+ ", contenidoNuevo=" + contenidoNuevo.toString();

		return of(id, tableroId, TipoEntryHistorial.TARJETA_EDITADA, usuario, timestamp, detalles);
	}

	// TARJETA RENOMBRADA
	public static EntryHistorial tarjetaRenombrada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, String nombreAnterior, String nombreNuevo) {

		String detalles = "tarjetaId=" + tarjetaId.getId() + ", nombreAnterior=" + nombreAnterior + ", nombreNuevo="
				+ nombreNuevo;

		return of(id, tableroId, TipoEntryHistorial.TARJETA_RENOMBRADA, usuario, timestamp, detalles);
	}

	// TARJETA ELIMINADA
	public static EntryHistorial tarjetaEliminada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, ListaId listaId, String titulo) {

		String detalles = "tarjetaId=" + tarjetaId.getId() + ", listaId=" + listaId.getId() + ", titulo=" + titulo;

		return of(id, tableroId, TipoEntryHistorial.TARJETA_ELIMINADA, usuario, timestamp, detalles);
	}

	// TARJETA MOVIDA
	public static EntryHistorial tarjetaMovida(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, ListaId fromListId, ListaId toListId) {

		String detalles = "tarjetaId=" + tarjetaId.getId() + ", fromListId=" + fromListId.getId() + ", toListId="
				+ toListId.getId();

		return of(id, tableroId, TipoEntryHistorial.TARJETA_MOVIDA, usuario, timestamp, detalles);
	}

	// TARJETA COMPLETADA
	public static EntryHistorial tarjetaCompletada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, ListaId listaId) {

		String detalles = "tarjetaId=" + tarjetaId.getId() + ", listaId=" + listaId.getId();

		return of(id, tableroId, TipoEntryHistorial.TARJETA_COMPLETADA, usuario, timestamp, detalles);
	}

	// TARJETA ETIQUETADA
	public static EntryHistorial tarjetaEtiquetada(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, Etiqueta etiqueta) {

		String detalles = "tarjetaId=" + tarjetaId.getId() + ", etiqueta=" + etiqueta.nombre() + ", color="
				+ etiqueta.color();

		return of(id, tableroId, TipoEntryHistorial.TARJETA_ETIQUETADA, usuario, timestamp, detalles);
	}

	// ETIQUETA ELIMINADA DE TARJETA
	public static EntryHistorial etiquetaEliminadaDeTarjeta(EntryHistorialId id, TableroId tableroId, UsuarioId usuario,
			LocalDateTime timestamp, TarjetaId tarjetaId, Etiqueta etiqueta) {

		String detalles = "accion=eliminada" + ", tarjetaId=" + tarjetaId.getId() + ", etiquetaNombre="
				+ etiqueta.nombre() + ", etiquetaColor=" + etiqueta.color();

		return of(id, tableroId, TipoEntryHistorial.ETIQUETA_ELIMINADA, usuario, timestamp, detalles);
	}

	// ETIQUETA MODIFICADA EN TARJETA
	public static EntryHistorial etiquetaModificadaEnTarjeta(EntryHistorialId id, TableroId tableroId,
			UsuarioId usuario, LocalDateTime timestamp, TarjetaId tarjetaId, Etiqueta etiquetaAnterior,
			Etiqueta etiquetaNueva) {

		String detalles = "accion=modificada" + ", tarjetaId=" + tarjetaId.getId() + ", etiquetaAnteriorNombre="
				+ etiquetaAnterior.nombre() + ", etiquetaAnteriorColor=" + etiquetaAnterior.color()
				+ ", etiquetaNuevaNombre=" + etiquetaNueva.nombre() + ", etiquetaNuevaColor=" + etiquetaNueva.color();

		return of(id, tableroId, TipoEntryHistorial.ETIQUETA_MODIFICADA, usuario, timestamp, detalles);
	}
}