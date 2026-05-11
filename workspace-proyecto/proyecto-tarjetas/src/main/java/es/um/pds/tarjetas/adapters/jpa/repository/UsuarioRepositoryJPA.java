package es.um.pds.tarjetas.adapters.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.UsuarioEntity;

/**
 * Repositorio JPA de bajo nivel para usuarios
 */
@Repository
public interface UsuarioRepositoryJPA extends JpaRepository<UsuarioEntity, String> {
	// No tenemos métodos extra porque nos vale con los de JpaRepository
}