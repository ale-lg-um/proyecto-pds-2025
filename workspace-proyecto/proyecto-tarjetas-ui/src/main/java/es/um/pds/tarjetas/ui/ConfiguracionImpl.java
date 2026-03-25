package es.um.pds.tarjetas.ui;

import es.um.pds.tarjetas.application.usecases.ServicioGestionTableroImpl;
import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.domain.rules.PoliticaTarjetas;
import es.um.pds.tarjetas.infrastructure.adapters.RepositorioListasMemoria;
import es.um.pds.tarjetas.infrastructure.adapters.RepositorioTablerosMemoria;

public class ConfiguracionImpl extends Configuracion{
	// Atributos
	private ServicioGestionTablero servicioTablero;
	private RepositorioListas repoListas;
	private RepositorioTableros repoTableros;
	
	public ConfiguracionImpl() {
		this.repoListas = new RepositorioListasMemoria();
		this.repoTableros = new RepositorioTablerosMemoria();
		PoliticaTarjetas politica = new PoliticaTarjetas();
		this.servicioTablero = new ServicioGestionTableroImpl(repoTableros, repoListas, politica);
	}
	
	@Override
	public ServicioGestionTablero getServicioTablero() {
		return this.servicioTablero;
	}
	
	@Override
	public RepositorioListas getRepoListas() {
		return this.repoListas;
	}
	
	@Override
	public RepositorioTableros getRepoTableros() {
		return this.repoTableros;
	}
}
