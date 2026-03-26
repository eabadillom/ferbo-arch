package com.ferbo.arch.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.tools.exception.SystemException;
import com.ferbo.tools.functional.ThrowingSupplier;

/**
 * BaseRepository: Repositorio genérico para cualquier entidad.
 * 
 * Propósito:
 * - Proveer operaciones básicas de persistencia (CRUD) de forma genérica
 * - Desacoplar la lógica de acceso a datos del framework de persistencia
 * - No manejar transacciones (eso lo hace BaseUseCase con TransactionManager)
 * 
 * IMPORTANTE:
 * - Solo interactúa con PersistenceContext
 * - Debe ser extendido por respositorios especificos de cada módulo
 * - Permite centralizar consultas genéricas y paginadas
 * 
 * @param <T> Tipo de entidad
 * @param <ID> tipo de identificador de la entidad
 */
public abstract class BaseRepository<T, ID> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Class<T> entityClass;
    protected final PersistenceContext persistenceContext;

    /**
     * Constructor base. 
     * 
     * @param entityClass Clase de la entidad
     * @param persistenceContext Contexto de persistencia agnóstica
     */
    protected BaseRepository(Class<T> entityClass, PersistenceContext persistenceContext) {
        this.entityClass = entityClass;
        this.persistenceContext = persistenceContext;
    }

    // -------------------------------------------------------------------------
    // CRUD genérico
    // -------------------------------------------------------------------------

    /**
     * Busca una entidad por su ID. 
     * 
     * @param id Identificador de la entidad
     * @return Entidad encontrada o null
     */
    public T buscarPorId(ID id) {
        return persistenceContext.find(entityClass, id);
    }

    /**
     * Persiste una nueva entidad. 
     * 
     * @param entity Entidad a guardar
     * @return La misma entidad
     */
    public T guardar(T entity) {
        persistenceContext.persist(entity);
        log.info("Entitdad {} guardada correctamente", entity);
        return entity;
    }

    /**
     * Actualiza una entidad existente. 
     * 
     * @param entity Entidad a actualizar
     * @return Entidad gestionada actualizada
     */
    public T actualizar(T entity) {
        T merged = persistenceContext.merge(entity);
        log.info("Entidad {} actualizada correctamente", entity);
        return merged;
    }

    /**
     * Elimina una entidad. 
     * 
     * @param entity Entidad a eliminar
     */
    public void eliminar(T entity) {
        if (!persistenceContext.contains(entity)) {
            entity = persistenceContext.merge(entity);
        }
        persistenceContext.remove(entity);
        log.info("Entidad {} elimina correctamente", entity);
    }

    // -------------------------------------------------------------------------
    // Consultas y helpers
    // -------------------------------------------------------------------------

    /**
     * Verifica si la entidad está en el contexto de persistencia. 
     * 
     * @param entity Entidad a verificar
     * @return true si está en el contexto
     */
    protected boolean contiene(T entity) {
        return persistenceContext.contains(entity);
    }

    /**
     * Ejecuta una consulta genérica con manejo de excepciones. 
     * 
     * @param <R> Tipo de resultado
     * @param query Lógica de consulta
     * @return Resultado de la consulta
     */
    protected <R> R ejecutarConsulta(ThrowingSupplier<R> query) {
        try {
            return query.get();
        } catch (Exception ex) {
            log.error("Error en consulta de {}", entityClass.getSimpleName(), ex);
            throw new SystemException("Error al ejecutar consulta.", ex);
        }
    }
}