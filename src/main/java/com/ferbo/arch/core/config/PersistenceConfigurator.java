package com.ferbo.arch.core.config;

import javax.persistence.EntityManager;

import com.ferbo.tools.functional.ThrowingSupplier;

/**
 * Contrato que obliga a lsos proyectos a definir
 * cómo configurar y proveer EntityManager.
 */
public interface PersistenceConfigurator {

    /**
     * Inicializada cualquier recurso de persistencia necesario
     * (EntityManagerFactory, conexión, etc.)
     */
    void init();

    /**
     * Provee un entityManager para ser usado por los repositorios.
     * @return ThrowingSupplier que entraga un EnitityManager
     */
    ThrowingSupplier<EntityManager> entityManagerSupplier();

    /**
     * Libera cualquier recurso de persistencia al finalizar lá aplicación
     */
    void shutdown();
}
