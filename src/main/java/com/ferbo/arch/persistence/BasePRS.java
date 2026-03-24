package com.ferbo.arch.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.tools.domain.Identifiable;
import com.ferbo.tools.exception.SystemException;
import com.ferbo.tools.functional.ThrowingConsumer;

/**
 * BasePRS: DAO genérico para cualquier entidad Identificable<ID>.
 *
 * Propósito:
 * - Proveer operaciones básicas (CRUD) genéricas.
 * - Manejar transacciones, logging y excepciones de manera centralizada.
 *
 * @param <T>  Tipo de entidad que implementa Identifiable<ID>
 * @param <ID> Tipo de ID de la entidad (Long, Integer, String, etc.)
 */
public abstract class BasePRS<T extends Identifiable<ID>, ID> {

    protected static final Logger log = LoggerFactory.getLogger(BasePRS.class);

    protected Class<T> modelClass;

    public BasePRS(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    /**
     * Cada DAO concreto implementa cómo obtener un EntityManager válido.
     *
     * @return EntityManager
     * @throws SystemException si no se puede obtener
     */
    protected abstract EntityManager getEntityManager() throws SystemException;

    /**
     * Busca una entidad por su ID.
     * Devuelve Optional.empty() si no se encuentra.
     *
     * @param id ID de la entidad a buscar
     * @return Optional con la entidad o vacío si no existe
     * @throws SystemException si ocurre un error inesperado
     */
    public Optional<T> buscarPorId(ID id) throws SystemException {
        try {
            EntityManager em = getEntityManager();
            T entity = em.find(modelClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception ex) {
            log.error("Error al buscar {} con ID {}", modelClass.getSimpleName(), id, ex);
            throw new SystemException("Error al buscar entidad por ID.", ex);
        }
    }

    /**
     * Guarda una entidad en la base de datos.
     *
     * @param entity Entidad a guardar
     * @return la entidad guardada
     * @throws SystemException si ocurre un error
     */
    public T guardar(T entity) throws SystemException {
        return ejecutarTransaccion(em -> em.persist(entity), entity, "guardar");
    }

    /**
     * Actualiza una entidad existente.
     *
     * @param entity Entidad a actualizar
     * @return la entidad actualizada
     * @throws SystemException si ocurre un error
     */
    public T actualizar(T entity) throws SystemException {
        return ejecutarTransaccion(em -> em.merge(entity), entity, "actualizar");
    }

    /**
     * Elimina una entidad de la base de datos.
     *
     * @param entity Entidad a eliminar
     * @throws SystemException si ocurre un error
     */
    public void eliminar(T entity) throws SystemException {
        ejecutarTransaccion(
                em -> em.remove(em.contains(entity) ? entity : em.merge(entity)),
                entity,
                "eliminar");
    }

    /**
     * Ejecuta cualquier operación sobre EntityManager dentro de una transacción.
     *
     * @param accion    Lambda con la operación a ejecutar sobre EntityManager
     * @param entity    Entidad para logging
     * @param operacion Nombre de operación (guardar, actualizar, eliminar)
     * @return la entidad procesada (para persist y merge)
     * @throws SystemException si ocurre un error
     */
    private T ejecutarTransaccion(ThrowingConsumer<EntityManager> accion, T entity, String operacion)
            throws SystemException {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            accion.accept(em);
            tx.commit();
            log.info("Entidad {} {} correctamente", entity, operacion);
            return entity;
        } catch (Exception ex) {
            if (tx.isActive())
                tx.rollback();
            log.error("Error al {} entidad {}", operacion, entity, ex);
            throw new SystemException("Error al " + operacion + " la entidad.", ex);
        } finally {
            if (em.isOpen())
                em.close();
        }
    }
}