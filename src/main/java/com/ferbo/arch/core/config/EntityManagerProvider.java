package com.ferbo.arch.core.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.ferbo.tools.exception.SystemException;

/**
 * Proveedor de EntityManager para la aplicación.
 * Totalmente desacoplado de cualquier framework o implementación concreta.
 * Permite obtener EntityManager y cerrar la fábrica al final de la aplicación.
 */
public class EntityManagerProvider {

    // Fábrica de EntityManagers, única en la aplicación
    private static EntityManagerFactory emf;

    /**
     * Inicializa la fábrica de EntityManagers con la unidad de persistencia
     * indicada.
     * Debe llamarse al inicio de la aplicación.
     *
     * @param persistenceUnitName nombre de la unidad de persistencia definida en
     *                            persistence.xml
     */
    public static synchronized void init(String persistenceUnitName) {
        if (emf == null) {
            try {
                emf = Persistence.createEntityManagerFactory(persistenceUnitName);
            } catch (Exception ex) {
                throw new SystemException("No se pudo inicializar EntityManagerFactory con " + persistenceUnitName, ex);
            }
        }
    }

    /**
     * Proporciona un EntityManager nuevo.
     * Cada llamada devuelve un EntityManager independiente.
     *
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new SystemException("EntityManagerFactory no inicializada. Llama a init() primero.");
        }
        return emf.createEntityManager();
    }

    /**
     * Cierra la fábrica de EntityManagers al finalizar la aplicación.
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    // Evita instanciación
    private EntityManagerProvider() {
    }
}