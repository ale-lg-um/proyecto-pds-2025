package serviciosAplicacion;

public interface ServicioSesion {
	public UsuarioId validarYRenovarToken(String token);
}
