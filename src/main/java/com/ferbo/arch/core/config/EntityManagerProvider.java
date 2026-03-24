package com.ferbo.arch.core.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Proveedor de EntityManager para la aplicación.
 * Esta clase es independiente del proveedor JPA.
 * Permite Obtener un EntityManager de manera centralizada
 * y cerrar la fabrica de EntityManager al final de la aplicación.
 */
public class EntityManagerProvider {

    // Fábrica de EntityManagers, única en la aplicación
    private static EntityManagerFactory emf;

    // Inicializa la fábrica con la unidad de persistencia definida en persistence.xml 
    public static void init(String persistenceUnitName) {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        }
    }

    /**
     * Obtiene un EntityManager nuevo
     * @return EntityManager
     */
    public static EntityManager gEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory no inicializada. LLama a init () primero.");
        }
        return emf.createEntityManager();
    }

    /**
     * Cierra la fábrica de EntityManagers
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    // Constructor privado para evitar instanciación
    private EntityManagerProvider() {}
}
