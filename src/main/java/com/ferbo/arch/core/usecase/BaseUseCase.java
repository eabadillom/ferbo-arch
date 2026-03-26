package com.ferbo.arch.core.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.arch.persistence.TransactionManager;
import com.ferbo.tools.exception.BusinessException;
import com.ferbo.tools.exception.RuleException;
import com.ferbo.tools.exception.SystemException;
import com.ferbo.tools.exception.ToolException;
import com.ferbo.tools.exception.ValidationException;
import com.ferbo.tools.functional.ThrowingRunnable;
import com.ferbo.tools.functional.ThrowingSupplier;

/**
 * BaseUseCase: Clase base para la ejecución de casos de uso.
 *
 * Propósito:
 * - Centralizar la ejecución de lógica de negocio
 * - Manejar de forma uniforme las excepciones
 * - Integrar el manejo de transacciones mediante TransactionManager
 *
 * Responsabilidades:
 * - Ejecutar lógica de negocio (casos de uso)
 * - Manejar logging consistente
 * - Controlar ejecución transaccional cuando sea necesario
 *
 * NO se encarga de:
 * - Acceso directo a datos (eso es responsabilidad de los repositories)
 * - Construcción de respuestas para UI (DTOs, responses, etc.)
 * - Manejo de frameworks (JSF, REST, etc.)
 *
 * IMPORTANTE:
 * - Las transacciones se definen aquí, no en los repositories
 * - Permite agrupar múltiples operaciones en una sola transacción
 */
public abstract class BaseUseCase<T> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final TransactionManager transactionManager;

    /**
     * Constructor base.
     *
     * @param transactionManager Gestor de transacciones
     */
    protected BaseUseCase(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // -------------------------------------------------------------------------
    // Ejecución SIN transacción
    // -------------------------------------------------------------------------

    /**
     * Ejecuta una acción de negocio sin contexto transaccional.
     *
     * Usar cuando:
     * - Solo se realizan consultas
     * - No hay modificaciones en base de datos
     *
     * @param accion Lógica a ejecutar
     * @param <R>    Tipo de retorno
     * @return Resultado de la ejecución
     */
    protected <R> R ejecutar(ThrowingSupplier<R> accion)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        return ejecutarInterno(accion);
    }

    // -------------------------------------------------------------------------
    // Ejecución CON transacción
    // -------------------------------------------------------------------------

    /**
     * Ejecuta una acción de negocio dentro de una transacción.
     *
     * Usar cuando:
     * - Se realizan operaciones de escritura (insert/update/delete)
     * - Se requiere atomicidad
     *
     * @param accion Lógica a ejecutar
     * @param <R>    Tipo de retorno
     * @return Resultado de la ejecución
     */
    protected <R> R ejecutarTx(ThrowingSupplier<R> accion)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        return transactionManager.execute(() -> ejecutarInterno(accion));
    }

    /**
     * Ejecuta una acción de negocio dentro de una transacción sin retorno.
     *
     * @param accion Lógica a ejecutar
     */
    protected void ejecutarTxVoid(ThrowingRunnable accion)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        transactionManager.executeVoid(() -> ejecutarInternoVoid(accion));
    }

    // -------------------------------------------------------------------------
    // Métodos internos (manejo centralizado de excepciones)
    // -------------------------------------------------------------------------

    /**
     * Método interno que ejecuta la lógica y maneja excepciones de forma uniforme.
     *
     * @param accion Lógica a ejecutar
     * @param <R>    Tipo de retorno
     * @return Resultado de la ejecución
     */
    private <R> R ejecutarInterno(ThrowingSupplier<R> accion)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        try {
            return accion.get();

        } catch (ValidationException ex) {
            log.warn("[{}] Validación fallida: {}", getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;

        } catch (RuleException ex) {
            log.warn("[{}] Regla de negocio no cumplida: {}", getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;

        } catch (BusinessException ex) {
            log.warn("[{}] Error de negocio: {}", getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;

        } catch (SystemException ex) {
            log.error("[{}] Error de sistema", getClass().getSimpleName(), ex);
            throw ex;

        } catch (ToolException ex) {
            log.error("[{}] Error de herramienta", getClass().getSimpleName(), ex);
            throw ex;

        } catch (Exception ex) {
            log.error("[{}] Error inesperado en caso de uso", getClass().getSimpleName(), ex);
            throw new SystemException("Error inesperado en caso de uso", ex);
        }
    }

    /**
     * Variante sin retorno del método interno.
     *
     * @param accion Lógica a ejecutar
     */
    private void ejecutarInternoVoid(ThrowingRunnable accion)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        ejecutarInterno(() -> {
            accion.run();
            return null;
        });
    }

}