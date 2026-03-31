package com.ferbo.arch.persistence;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.tools.exception.SystemException;

/**
 * BaseRepository: Repositorio genérico para cualquier entidad.
 * 
 * PROPÓSITO:
 * - Proveer operaciones básicas de persistencia (CRUD) de forma genérica
 * - Desacoplar la lógica de acceso a datos del framework de persistencia
 * - Mantener independencia de la capa de negocio
 * 
 * RESPONSABILIDADES: 
 * - Delegar todas las operaciones de persistencia a PersistenceContext
 * - No manejar transacciones (eso lo hace BaseUseCase con TransactionManager)
 * - Facilitar consultas y operaciones comunes
 * 
 * Notas: 
 * - Debe ser extendido por repositorios especificos de cada módulo
 * - Permite centralizar lógica genérica para todos los repositorios
 * 
 * REGLA:
 * Este repositorio provee únicamente operaciones básicas.
 *
 * Los repositorios específicos deben exponer métodos orientados a negocio:
 * - buscarUsuariosActivos()
 * - obtenerPedidosPendientes()
 *
 * Evitar exponer únicamente operaciones CRUD genéricas en capas superiores.
 * 
 * @param <T> Tipo de entidad
 */
public abstract class BaseRepository<T, ID> {

     protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Class<T> entityClass;
    protected final PersistenceContext persistenceContext;

    /**
     * Constructor base.
     * 
     * @param entityClass clase de la entidad 
     * @param pesistenceContext Contexto de persistencia agnóstico
     */
    protected BaseRepository(Class<T> entityClass, PersistenceContext persistenceContext) {
        this.entityClass = entityClass;
        this.persistenceContext = persistenceContext;
    }

    // -------------------------------------------------------------------------
    // Operaciones de consulta
    // -------------------------------------------------------------------------

    /**
     * Busca una entidad por su ID.
     * 
     * @param id Identificador de la entidad
     * @return Optional con la entidad encontrada, o vacío si no existe
     */
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(persistenceContext.find(entityClass, id));
    }

    // -------------------------------------------------------------------------
    // Operaciones de escritura
    // -------------------------------------------------------------------------


    /**
     * Persiste o actualiza una entidad.
     * 
     * @param entity Entidad a guardar o actualizar
     * @return Entidad gestionada
     */
     public T save(T entity) {
        try {
            return persistenceContext.save(entity);

        } catch (Exception ex) {
            log.error("Error guardando entidad {}", entityClass.getSimpleName(), ex);
            throw new SystemException("Error al guardar entidad", ex);
        }
    }

    /**
     * Elimina una entidad.
     * 
     * @param entity Entidad a eliminar
     */
     public void delete(T entity) {
        try {
            persistenceContext.delete(entity);
        } catch (Exception ex) {
            log.error("Error eliminando entidad {}", entityClass.getSimpleName(), ex);
            throw new SystemException("Error al eliminar entidad", ex);
        }
    }
}