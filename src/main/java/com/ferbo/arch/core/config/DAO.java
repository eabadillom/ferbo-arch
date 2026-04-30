package com.ferbo.arch.core.config;

import java.util.Optional;

/**
 * Respository: Abstracción del contexto de persistencia.
 *
 * PROPÓSITO:
 * - Desacoplar la lógica de negocio de cualquier framework de persistencia (JPA, JDBC, Hibernate, etc.)
 * - Proveer operaciones básicas de acceso a datos de forma agnóstica
 * - Facilitar testing, permitiendo mocks y reemplazos de implementación
 *
 * RESPONSABILIDADES:
 * - Buscar, guardar, actualizar y eliminar entidades
 * - Verificar si una entidad está siendo gestionada
 *
 * NO SE ENCARGA DE:
 * - Lógica de negocio
 * - Manejo de transacciones
 * - Exponer frameworks específicos (EntityManager, Session, etc.)
 *
 * IMPLEMENTACIONES TÍPICAS:
 * - JPARespository (con JPA)
 * - JdbcRespository (con JDBC puro, futuro)
 * - MockRespository (testing)
 */
public interface DAO <T, ID> {

    /**
     * Busca una entidad por su identificador.
     *
     * @param clazz Clase de la entidad
     * @param id Identificador de la entidad
     * @param <T> Tipo de la entidad
     * @return Optional con la entidad encontrada, o vacío si no existe
     */
    Optional<T> find(ID id);

    /**
     * Persiste una nueva entidad o actualiza una existente en el contexto.
     *
     * @param entity Entidad a guardar o actualizar
     * @param <T> Tipo de la entidad
     * @return La entidad gestionada después de la operación
     */
    T save(T entity);

    /**
     * Elimina una entidad del contexto.
     *
     * @param entity Entidad a eliminar
     * @param <T> Tipo de la entidad
     */
    void delete(T entity);

    /**
     * Verifica si una entidad está siendo gestionada por el contexto actual.
     *
     * @param entity Entidad a verificar
     * @param <T> Tipo de la entidad
     * @return true si está en el contexto, false en caso contrario
     */
    boolean contains(T entity);
}