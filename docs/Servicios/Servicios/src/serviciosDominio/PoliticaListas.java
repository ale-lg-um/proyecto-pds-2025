package serviciosDominio;

public class PoliticaListas {
	
	// Validar que todas las listas que se quieran configurar como prerrequisito existan en el tablero (R10)
	public void validarPrerrequisitosConfigurados(Set<ListaId> listasTablero, Set<ListaId> prerrequisitos);
	
	// Validar que todas las listas de un tablero tienen nombre único (R11)
	public void validarNombreUnicoEnTablero(TableroId tablero, String nombreLista);
}
