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

import es.um.pds.tarjetas.application.common.exceptions.ListaInvalidaException;
import es.um.pds.tarjetas.domain.model.lista.id.ListaId;
import es.um.pds.tarjetas.domain.model.lista.model.Lista;
import es.um.pds.tarjetas.domain.model.tablero.id.TableroId;
import es.um.pds.tarjetas.domain.model.tablero.model.Tablero;
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
		assertEquals(TipoEntryHistorial.TABLERO_CREADO.name(), entry.tipo());
		assertEquals(resultado.tableroId(), entry.tableroId());
		assertEquals(EMAIL_USUARIO, entry.usuario());
		assertTrue(entry.detalles().contains("nombreTablero=" + NOMBRE_TABLERO));
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

		assertTrue(historial.contenido().stream()
				.anyMatch(entry -> entry.tipo().equals(TipoEntryHistorial.LISTA_CREADA.name())
						&& entry.usuario().equals(EMAIL_USUARIO)
						&& entry.detalles().contains("listaId=" + listaDTO.id())
						&& entry.detalles().contains("nombre=REVIEW")));
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
}