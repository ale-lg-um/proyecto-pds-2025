package serviciosAplicacion;

public interface ServicioHistorial {
	PageDTO<EntryHistorialDTO> consultarPorTablero(String tableroId, int pagina, int tamano);
}
