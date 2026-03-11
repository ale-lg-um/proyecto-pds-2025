package InterfacesSalida;

/* ------------------------- TABLEROS ------------------------- */

interface RepositorioTableros {
    void guardar(Tablero tablero);
    Optional<Tablero> buscarPorId(TableroId tableroId);

    // Necesario para HA6 (hard delete)
    void eliminarPorId(TableroId tableroId);

    // Útil para "tableros disponibles" (HA6). Si no lo quieres, puedes quitarlo.
    List<TableroId> listarIdsPorUsuario(Email email);
}

/* ------------------------- PLANTILLAS ------------------------- */

interface RepositorioPlantilla {
    void guardar(Plantilla plantilla);
    Optional<Plantilla> buscarPorId(PlantillaId plantillaId);
}

/* ------------------------- LISTAS ------------------------- */

interface RepositorioListas {
    void guardar(ListaDeTareas lista);
    Optional<ListaDeTareas> buscarPorId(ListaId listaId);

    // Necesario para HA6 (hard delete cascada) y para operaciones por tablero
    List<ListaDeTareas> buscarPorTableroId(TableroId tableroId);

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

    // Borrados masivos (eficientes)
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
    Optional<Usuario> buscarPorEmail(Email email);
}

/* ------------------------- HISTORIAL (EntryHistorial) ------------------------- */

interface RepositorioEntryHistorial {
    void append(EntryHistorial entry);

    Page<EntryHistorial> consultarPorTablero(TableroId tableroId, PageRequest pageRequest);

    // Necesario para HA6 (hard delete) -> "El historial del tablero desaparece"
    void eliminarPorTableroId(TableroId tableroId);

    // Posibles mejoras:
    /*
    // Opcional: filtros si los expones en REST
    Page<EntryHistorial> consultarPorTableroYTipo(
            TableroId tableroId,
            TipoEntry tipo,
            PageRequest pageRequest
    );

    // Opcional: si quieres rango de fechas
    Page<EntryHistorial> consultarPorTableroYRango(
            TableroId tableroId,
            Instant from,
            Instant to,
            PageRequest pageRequest
    );
    */
}

/* ------------------------- EMAIL (autenticación) ------------------------- */

interface PuertoEnvioEmail {
    void enviarEmail(MensajeEmail m);
}

/* ------------------------- YAML (plantillas) ------------------------- */

interface PuertoParserYAML {
    YamlCorrecto parse(String yaml) throws PlantillaInvalida;
}

/* ------------------------- CÓDIGOS DE LOGIN ------------------------- */

interface RepositorioCodigosLogin {
    void guardarCodigo(Email email, CodigoLogin codigo, Instant expiraEn);

    Optional<CodigoLogin> buscarCodigoVigente(Email email);

    void invalidarCodigo(Email email);
}