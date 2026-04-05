package es.um.pds.tarjetas.adapters.jpa.repository.implementations;

import java.time.Instant;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.CodigoLoginEntity;
import es.um.pds.tarjetas.adapters.jpa.repository.CodigoLoginRepositoryJPA;
import es.um.pds.tarjetas.domain.model.usuario.id.UsuarioId;
import es.um.pds.tarjetas.domain.ports.output.RepositorioCodigosLogin;

@Repository
@Primary
public class RepositorioCodigoLoginAdapterJPA implements RepositorioCodigosLogin {

	// Inyección de dependencias necesarias
	private final CodigoLoginRepositoryJPA codigoLoginRepositoryJPA;

	public RepositorioCodigoLoginAdapterJPA(CodigoLoginRepositoryJPA codigoLoginRepositoryJPA) {
		this.codigoLoginRepositoryJPA = codigoLoginRepositoryJPA;
	}

	/*
	 * Usa el método save de JpaRepository
	 */
	@Override
	public void guardarCodigo(UsuarioId usuarioId, String codigo, Instant expiraEn) {
		if (usuarioId == null) {
			throw new IllegalArgumentException("El identificador del usuario no puede ser nulo");
		}
		if (codigo == null || codigo.isBlank()) {
			throw new IllegalArgumentException("El código no puede ser nulo ni vacío");
		}
		if (expiraEn == null) {
			throw new IllegalArgumentException("La fecha de expiración no puede ser nula");
		}

		CodigoLoginEntity entity = new CodigoLoginEntity(usuarioId.toString(), codigo, expiraEn);
		codigoLoginRepositoryJPA.save(entity);
	}

	/*
	 * Usa el método findByUsuarioIdAndExpiraEnAfter cuya implementación genera
	 * automáticamente Spring Data JPA porque coincide con atributos reales de la
	 * entidad y sigue la estructura de nombres adecuada
	 */
	@Override
	public Optional<String> buscarCodigoVigente(UsuarioId usuarioId) {
		if (usuarioId == null) {
			throw new IllegalArgumentException("El identificador del usuario no puede ser nulo");
		}
		return codigoLoginRepositoryJPA.findByUsuarioIdAndExpiraEnAfter(usuarioId.toString(), Instant.now())
				.map(CodigoLoginEntity::getCodigo);
	}

	/*
	 * Usa el método deleteByUsuarioId cuya implementación genera automáticamente
	 * Spring Data JPA porque coincide con atributos reales de la entidad
	 * y sigue la estructura de nombres adecuada
	 */
	@Override
	public void invalidarCodigo(UsuarioId usuarioId) {
		if (usuarioId == null) {
			throw new IllegalArgumentException("El identificador del usuario no puede ser nulo");
		}
		codigoLoginRepositoryJPA.deleteByUsuarioId(usuarioId.toString());
	}
}