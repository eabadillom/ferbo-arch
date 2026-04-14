package com.ferbo.arch.persistence;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * BaseRepository
 *
 * PROPÓSITO:
 * - Proveer utilidades opcionales para operaciones básicas de persistencia (CRUD)
 * - Reducir duplicación de código en implementaciones de repositorios
 * - Servir como componente de reutilización de infraestructura sobre el PersistenceContext
 *
 * IMPORTANTE:
 * - Esta clase NO es obligatoria dentro de la arquitectura
 * - Su uso es opcional y basado en conveniencia del equipo
 * - No debe imponerse como base estructural de todos los repositorios
 *
 * RESPONSABILIDADES:
 * - Delegar operaciones básicas de persistencia al PersistenceContext
 * - Facilitar operaciones comunes como búsqueda por ID, guardado y eliminación
 *
 * NO ES RESPONSABLE DE:
 * - Manejo de transacciones (responsabilidad de UseCaseExecutor)
 * - Manejo de excepciones de negocio (responsabilidad de UseCaseExecutor)
 * - Lógica de dominio o reglas de negocio
 * - Conocimiento del framework de persistencia (Hibernate, JPA, Spring, etc.)
 *
 * SOBRE EL MANEJO DE ERRORES:
 * - Este repositorio NO captura excepciones
 * - Los errores de persistencia son propagados tal cual ocurren
 * - La clasificación y transformación de excepciones es responsabilidad de la capa de aplicación
 *   (UseCaseExecutor)
 *
 * DISEÑO:
 * - Es un componente técnico opcional
 * - Se recomienda composición. La herencia se mantiene solo como opción legacy o conveniencia controlada.
 * - Se recomienda preferir composición para mayor flexibilidad
 *
 * EJEMPLO DE USO:
 *
 * // Composición (recomendado)
 * class ClienteRepositoryImpl implements ClienteRepository {
 *     private final PersistenceContext ctx;
 *
 *     public ClienteRepositoryImpl(PersistenceContext ctx) {
 *         this.ctx = ctx;
 *     }
 *
 *     public Cliente save(Cliente c) {
 *         return ctx.save(c);
 *     }
 * }
 *
 * // O uso por extensión (opcional)
 * class ClienteRepositoryImpl extends BaseRepository<Cliente, Long> {
 *     ...
 * }
 *
 * REGLA DE ORO:
 * - BaseRepository es un acelerador, no una obligación arquitectónica
 * - La simplicidad y claridad del repositorio siempre tiene prioridad sobre su reutilización
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
        return persistenceContext.find(entityClass, id);
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

        return persistenceContext.save(entity); 
    }

    /**
     * Elimina una entidad.
     * 
     * @param entity Entidad a eliminar
     */
     public void delete(T entity) {

        persistenceContext.delete(entity);
    }
}