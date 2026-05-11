package es.um.pds.tarjetas.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
import es.um.pds.tarjetas.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.TipoEntryHistorial;
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.EntryHistorialDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.domain.ports.output.PuertoEnvioEmail;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;

//Levanta el contexto de la aplicación con test SpringBoot
@SpringBootTest

//Activa el perfil test de Spring, porque se pueden tener distintos entornos
@ActiveProfiles("test")

//Propiedades específicas para este test
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:tarjetasdb_test;DB_CLOSE_DELAY=-1",
		"spring.datasource.driverClassName=org.h2.Driver", "spring.datasource.username=sa",
		"spring.datasource.password=", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false",
		"spring.mail.username=test@ejemplo.com" })
@Transactional
class ServicioTableroListaIntegrationTest {

	private static final String EMAIL_USUARIO = "ejemplo@um.es";
	private static final String NOMBRE_TABLERO = "Proyecto PDS";

	@Autowired
	private ServicioTablero servicioTablero;

	@Autowired
	private ServicioLista servicioLista;

	@Autowired
	private ServicioHistorial servicioHistorial;

	@Autowired
	private RepositorioTableros repoTableros;

	@Autowired
	private RepositorioListas repoListas;

	@MockBean
	private PuertoEnvioEmail puertoEnvioEmail;

	private ResultadoCrearTableroDTO crearTableroBase() {
		CrearTableroCmd cmd = new CrearTableroCmd(NOMBRE_TABLERO, EMAIL_USUARIO, null, null, null);
		return servicioTablero.crearTablero(cmd);
	}

	@Test
	void crearTablero_registraEntradaEnHistorial() {
		ResultadoCrearTableroDTO resultado = crearTableroBase();

		PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(resultado.tableroId(), 0, 20);

		assertFalse(historial.contenido().isEmpty());

		EntryHistorialDTO entry = historial.contenido().get(0);
		
		String detalles = "Tablero creado. Nombre del tablero: " + resultado.nombre();
		
		assertEquals(TipoEntryHistorial.TABLERO_CREADO.name(), entry.tipo());
		assertEquals(resultado.tableroId(), entry.tableroId());
		assertEquals(EMAIL_USUARIO, entry.usuario());
		assertTrue(entry.detalles().equals(detalles));
	}

	@Test
	void crearLista_conNombreDuplicado_falla() {
		ResultadoCrearTableroDTO resultado = crearTableroBase();

		servicioLista.crearLista(resultado.tableroId(), "TODO", EMAIL_USUARIO);

		assertThrows(ListaInvalidaException.class,
				() -> servicioLista.crearLista(resultado.tableroId(), "TODO", EMAIL_USUARIO));
	}

	@Test
	void crearLista_persisteListaYAsociaSuIdAlTablero() {
		ResultadoCrearTableroDTO resultado = crearTableroBase();

		ListaDTO listaDTO = servicioLista.crearLista(resultado.tableroId(), "DOING", EMAIL_USUARIO);

		assertEquals("DOING", listaDTO.nombre());

		TableroId tableroId = TableroId.of(resultado.tableroId());
		ListaId listaId = ListaId.of(listaDTO.id());

		Optional<Tablero> tableroOpt = repoTableros.buscarPorId(tableroId);
		assertTrue(tableroOpt.isPresent());

		Tablero tablero = tableroOpt.get();
		assertTrue(tablero.getListas().contains(listaId));

		Optional<Lista> listaOpt = repoListas.buscarPorId(listaId);
		assertTrue(listaOpt.isPresent());

		Lista lista = listaOpt.get();
		assertEquals("DOING", lista.getNombreLista());
	}

	@Test
	void crearLista_registraEntradaEnHistorial() {
		ResultadoCrearTableroDTO resultado = crearTableroBase();

		ListaDTO listaDTO = servicioLista.crearLista(resultado.tableroId(), "REVIEW", EMAIL_USUARIO);

		PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(resultado.tableroId(), 0, 20);

		String detalles = "Lista creada con ID " + listaDTO.id() + ", nombre: " + listaDTO.nombre();
		
		assertTrue(historial.contenido().stream()
				.anyMatch(entry -> entry.tipo().equals(TipoEntryHistorial.LISTA_CREADA.name())
						&& entry.usuario().equals(EMAIL_USUARIO)
						&& entry.detalles().equals(detalles)));
	}

	@Test
	void crearLista_quedaRecuperablePorId() {
		ResultadoCrearTableroDTO resultado = crearTableroBase();

		ListaDTO listaDTO = servicioLista.crearLista(resultado.tableroId(), "DONE", EMAIL_USUARIO);

		Optional<Lista> listaOpt = repoListas.buscarPorId(ListaId.of(listaDTO.id()));

		assertTrue(listaOpt.isPresent());
		assertEquals("DONE", listaOpt.get().getNombreLista());
	}

