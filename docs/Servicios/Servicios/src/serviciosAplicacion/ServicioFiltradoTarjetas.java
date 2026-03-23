package serviciosAplicacion;

public interface ServicioFiltradoTarjetas {
	Page<Tarjeta> filtrarPorEtiquetas(TableroId tableroId, List<Etiqueta> etiquetas,
			ModoFiltrado modo, PageRequest page);
}
