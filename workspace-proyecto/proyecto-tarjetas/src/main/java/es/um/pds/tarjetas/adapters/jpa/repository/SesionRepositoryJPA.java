package es.um.pds.tarjetas.adapters.jpa.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.SesionEntity;

/**
 * Repositorio JPA de bajo nivel para sesiones.
 */
@Repository
public interface SesionRepositoryJPA extends JpaRepository<SesionEntity, String> {

	/**
	 * Busca una sesión por su token.
	 */
	Optional<SesionEntity> findByToken(String token);

	/**
	 * Busca una sesión por token solo si sigue vigente.
	 */
	Optional<SesionEntity> findByTokenAndExpiraEnAfter(String token, Instant instante);

	/**
	 * Elimina una sesión por token.
	 */
	void deleteByToken(String token);
}