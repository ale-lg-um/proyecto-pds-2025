package es.um.pds.tarjetas.adapters.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.PlantillaEntity;

/**
 * Repositorio JPA de bajo nivel para plantillas.
 */
@Repository
public interface PlantillaRepositoryJPA extends JpaRepository<PlantillaEntity, String> {

	// No tenemos métodos extra porque nos vale con los de JpaRepository
}