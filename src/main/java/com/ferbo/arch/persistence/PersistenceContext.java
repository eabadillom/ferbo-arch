package com.ferbo.arch.persistence;

/**
 * PersistenceContext: Abstracción del contexto de persistencia.
 * 
 * Proposito: 
 * - Desacoplar la lógica de negocio del framework de persistencia (JPA, Hibernate, etc.)
 * - Proveer operaciones básicas de acceso a datos de forma agnóstica
 * 
 * Este contrato permite que el sistema:
 * - Cambie de tecnología de persistencia sin afectar el dominio
 * - Sea fácilmente testeable (mockeable)
 * 
 * IMPORTANTE: 
 * - No debe contener lógica de negocio
 * - No debe manejar transacciones
 * - No debe exponer clases especificas como EntityManager
 * 
 * Implementaciones típicas:
 * - JPAPersistenceContext
 * - JdbcPersistenceContet (futuro)
 * - MockPersistenceContext (testing)
 */
public interface PersistenceContext {

    /**
     * Busca una entidad por su identificador.
     *
     * @param clazz Clase de la entidad
     * @param id Identificador de la entidad
     * @param <T> Tipo de la entidad
     * @return Entidad encontrada o null si no existe
     */
    <T> T find(Class<T> clazz, Object id);

    /**
     * Persiste una nueva entidad en el contexto.
     * 
     * @param entity Entidad a persistir
     */
    void persist(Object Entity);

    /**
     * Actualuiza el estado de una entidad en el contexto.
     * 
     * @param entity Entidad a actualizar
     * @param <T> Tipo de la entidad
     * @return Entidad gestionada actualizada
     */
    <T> T merge(T entity);

    /**
     * Elimina una entidad del contexto.
     * 
     * @param entity Entidad a eliminar
     */
    void remove(Object entity);

    /**
     * Verifica si una entidad está siendo gestionada por contexto actual.
     * 
     * @param entity Entidad a verificar
     * @return true si está en el contexto, false en caso contrario
     */
    boolean contains(Object entity);
}
