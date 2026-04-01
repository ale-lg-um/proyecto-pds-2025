package es.um.pds.tarjetas.infrastructure.adapters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.PuertoEnvioEmail;

// Hacemos envío real de correos con Spring Boot. Añadir dependencia al pom
// También hay que cambiar el application.properties de SpringBoot (src/main/resources)
@Component
public class PuertoEnvioEmailImpl implements PuertoEnvioEmail {

	private final JavaMailSender mailSender;
	
	// Usamos el @Value que lo extraiga del application.properties en vez de ponerlo directamente
	@Value("${spring.mail.username}")
	private String from;

	public PuertoEnvioEmailImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	// Método simple usando los métodos de SpringBoot
	@Override
	public void enviarEmail(UsuarioId destinatario, String asunto, String cuerpo) {

		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setTo(destinatario.getCorreo());
		mensaje.setFrom(from);
		mensaje.setSubject(asunto);
		mensaje.setText(cuerpo);

		try {
			 mailSender.send(mensaje);
		} catch (Exception e) {
		    throw new RuntimeException("Error al enviar email", e);
		}
	}

}
