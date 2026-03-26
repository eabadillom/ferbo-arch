package com.ferbo.arch.persistence;

import com.ferbo.tools.functional.ThrowingRunnable;
import com.ferbo.tools.functional.ThrowingSupplier;

/**
 * TransactionManager: Abstracción para la gestion de transacciones.
 * 
 * Proposito:
 * - Centralizar el manejo de transacciones en la capa de aplicación (UseCase)
 * - Desacoplar la lógica de negocio de tecnologías especificas (JPA, JDBC, etc.)
 * 
 * Permite:
 * - Ejecutar bloques de código dentro de una transacción
 * - Garantizar atomicidad (todo o nada)
 * - Manejar commit y rollback de forma consistente
 * 
 * IMPORTANTE:
 * - No debe contener lógics de negocio
 * - No debe depender directamente de frameworks (eso lo hacen las implementaciones)
 * - Debe ser utilizado únicamente desde los UseCase
 * 
 * Implementaciones tipicas:
 * - JpaTransactionManager
 * - SpringTransactionManager (futuro)
 * - MockTransactionManager (testing)
 */
public interface TransactionManager {

    /**
     * Ejecuta una acción dentro de una transacción y retorna un resultado
     * 
     * Flujo esperado:
     * 1. Inicia la transacción
     * 2. Ejecuta la acción
     * 3. Si todo es correcto -> commit 
     * 4. Si ocurre error -> rollback
     * 
     * @param accion Bloque de código a ejecutar
     * @param <R> Tipo de retorno
     * @return Resultado de la ejecución
     */
    <R> R execute(ThrowingSupplier<R> action);

    /**
     * Ejecuta una acción dentro de una transacción sin retorno.
     * 
     * Internamente puede reutilizar el método con retorno.
     * 
     * @param action Bloque de código a ejecutar
     */
    void executeVoid(ThrowingRunnable accion);


}
