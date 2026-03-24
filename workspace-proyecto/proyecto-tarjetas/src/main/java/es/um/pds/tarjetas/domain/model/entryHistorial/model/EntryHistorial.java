package es.um.pds.tarjetas.domain.model.entryHistorial.model;

import java.time.LocalDateTime;
import java.util.Objects;

import es.um.pds.tarjetas.application.common.exceptions.EntryHistorialInvalidaException;
import es.um.pds.tarjetas.domain.model.entryHistorial.id.EntryHistorialId;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
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
	private EntryHistorial(
			EntryHistorialId identificador,
			TableroId tableroId,
			TipoEntryHistorial tipo,
			UsuarioId usuario,
			LocalDateTime timestamp,
			String detalles
	) {
		this.identificador = identificador;
		this.tableroId = tableroId;
		this.tipo = tipo;
		this.usuario = usuario;
		this.timestamp = timestamp;
		this.detalles = detalles;
	}
	
	// Método factoría GENERAL
	public static EntryHistorial of(
			EntryHistorialId identificador,
			TableroId tableroId,
			TipoEntryHistorial tipo,
			UsuarioId usuario,
			LocalDateTime timestamp,
			String detalles
	) throws EntryHistorialInvalidaException {
		
		if (identificador == null) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe tener identificador");
		}
		
		if (tableroId == null) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe estar asociado a un tablero");
		}
		
		if (tipo == null) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe tener un tipo");
		}
		
		if (usuario == null) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe registrar un usuario que haya hecho la acción");
		}
		
		if (timestamp == null) {
			timestamp = LocalDateTime.now();
		}
		
		if (detalles == null || detalles.isBlank()) {
			throw new EntryHistorialInvalidaException("La entry del historial del historial debe tener detalles");
		}
		
		return new EntryHistorial(
				identificador,
				tableroId,
				tipo,
				usuario,
				timestamp,
				detalles
		);
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
		if (this == obj) return true;
		if (!(obj instanceof EntryHistorial other)) return false;
		return Objects.equals(this.identificador, other.identificador);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(identificador);
	}
	
	// Definimos métodos factoría por cada tipo para ser reutilizados
	
	// TABLERO CREADO
	public static EntryHistorial tableroCreado(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp
	) throws EntryHistorialInvalidaException {

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.TABLERO_CREADO,
	            usuario,
	            timestamp,
	            "Tablero creado"
	    );
	}
	
	// TABLERO RENOMBRADO
	public static EntryHistorial tableroRenombrado(
			EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp,
	        String nombreAnterior,
	        String nombreNuevo
	) throws EntryHistorialInvalidaException {

		String detalles = "antiguo=" + nombreAnterior +
	    		", nuevo=" + nombreNuevo;
	
	    return of(
	    		id,
	            tableroId,
	            TipoEntryHistorial.TABLERO_RENOMBRADO,
	            usuario,
	            timestamp,
	            detalles
	    );
	}
	
	// LISTA CREADA
	public static EntryHistorial listaCreada(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp,
	        ListaId listaId,
	        String nombreLista
	) throws EntryHistorialInvalidaException {

	    String detalles = "listaId=" + listaId.getId() +
	            ", nombre=" + nombreLista;

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.LISTA_CREADA,
	            usuario,
	            timestamp,
	            detalles
	    );
	}
	
	// LISTA RENOMBRADA
	public static EntryHistorial listaRenombrada(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp,
	        ListaId listaId,
	        String nombreAnterior,
	        String nombreNuevo
	) throws EntryHistorialInvalidaException {

	    String detalles = "listaId=" + listaId.getId() +
	            ", antiguo=" + nombreAnterior +
	            ", nuevo=" + nombreNuevo;

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.LISTA_RENOMBRADA,
	            usuario,
	            timestamp,
	            detalles
	    );
	}
	
	// TARJETA CREADA
	public static EntryHistorial tarjetaCreada(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp,
	        TarjetaId tarjetaId,
	        ListaId listaId
	) throws EntryHistorialInvalidaException {

	    String detalles = "tarjetaId=" + tarjetaId.getId() +
	            ", listaId=" + listaId.getId();

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.TARJETA_CREADA,
	            usuario,
	            timestamp,
	            detalles
	    );
	}
	
	// TARJETA MOVIDA
	public static EntryHistorial tarjetaMovida(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp,
	        TarjetaId tarjetaId,
	        ListaId fromListId,
	        ListaId toListId
	) throws EntryHistorialInvalidaException {

	    String detalles = "tarjetaId=" + tarjetaId.getId() +
	            ", fromListId=" + fromListId.getId() +
	            ", toListId=" + toListId.getId();

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.TARJETA_MOVIDA,
	            usuario,
	            timestamp,
	            detalles
	    );
	}
	
	// TARJETA ETIQUETADA
	public static EntryHistorial tarjetaEtiquetada(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp,
	        TarjetaId tarjetaId,
	        Etiqueta etiqueta
	) throws EntryHistorialInvalidaException {

	    String detalles = "tarjetaId=" + tarjetaId.getId() +
	            ", etiqueta=" + etiqueta.nombre() +
	            ", color=" + etiqueta.color();

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.TARJETA_ETIQUETADA,
	            usuario,
	            timestamp,
	            detalles
	    );
	}
	
	// TABLERO BLOQUEADO
	public static EntryHistorial tableroBloqueado(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp,
	        String descripcion
	) throws EntryHistorialInvalidaException {

	    String detalles = "descripcion=" + descripcion;

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.TABLERO_BLOQUEADO,
	            usuario,
	            timestamp,
	            detalles
	    );
	}
	
	// TABLERO DESBLOQUEADO
	public static EntryHistorial tableroDesbloqueado(
	        EntryHistorialId id,
	        TableroId tableroId,
	        UsuarioId usuario,
	        LocalDateTime timestamp
	) throws EntryHistorialInvalidaException {

	    return of(
	            id,
	            tableroId,
	            TipoEntryHistorial.TABLERO_DESBLOQUEADO,
	            usuario,
	            timestamp,
	            "Tablero desbloqueado"
	    );
	}
}