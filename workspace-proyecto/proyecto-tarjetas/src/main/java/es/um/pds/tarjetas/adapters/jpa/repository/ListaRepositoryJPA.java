package es.um.pds.tarjetas.adapters.jpa.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.ListaEntity;

/**
 * Repositorio JPA de bajo nivel para listas
 */
@Repository
public interface ListaRepositoryJPA extends JpaRepository<ListaEntity, String> {

	/**
	 * Recupera todas las listas de un tablero
	 * Implementación generada automáticamente por JPA
	 */
	Set<ListaEntity> findByTableroId(String tableroId);

	/**
	 * Recupera todas las listas cuyo id esté en el conjunto indicado
	 * Implementación generada automáticamente por JPA
	 */
	Set<ListaEntity> findByIdIn(Set<String> ids);

	/**
	 * Elimina todas las listas de un tablero
	 * Implementación generada automáticamente por JPA
	 */
	void deleteByTableroId(String tableroId);
}