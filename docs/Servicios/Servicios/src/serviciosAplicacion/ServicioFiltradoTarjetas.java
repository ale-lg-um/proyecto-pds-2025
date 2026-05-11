package serviciosAplicacion;

public interface ServicioFiltradoTarjetas {

	PageDTO<TarjetaDTO> filtrarPorEtiquetas(String tableroId, List<String> nombresEtiquetas, ModoFiltradoEtiquetas modo,
			int pagina, int tamano);
}
