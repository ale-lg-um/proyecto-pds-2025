package InterfacesSalida;

/* ------------------------- TABLEROS ------------------------- */

interface RepositorioTableros {
    void guardar(Tablero tablero);
    
    Optional<Tablero> buscarPorId(TableroId tableroId);

    // Necesario para HA6 (hard delete)
    void eliminarPorId(TableroId tableroId);

    // Útil para tableros disponibles (HA6)
    List<TableroId> listarIdsPorUsuario(UsuarioId usuarioId);
}

/* ------------------------- PLANTILLAS ------------------------- */

interface RepositorioPlantilla {
    void guardar(Plantilla plantilla);
    
    Optional<Plantilla> buscarPorId(PlantillaId plantillaId);
}

/* ------------------------- LISTAS ------------------------- */

interface RepositorioListas {
    void guardar(Lista lista);
    
    Optional<Lista> buscarPorId(ListaId listaId);
    
    // Útil para configurar límite a nivel de tablero, configurar prerrequisitos y eliminar tablero
    Set<Lista> buscarPorIds(Set<ListaId> ids);

    // Necesario para HA6 (hard delete cascada) y para operaciones por tablero
    Set<Lista> buscarPorTableroId(TableroId tableroId);

    // Necesario para borrar lista (HB1) y cascada al borrar tablero
    void eliminarPorId(ListaId listaId);

    // Útil para borrar todo por tablero sin cargarlo (hard delete)
    void eliminarPorTableroId(TableroId tableroId);
}

/* ------------------------- TARJETAS ------------------------- */

enum ModoFiltradoEtiquetas { AND, OR }

interface RepositorioTarjetas {
    void guardar(Tarjeta tarjeta);
    
    Optional<Tarjeta> buscarPorId(TarjetaId tarjetaId);

    // Necesario para eliminar todas las tarjetas dentro de una lista (HB1)
    List<Tarjeta> buscarPorListaId(ListaId listaId);

    // Necesario para hard delete cascada al eliminar tablero
    List<Tarjeta> buscarPorTableroId(TableroId tableroId);

    // Borrado directo
    void eliminarPorId(TarjetaId tarjetaId);

    // Borrados masivos
    void eliminarPorListaId(ListaId listaId);
    
    void eliminarPorTableroId(TableroId tableroId);

    // HG1 – Filtrado por etiquetas (consulta)
    Page<Tarjeta> filtrarPorEtiquetas(
            TableroId tableroId,
            List<Etiqueta> etiquetas,
            ModoFiltradoEtiquetas modo,
            PageRequest pageRequest
    );
}

/* ------------------------- USUARIOS ------------------------- */

interface RepositorioUsuarios {
    void guardar(Usuario usuario);
    
    Optional<Usuario> buscarPorEmail(UsuarioId usuarioId);
}

/* ------------------------- HISTORIAL (EntryHistorial) ------------------------- */

interface RepositorioEntryHistorial {
    void guardar(EntryHistorial entry);

    Page<EntryHistorial> consultarPorTablero(TableroId tableroId, PageRequest pageRequest);

    // Necesario para HA6 (hard delete) -> "El historial del tablero desaparece"
    void eliminarPorTableroId(TableroId tableroId);
}

/* ------------------------- EMAIL (autenticación) ------------------------- */

interface PuertoEnvioEmail {
	void enviarEmail(UsuarioId destinatario, String asunto, String cuerpo);
}

/* ------------------------- YAML (plantillas) ------------------------- */

interface PuertoParserYAML {
    EspecificacionTableroPlantilla parse(String yaml);
}

/* ------------------------- CÓDIGOS DE LOGIN ------------------------- */

interface RepositorioCodigosLogin {
	void guardarCodigo(UsuarioId usuarioId, String codigo, Instant expiraEn);

	Optional<String> buscarCodigoVigente(UsuarioId usuarioId);

	void invalidarCodigo(UsuarioId usuarioId);
}

/* ------------------------- SESIONES DE APLICACIÓN ------------------------- */
public interface RepositorioSesiones {

	void guardarToken(String token, UsuarioId usuarioId, Instant expiraEn);

	Optional<UsuarioId> buscarUsuarioPorTokenVigente(String token);

	void extenderExpiracion(String token, Instant nuevaExpiracion);

	void invalidarToken(String token);
}