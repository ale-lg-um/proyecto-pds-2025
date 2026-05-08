package es.um.pds.tarjetas.ui.infrastructure.api;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

public class LoginApiClient extends BaseApiClient {
	
	// Constructor
	
	public LoginApiClient(String baseURL) {
		super(baseURL + "/usuarios/");
	}
	
	// Métodos
	
	public void loginUsuario(String usuarioId) throws IOException, InterruptedException, RuntimeException {
		post(enc(usuarioId) + "/login", null);		
	}
	
	public Optional<String> verificarCodigo(String usuarioId, String codigo) throws IOException, InterruptedException, RuntimeException {
		HttpResponse<String> response = post(enc(usuarioId) + "/verificar?codigo=" + enc(codigo), null);
		if(response.statusCode() == 200) {
			Optional<String> token = response.headers().firstValue("Authorization");
			return token;
		}
		
		throw new RuntimeException("El servidor no ha podido verificar el código introducido, " + response.body());
	}
}
