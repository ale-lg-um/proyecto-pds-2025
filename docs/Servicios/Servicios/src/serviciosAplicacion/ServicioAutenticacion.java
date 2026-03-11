package serviciosAplicacion;

public class ServicioAutenticacion {
	void enviarCodigoLogin(Email email);
	AuthToken verificarCodigoLogin(Email email, Codigo codigo);
}
