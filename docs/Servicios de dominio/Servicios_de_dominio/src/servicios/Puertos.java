package servicios;


interface RepositorioTableros {
	void guardar(Tablero tablero)
}

interface RepositorioPlantilla {
	void guardar(Plantilla plantilla)
}

interface RepositorioUsuarios {
	void guardar(Usuario usuario)
}

interface PuertoRegistroHistorial {
	void append(EntryHistorial entry)
}

interface PuertoEnvioEmail {
	void enviarEmail(MensajeEmail m)
}

interface PuertoParserYAML {
	YamlCorrecto parse(String yaml) throws PlantillaInvalida
}