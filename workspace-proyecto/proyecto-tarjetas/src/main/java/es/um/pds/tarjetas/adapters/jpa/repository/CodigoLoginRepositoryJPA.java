package es.um.pds.tarjetas.adapters.jpa.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.CodigoLoginEntity;

/**
 * Repositorio JPA de bajo nivel para códigos de login.
 */
@Repository
public interface CodigoLoginRepositoryJPA extends JpaRepository<CodigoLoginEntity, String> {

	/**
	 * Busca el código de login asociado a un usuario.
	 */
	Optional<CodigoLoginEntity> findByUsuarioId(String usuarioId);

	/**
	 * Busca el código de login de un usuario solo si sigue vigente.
	 */
	Optional<CodigoLoginEntity> findByUsuarioIdAndExpiraEnAfter(String usuarioId, Instant instante);

	/**
	 * Elimina el código de login asociado a un usuario.
	 */
	void deleteByUsuarioId(String usuarioId);
}