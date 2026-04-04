package es.um.pds.tarjetas.adapters.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.EntryHistorialEntity;

/**
 * Repositorio JPA de bajo nivel para entradas de historial
 */
@Repository
public interface EntryHistorialRepositoryJPA extends JpaRepository<EntryHistorialEntity, String> {

	/**
	 * Recupera el historial de un tablero ordenado por timestamp descendente
	 * Implementación generada automáticamente por JPA. Pageable interfaz de Spring 
	 * para paginación
	 */
	Page<EntryHistorialEntity> findByTableroIdOrderByTimestampDesc(String tableroId, Pageable pageable);

	/**
	 * Elimina todas las entries de historial de un tablero
	 * Implementación generada automáticamente por JPA
	 */
	void deleteByTableroId(String tableroId);
}