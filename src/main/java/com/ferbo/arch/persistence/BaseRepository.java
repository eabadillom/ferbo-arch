package com.ferbo.arch.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.tools.exception.SystemException;
import com.ferbo.tools.functional.ThrowingConsumer;
import com.ferbo.tools.functional.ThrowingFunction;


/**
 * BaseRepository: Repositorio genérico para cualquier entidad Identifiable<ID>.
 *
 * Responsabilidades:
 * - Ejecutar operaciones sobre EntityManager
 * - Manejar transacciones y excepciones de manera centralizada
 * - Proveer métodos CRUD y de consulta reutilizables
 *
 * @param <T>  Tipo de entidad
 * @param <ID> Tipo de ID
 */
/**
 * BaseRepository: Repositorio genérico para cualquier entidad Identifiable<ID>.
 *
 * Responsabilidad:
 * - Ejecutar operaciones sobre EntityManager
 * - Manejar transacciones y excepciones de manera centralizada
 * - Proveer métodos CRUD y de consulta reutilizables
 *
 * @param <T>  Tipo de entidad
 * @param <ID> Tipo de ID
 */
public abstract class BaseRepository<T, ID> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Class<T> modelClass;
    protected final ThrowingFunction<Void, EntityManager> entityManagerSupplier;

    protected BaseRepository(Class<T> modelClass, ThrowingFunction<Void, EntityManager> entityManagerSupplier) {
        this.modelClass = modelClass;
        this.entityManagerSupplier = entityManagerSupplier;
    }

    // --------------------------
    // CRUD
    // --------------------------

    public Optional<T> buscarPorId(ID id) {
        return ejecutarConsulta(em -> Optional.ofNullable(em.find(modelClass, id)));
    }

    public T guardar(T entity) {
        ejecutarTransaccionVoid(em -> em.persist(entity), "guardar");
        return entity;
    }

    public T actualizar(T entity) {
        return ejecutarTransaccion(em -> em.merge(entity), entity, "actualizar");
    }

    public void eliminar(T entity) {
        ejecutarTransaccionVoid(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)), "eliminar");
    }

    // --------------------------
    // Transacciones
    // --------------------------

    protected T ejecutarTransaccion(ThrowingFunction<EntityManager, ?> accion, T entity, String operacion) {
        EntityManager em = obtenerEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            accion.apply(em);
            tx.commit();
            log.info("Entidad {} {} correctamente", entity, operacion);
            return entity;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            log.error("Error al {} entidad {}", operacion, entity, ex);
            throw new SystemException("Error al " + operacion + " la entidad.", ex);
        } finally {
            cerrarEntityManager(em);
        }
    }

    protected void ejecutarTransaccionVoid(ThrowingConsumer<EntityManager> accion, String operacion) {
        EntityManager em = obtenerEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            accion.accept(em);
            tx.commit();
            log.info("Operación '{}' completada correctamente", operacion);
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            log.error("Error en operación '{}'", operacion, ex);
            throw new SystemException("Error al " + operacion, ex);
        } finally {
            cerrarEntityManager(em);
        }
    }

    // --------------------------
    // Consultas
    // --------------------------

    protected <R> R ejecutarConsulta(ThrowingFunction<EntityManager, R> query) {
        EntityManager em = obtenerEntityManager();
        try {
            return query.apply(em);
        } catch (NoResultException ex) {
            return null;
        } catch (NonUniqueResultException ex) {
            log.error("Se esperaba un único resultado en {}", modelClass.getSimpleName(), ex);
            throw new SystemException("La consulta retornó múltiples resultados.", ex);
        } catch (Exception ex) {
            log.error("Error en consulta en {}", modelClass.getSimpleName(), ex);
            throw new SystemException("Error al ejecutar consulta.", ex);
        } finally {
            cerrarEntityManager(em);
        }
    }

    protected ResultadoPaginado<T> ejecutarPaginado(
            ThrowingFunction<EntityManager, List<T>> dataQuery,
            ThrowingFunction<EntityManager, Long> countQuery,
            int pagina,
            int tamanio) {

        EntityManager em = obtenerEntityManager();
        try {
            List<T> resultados = dataQuery.apply(em);
            Long total = countQuery.apply(em);
            return new ResultadoPaginado<>(resultados, (total != null) ? total : 0L, pagina, tamanio);
        } catch (Exception ex) {
            log.error("Error en paginación en {}", modelClass.getSimpleName(), ex);
            throw new SystemException("Error en paginación.", ex);
        } finally {
            cerrarEntityManager(em);
        }
    }

    // --------------------------
    // Helpers
    // --------------------------

    protected EntityManager obtenerEntityManager() {
        try {
            return entityManagerSupplier.apply(null);
        } catch (Exception ex) {
            throw new SystemException("No se pudo obtener EntityManager.", ex);
        }
    }

    protected void cerrarEntityManager(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}