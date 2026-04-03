package es.um.pds.tarjetas.adapters.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.um.pds.tarjetas.adapters.jpa.entity.TableroEntity;

/**
 * Repositorio JPA de bajo nivel para tableros. Lo usará el adaptador JPA que
 * implemente el puerto RepositorioTableros.
 */
@Repository
public interface TableroRepositoryJPA extends JpaRepository<TableroEntity, String> {

	/**
	 * Busca un tablero por su token/URL compartida.
	 */
	Optional<TableroEntity> findByTokenUrl(String tokenUrl);

	/**
	 * Recupera todos los tableros creados por un usuario.
	 */
	List<TableroEntity> findByCreadorId(String creadorId);
}