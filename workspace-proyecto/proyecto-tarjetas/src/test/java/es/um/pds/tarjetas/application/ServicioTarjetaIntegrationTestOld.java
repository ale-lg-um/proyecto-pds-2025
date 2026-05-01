package es.um.pds.tarjetas.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import es.um.pds.tarjetas.adapters.jpa.repository.EntryHistorialRepositoryJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.ListaRepositoryJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.PlantillaRepositoryJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.TableroRepositoryJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.TarjetaRepositoryJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.implementations.RepositorioEntryHistorialAdapterJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.implementations.RepositorioListasAdapterJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.implementations.RepositorioPlantillasAdapterJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.implementations.RepositorioTablerosAdapterJPA;
import es.um.pds.tarjetas.adapters.jpa.repository.implementations.RepositorioTarjetasAdapterJPA;
import es.um.pds.tarjetas.application.common.exceptions.PrerrequisitosNoCumplidosException;
import es.um.pds.tarjetas.application.usecases.ManejadorEventosHistorial;
import es.um.pds.tarjetas.application.usecases.ServicioFiltradoTarjetasImpl;
import es.um.pds.tarjetas.application.usecases.ServicioHistorialImpl;
import es.um.pds.tarjetas.application.usecases.ServicioListaImpl;
import es.um.pds.tarjetas.application.usecases.ServicioTableroImpl;
import es.um.pds.tarjetas.application.usecases.ServicioTarjetaImpl;
import es.um.pds.tarjetas.common.events.EventBus;
import es.um.pds.tarjetas.common.events.EventBusImpl;
import es.um.pds.tarjetas.domain.model.entryHistorial.model.TipoEntryHistorial;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;
import es.um.pds.tarjetas.domain.ports.input.ServicioFiltradoTarjetas;
import es.um.pds.tarjetas.domain.ports.input.ServicioHistorial;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.EntryHistorialDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;
import es.um.pds.tarjetas.domain.ports.output.PuertoParserYAML;
import es.um.pds.tarjetas.domain.ports.output.RepositorioEntryHistorial;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioPlantillas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;
import es.um.pds.tarjetas.domain.rules.PoliticaListas;
import es.um.pds.tarjetas.domain.rules.PoliticaTarjetas;
import es.um.pds.tarjetas.infrastructure.adapters.ParserYAMLImpl;
import jakarta.transaction.Transactional;

@DataJpaTest
@Transactional

// Activa el perfil test de Spring, porque se pueden tener distintos entornos
@ActiveProfiles("test")

// Fuerza el uso de la base de datos embebida H2 en tests
// Si hay una base de datos configurada, sustituirla por una base en memoria
// Importante para no conectarse a la real y romper datos
@AutoConfigureTestDatabase(replace = Replace.ANY)

// Propiedades específicas para este test
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:tarjetasdb_test;DB_CLOSE_DELAY=-1",
		"spring.datasource.driverClassName=org.h2.Driver", "spring.datasource.username=sa",
		"spring.datasource.password=", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false" })

// Importar una configuración personalizada de beans para el test
@Import(ServicioTarjetaIntegrationTestOld.JpaTestConfig.class)
class ServicioTarjetaIntegrationTestOld {

	private static final String EMAIL_USUARIO = "ejemplo@um.es";

	@Autowired
	private ServicioTablero servicioTablero;

	@Autowired
	private ServicioLista servicioLista;

	@Autowired
	private ServicioTarjeta servicioTarjeta;

	@Autowired
	private ServicioFiltradoTarjetas servicioFiltradoTarjetas;

	@Autowired
	private ServicioHistorial servicioHistorial;

	@Autowired
	private RepositorioTarjetas repoTarjetas;

	private ResultadoCrearTableroDTO crearTablero() {
		return servicioTablero.crearTablero(new CrearTableroCmd("Tablero tarjetas", EMAIL_USUARIO, null, null, null));
	}

	@Test
	@DisplayName("Una tarjeta no puede entrar en una lista si no cumple los prerrequisitos")
	void moverTarjeta_aListaConPrerrequisitosSinHaberPasadoPorEllos_falla() {
		ResultadoCrearTableroDTO tablero = crearTablero();

		ListaDTO todo = servicioLista.crearLista(tablero.tableroId(), "TODO", EMAIL_USUARIO);
		ListaDTO doing = servicioLista.crearLista(tablero.tableroId(), "DOING", EMAIL_USUARIO);
		ListaDTO done = servicioLista.crearLista(tablero.tableroId(), "DONE", EMAIL_USUARIO);

		servicioLista.configurarPrerrequisitosLista(tablero.tableroId(), done.id(), Set.of(doing.id()), EMAIL_USUARIO);

		TarjetaDTO tarjeta = servicioTarjeta.crearTarjeta(tablero.tableroId(), todo.id(), "Implementar login",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, "Enviar código por correo", null, EMAIL_USUARIO));

