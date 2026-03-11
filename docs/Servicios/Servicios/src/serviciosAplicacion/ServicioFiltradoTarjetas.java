package serviciosAplicacion;

public class ServicioFiltradoTarjetas {
	Page<Tarjeta> filtrarPorEtiquetas(TableroId tableroId, List<Etiqueta> etiquetas,
			ModoFiltrado modo, PageRequest page);
}
