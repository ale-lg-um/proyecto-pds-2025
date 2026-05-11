package es.um.pds.tarjetas.adapters.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.TarjetaEntity;

/**
 * Repositorio JPA de bajo nivel para tarjetas
 */
@Repository
public interface TarjetaRepositoryJPA extends JpaRepository<TarjetaEntity, String> {

	/**
	 * Recupera todas las tarjetas de una lista
	 * Implementación generada automáticamente por JPA
	 */
	List<TarjetaEntity> findByListaActualId(String listaActualId);

	/**
	 * Recupera todas las tarjetas de un tablero
	 * Implementación generada automáticamente por JPA
	 */
	List<TarjetaEntity> findByTableroId(String tableroId);

	/**
	 * Elimina todas las tarjetas de una lista
	 * Implementación generada automáticamente por JPA
	 */
	void deleteByListaActualId(String listaActualId);

	/**
	 * Elimina todas las tarjetas de un tablero
	 * Implementación generada automáticamente por JPA
	 */
	void deleteByTableroId(String tableroId);

	/**
	 * Filtrado OR por nombre de etiqueta dentro de un tablero. Devuelve tarjetas
	 * que tengan al menos una de las etiquetas indicadas. Implementación con
	 * una nueva Query. Pageable interfaz de Spring para paginación
	 */
	@Query("""
				SELECT DISTINCT t
				FROM TarjetaEntity t
				JOIN t.etiquetas e
				WHERE t.tableroId = :tableroId
				  AND e.nombre IN :nombresEtiquetas
			""")
	Page<TarjetaEntity> filtrarPorEtiquetasOR(String tableroId, List<String> nombresEtiquetas, Pageable pageable);

	/**
	 * Filtrado AND por nombre de etiqueta dentro de un tablero. Devuelve tarjetas
	 * que contengan todas las etiquetas indicadas. Implementación con una
	 * nueva Query. Pageable interfaz de Spring para paginación
	 */
	@Query("""
				SELECT t
				FROM TarjetaEntity t
				JOIN t.etiquetas e
				WHERE t.tableroId = :tableroId
				  AND e.nombre IN :nombresEtiquetas
				GROUP BY t
				HAVING COUNT(DISTINCT e.nombre) = :numeroEtiquetas
			""")
	Page<TarjetaEntity> filtrarPorEtiquetasAND(String tableroId, List<String> nombresEtiquetas, long numeroEtiquetas,
			Pageable pageable);
}