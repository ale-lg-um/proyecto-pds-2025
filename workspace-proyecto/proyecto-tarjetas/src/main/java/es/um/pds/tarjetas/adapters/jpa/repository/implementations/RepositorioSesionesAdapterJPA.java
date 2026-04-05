package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.time.Instant;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.SesionEntity;
import es.um.pds.tarjetas.adapters.jpa.repository.SesionRepositoryJPA;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioSesiones;

@Repository
@Primary
public class RepositorioSesionesAdapterJPA implements RepositorioSesiones {

	// Inyección de dependencias necesarias
	private final SesionRepositoryJPA sesionRepositoryJPA;

	public RepositorioSesionesAdapterJPA(SesionRepositoryJPA sesionRepositoryJPA) {
		this.sesionRepositoryJPA = sesionRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardarToken(String token, UsuarioId usuarioId, Instant expiraEn) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("El token no puede ser nulo ni vacío");
		}
		if (usuarioId == null) {
			throw new IllegalArgumentException("El identificador del usuario no puede ser nulo");
		}
		if (expiraEn == null) {
			throw new IllegalArgumentException("La fecha de expiración no puede ser nula");
		}

		SesionEntity entity = new SesionEntity(token, usuarioId.toString(), expiraEn);
		sesionRepositoryJPA.save(entity);
	}

	/*
	 * Usa el método findByTokenAndExpiraEnAfter cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public Optional<UsuarioId> buscarUsuarioPorTokenVigente(String token) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("El token no puede ser nulo ni vacío");
		}
		return sesionRepositoryJPA.findByTokenAndExpiraEnAfter(token, Instant.now())
				.map(entity -> UsuarioId.of(entity.getUsuarioId()));
	}

	/*
	 * Usa el método findByToken cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada. También utiliza el método
	 * save de JpaRepository
	 */
	@Override
	public void extenderExpiracion(String token, Instant nuevaExpiracion) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("El token no puede ser nulo ni vacío");
		}
		if (nuevaExpiracion == null) {
			throw new IllegalArgumentException("La nueva expiración no puede ser nula");
		}

		Optional<SesionEntity> sesionOpt = sesionRepositoryJPA.findByToken(token);
		if (sesionOpt.isPresent()) {
			SesionEntity sesion = sesionOpt.get();
			sesion.setExpiraEn(nuevaExpiracion);
			sesionRepositoryJPA.save(sesion);
		}
	}

	/*
	 * Usa el método deleteByToken cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public void invalidarToken(String token) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("El token no puede ser nulo ni vacío");
		}
		sesionRepositoryJPA.deleteByToken(token);
	}
}