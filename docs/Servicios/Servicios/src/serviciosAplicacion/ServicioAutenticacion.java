package serviciosAplicacion;

public interface ServicioAutenticacion {
	void enviarCodigoLogin(Email email);
	AuthToken verificarCodigoLogin(Email email, Codigo codigo);
}