	@Test
	void crearLista_buscarPorTableroId_devuelveLaListaCreada() {
		ResultadoCrearTableroDTO resultado = crearTableroBase();

		ListaDTO listaDTO = servicioLista.crearLista(resultado.tableroId(), "BACKLOG", EMAIL_USUARIO);

		Set<Lista> listas = repoListas.buscarPorTableroId(TableroId.of(resultado.tableroId()));

		assertTrue(listas.stream().anyMatch(
				l -> l.getIdentificador().equals(ListaId.of(listaDTO.id())) && l.getNombreLista().equals("BACKLOG")));
	}
	
	@Test
    void renombrarTablero_actualizaNombreYRegistraHistorial() {
        ResultadoCrearTableroDTO resultado = crearTableroBase();

        servicioTablero.renombrarTablero(resultado.tableroId(), "Nuevo nombre", EMAIL_USUARIO);

        Tablero tablero = repoTableros.buscarPorId(TableroId.of(resultado.tableroId())).orElseThrow();

        assertEquals("Nuevo nombre", tablero.getNombre());

        PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(resultado.tableroId(), 0, 20);

        assertTrue(historial.contenido().stream()
                .anyMatch(e -> e.tipo().equals(TipoEntryHistorial.TABLERO_EDITADO.name())
                        && e.detalles().contains("Nombre antiguo: " + NOMBRE_TABLERO)
                        && e.detalles().contains("nombre nuevo: Nuevo nombre")));
    }

	@Test
	void bloquearYDesbloquearTablero_registraHistorial() {
	    ResultadoCrearTableroDTO resultado = crearTableroBase();

	    servicioTablero.bloquearTablero(
	            resultado.tableroId(),
	            java.time.LocalDateTime.now(),
	            java.time.LocalDateTime.now().plusMinutes(10),
	            "Motivo test",
	            EMAIL_USUARIO
	    );

	    Tablero bloqueado = repoTableros.buscarPorId(TableroId.of(resultado.tableroId())).orElseThrow();

	    assertTrue(bloqueado.isBloqueado());

	    servicioTablero.desbloquearTablero(resultado.tableroId(), EMAIL_USUARIO);

	    PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(resultado.tableroId(), 0, 20);

	    assertTrue(historial.contenido().stream()
	            .anyMatch(e -> e.tipo().equals(TipoEntryHistorial.TABLERO_BLOQUEADO.name())
	                    && e.usuario().equals(EMAIL_USUARIO)
	                    && e.detalles().contains("Motivo test")));

	    assertTrue(historial.contenido().stream()
	            .anyMatch(e -> e.tipo().equals(TipoEntryHistorial.TABLERO_DESBLOQUEADO.name())
	                    && e.usuario().equals(EMAIL_USUARIO)));
	}


    @Test
    void definirListaEspecial_marcaListaYEliminaLimite() {
        ResultadoCrearTableroDTO resultado = crearTableroBase();

        ListaDTO lista = servicioLista.crearLista(resultado.tableroId(), "Completadas", EMAIL_USUARIO);

        servicioLista.configurarLimiteLista(resultado.tableroId(), lista.id(), 5, EMAIL_USUARIO);
        servicioLista.definirListaEspecial(resultado.tableroId(), lista.id(), EMAIL_USUARIO);

        Lista listaPersistida = repoListas.buscarPorId(ListaId.of(lista.id())).orElseThrow();
        Tablero tablero = repoTableros.buscarPorId(TableroId.of(resultado.tableroId())).orElseThrow();

        assertTrue(listaPersistida.isEspecial());
        //assertNull(listaPersistida.getLimite());
        assertEquals(ListaId.of(lista.id()), tablero.getListaEspecial());
    }

    @Test
    void configurarLimiteLista_actualizaLimiteYRegistraHistorial() {
        ResultadoCrearTableroDTO resultado = crearTableroBase();

        ListaDTO lista = servicioLista.crearLista(resultado.tableroId(), "TODO", EMAIL_USUARIO);

        servicioLista.configurarLimiteLista(resultado.tableroId(), lista.id(), 3, EMAIL_USUARIO);

        Lista persistida = repoListas.buscarPorId(ListaId.of(lista.id())).orElseThrow();

        assertEquals(3, persistida.getLimite());

        PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(resultado.tableroId(), 0, 20);

        assertTrue(historial.contenido().stream()
                .anyMatch(e -> e.tipo().equals(TipoEntryHistorial.LIMITE_LISTA_CONFIGURADO.name())
                        && e.detalles().contains("límite nuevo: 3")));
    }

    @Test
    void eliminarLista_eliminaListaDelTablero() {
        ResultadoCrearTableroDTO resultado = crearTableroBase();

        ListaDTO lista = servicioLista.crearLista(resultado.tableroId(), "A borrar", EMAIL_USUARIO);

        servicioLista.eliminarLista(resultado.tableroId(), lista.id(), EMAIL_USUARIO);

        assertTrue(repoListas.buscarPorId(ListaId.of(lista.id())).isEmpty());

        Tablero tablero = repoTableros.buscarPorId(TableroId.of(resultado.tableroId())).orElseThrow();

        assertFalse(tablero.getListas().contains(ListaId.of(lista.id())));
    }

}