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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import es.um.pds.tarjetas.application.common.exceptions.PrerrequisitosNoCumplidosException;
import es.um.pds.tarjetas.domain.model.tarjeta.id.TarjetaId;
import es.um.pds.tarjetas.domain.model.tarjeta.model.Tarjeta;
import es.um.pds.tarjetas.domain.model.tarjeta.model.TipoContenidoTarjeta;
import es.um.pds.tarjetas.domain.ports.input.ServicioLista;
import es.um.pds.tarjetas.domain.ports.input.ServicioTablero;
import es.um.pds.tarjetas.domain.ports.input.ServicioTarjeta;
import es.um.pds.tarjetas.domain.ports.input.commands.ContenidoTarjetaCmd;
import es.um.pds.tarjetas.domain.ports.input.commands.CrearTableroCmd;
import es.um.pds.tarjetas.domain.ports.input.dto.ListaDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.PageDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.ResultadoCrearTableroDTO;
import es.um.pds.tarjetas.domain.ports.input.dto.TarjetaDTO;
import es.um.pds.tarjetas.domain.ports.output.ModoFiltradoEtiquetas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTarjetas;
import es.um.pds.tarjetas.domain.ports.input.ServicioFiltradoTarjetas;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ServicioTarjetaIntegrationTest {

	@Autowired
	private ServicioTablero servicioTablero;

	@Autowired
	private ServicioLista servicioLista;

	@Autowired
	private ServicioTarjeta servicioTarjeta;

	@Autowired
	private ServicioFiltradoTarjetas servicioFiltradoTarjetas;

	@Autowired
	private RepositorioTarjetas repoTarjetas;

	private ResultadoCrearTableroDTO crearTablero() {
		return servicioTablero.crearTablero(new CrearTableroCmd("Tablero tarjetas", "ejemplo@um.es", null, null, null));
	}

	@Test
	@DisplayName("Una tarjeta no puede entrar en una lista si no cumple los prerrequisitos")
	void moverTarjeta_aListaConPrerrequisitosSinHaberPasadoPorEllos_falla() {
		ResultadoCrearTableroDTO tablero = crearTablero();
		ListaDTO todo = servicioLista.crearLista(tablero.tableroId(), "TODO", "ejemplo@um.es");
		ListaDTO doing = servicioLista.crearLista(tablero.tableroId(), "DOING", "ejemplo@um.es");
		ListaDTO done = servicioLista.crearLista(tablero.tableroId(), "DONE", "ejemplo@um.es");

		servicioLista.configurarPrerrequisitosLista(tablero.tableroId(), done.id(), Set.of(doing.id()), "ejemplo@um.es");

		TarjetaDTO tarjeta = servicioTarjeta.crearTarjeta(tablero.tableroId(), todo.id(), "Implementar login",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, "Enviar código por correo", null, "ejemplo@um.es"));

		assertThrows(PrerrequisitosNoCumplidosException.class, () -> servicioTarjeta.moverTarjeta(tablero.tableroId(),
				tarjeta.id(), todo.id(), done.id(), "ejemplo@um.es"));
	}

	@Test
	@DisplayName("Completar la checklist completa la tarjeta y la mueve a la lista especial")
	void completarChecklist_mueveTarjetaAListaEspecial() {
		ResultadoCrearTableroDTO tablero = crearTablero();
		ListaDTO pendientes = servicioLista.crearLista(tablero.tableroId(), "Pendientes", "ejemplo@um.es");
		ListaDTO completadas = servicioLista.crearLista(tablero.tableroId(), "Completadas", "ejemplo@um.es");
		servicioLista.definirListaEspecial(tablero.tableroId(), completadas.id(), "ejemplo@um.es");

		TarjetaDTO tarjeta = servicioTarjeta.crearTarjeta(tablero.tableroId(), pendientes.id(), "Preparar entrega",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.CHECKLIST, null, List.of("Memoria", "Tests"),
						"ejemplo@um.es"));

		servicioTarjeta.completarItemChecklist(tablero.tableroId(), pendientes.id(), tarjeta.id(), 0, "ejemplo@um.es");
		Tarjeta trasPrimerItem = repoTarjetas.buscarPorId(TarjetaId.of(tarjeta.id())).orElseThrow();
		assertFalse(trasPrimerItem.isCompletada());
		assertEquals(pendientes.id(), trasPrimerItem.getListaActual().getId());

		servicioTarjeta.completarItemChecklist(tablero.tableroId(), pendientes.id(), tarjeta.id(), 1, "ejemplo@um.es");
		Tarjeta trasCompletar = repoTarjetas.buscarPorId(TarjetaId.of(tarjeta.id())).orElseThrow();

		assertTrue(trasCompletar.isCompletada());
		assertEquals(completadas.id(), trasCompletar.getListaActual().getId());
		assertTrue(trasCompletar.getListasVisitadas().stream().anyMatch(id -> id.getId().equals(completadas.id())));
	}

	@Test
	@DisplayName("El filtrado OR y AND por etiquetas devuelve resultados distintos cuando corresponde")
	void filtrarTarjetas_porEtiquetas_andYOr() {
		ResultadoCrearTableroDTO tablero = crearTablero();
		ListaDTO lista = servicioLista.crearLista(tablero.tableroId(), "TODO", "ejemplo@um.es");

		TarjetaDTO tarjetaBackend = servicioTarjeta.crearTarjeta(tablero.tableroId(), lista.id(), "Backend",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, "Implementar servicios", null, "ejemplo@um.es"));
		servicioTarjeta.addEtiquetaATarjeta(tablero.tableroId(), lista.id(), tarjetaBackend.id(), "backend", "azul",
				"ejemplo@um.es");
		servicioTarjeta.addEtiquetaATarjeta(tablero.tableroId(), lista.id(), tarjetaBackend.id(), "urgente", "rojo",
				"ejemplo@um.es");

		TarjetaDTO tarjetaFrontend = servicioTarjeta.crearTarjeta(tablero.tableroId(), lista.id(), "Frontend",
				new ContenidoTarjetaCmd(TipoContenidoTarjeta.TAREA, "Pulir interfaz", null, "ejemplo@um.es"));
		servicioTarjeta.addEtiquetaATarjeta(tablero.tableroId(), lista.id(), tarjetaFrontend.id(), "frontend", "verde",
				"ejemplo@um.es");

		PageDTO<TarjetaDTO> or = servicioFiltradoTarjetas.filtrarPorEtiquetas(tablero.tableroId(),
				List.of("backend", "frontend"), ModoFiltradoEtiquetas.OR, 0, 20);
		PageDTO<TarjetaDTO> and = servicioFiltradoTarjetas.filtrarPorEtiquetas(tablero.tableroId(),
				List.of("backend", "urgente"), ModoFiltradoEtiquetas.AND, 0, 20);

		assertEquals(2, or.contenido().size());
		assertEquals(1, and.contenido().size());
		assertEquals(tarjetaBackend.id(), and.contenido().getFirst().id());
	}
}