		assertThrows(PrerrequisitosNoCumplidosException.class, () -> servicioTarjeta.moverTarjeta(tablero.tableroId(),
				tarjeta.id(), todo.id(), done.id(), EMAIL_USUARIO));

		PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(tablero.tableroId(), 0, 20);

		assertTrue(historial.contenido().stream()
				.anyMatch(entry -> entry.tipo().equals(TipoEntryHistorial.PRERREQUISITOS_LISTA_CONFIGURADOS.name())
						&& entry.usuario().equals(EMAIL_USUARIO) && entry.detalles().contains("listaId=" + done.id())));
	}

	@Test
	@DisplayName("Completar la checklist completa la tarjeta y la mueve a la lista especial")
	void completarChecklist_mueveTarjetaAListaEspecial() {
		ResultadoCrearTableroDTO tablero = crearTablero();

		ListaDTO pendientes = servicioLista.crearLista(tablero.tableroId(), "Pendientes", EMAIL_USUARIO);
		ListaDTO completadas = servicioLista.crearLista(tablero.tableroId(), "Completadas", EMAIL_USUARIO);
		servicioLista.definirListaEspecial(tablero.tableroId(), completadas.id(), EMAIL_USUARIO);

		TarjetaDTO tarjeta = servicioTarjeta.crearTarjeta(tablero.tableroId(), pendientes.id(), "Preparar entrega",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.CHECKLIST, null, List.of("Memoria", "Tests"),
						EMAIL_USUARIO));

		servicioTarjeta.completarItemChecklist(tablero.tableroId(), pendientes.id(), tarjeta.id(), 0, EMAIL_USUARIO);

		Tarjeta trasPrimerItem = repoTarjetas.buscarPorId(TarjetaId.of(tarjeta.id())).orElseThrow();
		assertFalse(trasPrimerItem.isCompletada());
		assertEquals(pendientes.id(), trasPrimerItem.getListaActual().getId());

		servicioTarjeta.completarItemChecklist(tablero.tableroId(), pendientes.id(), tarjeta.id(), 1, EMAIL_USUARIO);

		Tarjeta trasCompletar = repoTarjetas.buscarPorId(TarjetaId.of(tarjeta.id())).orElseThrow();

		assertTrue(trasCompletar.isCompletada());
		assertEquals(completadas.id(), trasCompletar.getListaActual().getId());
		assertTrue(trasCompletar.getListasVisitadas().stream().anyMatch(id -> id.getId().equals(completadas.id())));

		PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(tablero.tableroId(), 0, 20);

		assertTrue(historial.contenido().stream()
				.anyMatch(entry -> entry.tipo().equals(TipoEntryHistorial.LISTA_ESPECIAL_DEFINIDA.name())
						&& entry.usuario().equals(EMAIL_USUARIO)
						&& entry.detalles().contains("listaId=" + completadas.id())));

		assertTrue(historial.contenido().stream()
				.anyMatch(entry -> entry.tipo().equals(TipoEntryHistorial.TARJETA_CREADA.name())
						&& entry.usuario().equals(EMAIL_USUARIO)
						&& entry.detalles().contains("tarjetaId=" + tarjeta.id())));

		assertTrue(historial.contenido().stream()
				.anyMatch(entry -> entry.tipo().equals(TipoEntryHistorial.TARJETA_COMPLETADA.name())
						&& entry.usuario().equals(EMAIL_USUARIO)
						&& entry.detalles().contains("tarjetaId=" + tarjeta.id())));
	}

	@Test
	@DisplayName("El filtrado OR y AND por etiquetas devuelve resultados distintos cuando corresponde")
	void filtrarTarjetas_porEtiquetas_andYOr() {
		ResultadoCrearTableroDTO tablero = crearTablero();
		ListaDTO lista = servicioLista.crearLista(tablero.tableroId(), "TODO", EMAIL_USUARIO);

		TarjetaDTO tarjetaBackend = servicioTarjeta.crearTarjeta(tablero.tableroId(), lista.id(), "Backend",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, "Implementar servicios", null, EMAIL_USUARIO));

		servicioTarjeta.addEtiquetaATarjeta(tablero.tableroId(), lista.id(), tarjetaBackend.id(), "backend", "azul",
				EMAIL_USUARIO);

		servicioTarjeta.addEtiquetaATarjeta(tablero.tableroId(), lista.id(), tarjetaBackend.id(), "urgente", "rojo",
				EMAIL_USUARIO);

		TarjetaDTO tarjetaFrontend = servicioTarjeta.crearTarjeta(tablero.tableroId(), lista.id(), "Frontend",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, "Pulir interfaz", null, EMAIL_USUARIO));

		servicioTarjeta.addEtiquetaATarjeta(tablero.tableroId(), lista.id(), tarjetaFrontend.id(), "frontend", "verde",
				EMAIL_USUARIO);

		PageDTO<TarjetaDTO> or = servicioFiltradoTarjetas.filtrarPorEtiquetas(tablero.tableroId(),
				List.of("backend", "frontend"), ModoFiltradoEtiquetas.OR, 0, 20);

		PageDTO<TarjetaDTO> and = servicioFiltradoTarjetas.filtrarPorEtiquetas(tablero.tableroId(),
				List.of("backend", "urgente"), ModoFiltradoEtiquetas.AND, 0, 20);

		assertEquals(2, or.contenido().size());
		assertEquals(1, and.contenido().size());
		assertEquals(tarjetaBackend.id(), and.contenido().getFirst().id());

		PageDTO<EntryHistorialDTO> historial = servicioHistorial.consultarPorTablero(tablero.tableroId(), 0, 20);

		long etiquetasCreadas = historial.contenido().stream()
				.filter(entry -> entry.tipo().equals(TipoEntryHistorial.TARJETA_ETIQUETADA.name())).count();

		assertEquals(3, etiquetasCreadas);
	}

	@TestConfiguration
	static class JpaTestConfig {

		@Bean
		RepositorioTableros repoTableros(TableroRepositoryJPA tableroRepositoryJPA) {
			return new RepositorioTablerosAdapterJPA(tableroRepositoryJPA);
		}

		@Bean
		RepositorioListas repoListas(ListaRepositoryJPA listaRepositoryJPA) {
			return new RepositorioListasAdapterJPA(listaRepositoryJPA);
		}

		@Bean
		RepositorioTarjetas repoTarjetas(TarjetaRepositoryJPA tarjetaRepositoryJPA) {
			return new RepositorioTarjetasAdapterJPA(tarjetaRepositoryJPA);
		}

		@Bean
		RepositorioPlantillas repoPlantillas(PlantillaRepositoryJPA plantillaRepositoryJPA) {
			return new RepositorioPlantillasAdapterJPA(plantillaRepositoryJPA);
		}

		@Bean
		RepositorioEntryHistorial repoHistorial(EntryHistorialRepositoryJPA entryHistorialRepositoryJPA) {
			return new RepositorioEntryHistorialAdapterJPA(entryHistorialRepositoryJPA);
		}
		
		// Aunque no lo utilicemos, necesario para el bean de servicioTablero
		@Bean
		PuertoParserYAML parserYAML() {
			return new ParserYAMLImpl();
		}
		
		@Bean
		PoliticaListas politicaListas(RepositorioListas repoListas) {
			return new PoliticaListas(repoListas);
		}

		@Bean
		PoliticaTarjetas politicaTarjetas() {
			return new PoliticaTarjetas();
		}

		@Bean
		EventBus eventBus(EventBusImpl eventBusImpl) {
			return eventBusImpl;
		}

		@Bean
		ServicioTablero servicioTablero(RepositorioTableros repoTableros, RepositorioListas repoListas,
				RepositorioTarjetas repoTarjetas, RepositorioPlantillas repoPlantillas, PuertoParserYAML parserYAML,
				EventBus eventBus) {
			return new ServicioTableroImpl(repoTableros, repoListas, repoTarjetas, repoPlantillas, parserYAML,
					eventBus);
		}

		@Bean
		ServicioLista servicioLista(RepositorioTableros repoTableros, RepositorioListas repoListas,
				RepositorioTarjetas repoTarjetas, EventBus eventBus, PoliticaListas politicaListas,
				PoliticaTarjetas politicaTarjetas) {
			return new ServicioListaImpl(repoTableros, repoListas, repoTarjetas, eventBus, politicaListas,
					politicaTarjetas);
		}

		@Bean
		ServicioTarjeta servicioTarjeta(RepositorioTableros repoTableros, RepositorioListas repoListas,
				RepositorioTarjetas repoTarjetas, EventBus eventBus, PoliticaTarjetas politicaTarjetas) {
			return new ServicioTarjetaImpl(repoTableros, repoListas, repoTarjetas, eventBus, politicaTarjetas);
		}

		@Bean
		ServicioFiltradoTarjetas servicioFiltradoTarjetas(RepositorioTarjetas repoTarjetas) {
			return new ServicioFiltradoTarjetasImpl(repoTarjetas);
		}

		@Bean
		ServicioHistorial servicioHistorial(RepositorioEntryHistorial repoHistorial) {
			return new ServicioHistorialImpl(repoHistorial);
		}

		@Bean
		EventBusImpl eventBusImpl(org.springframework.context.ApplicationEventPublisher publisher) {
			return new EventBusImpl(publisher);
		}

		@Bean
		ManejadorEventosHistorial manejadorEventosHistorial(RepositorioEntryHistorial repoHistorial) {
			return new ManejadorEventosHistorial(repoHistorial);
		}
	}
}