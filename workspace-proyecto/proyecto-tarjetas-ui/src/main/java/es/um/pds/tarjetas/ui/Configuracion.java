package es.um.pds.tarjetas.ui;

import es.um.pds.tarjetas.domain.ports.input.ServicioGestionTablero;
import es.um.pds.tarjetas.domain.ports.output.RepositorioListas;
import es.um.pds.tarjetas.domain.ports.output.RepositorioTableros;
import es.um.pds.tarjetas.ui.controllers.SceneManager;
import es.um.pds.tarjetas.ui.controllers.TableroController;

public abstract class Configuracion {
	// Atributos
	private static Configuracion instancia;
	private final SceneManager sceneManager = new SceneManager();
	
	// Este método solo puede ser invocado desde la App
	static void setInstancia(Configuracion impl ) {
		Configuracion.instancia = impl;
	}
	
	public static Configuracion getInstancia() {
		return Configuracion.instancia;
	}
	
	//public abstract TableroController getTableroController();
	
	public abstract ServicioGestionTablero getServicioTablero();
	public abstract RepositorioListas getRepoListas();
	public abstract RepositorioTableros getRepoTableros();
	
	public SceneManager getSceneManager() {
		return sceneManager;
	}
}
