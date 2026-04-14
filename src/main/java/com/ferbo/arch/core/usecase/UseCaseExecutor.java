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
 * UseCaseExecutor
 *
 * RESPONSABILIDAD:
 * - Ejecutar lógica de casos de uso de forma controlada
 * - Manejar transacciones de forma opcional
 * - Centralizar manejo de excepciones
 *
 * NOTA IMPORTANTE:
 * - Esta clase NO es base obligatoria
 * - Se usa por composición, no por herencia
 * - Los UseCases NO deben extender esta clase
 */
public class UseCaseExecutor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TransactionManager transactionManager;

    /**
     * Constructor base.
     *
     * @param transactionManager Gestor de transacciones
     */
    public UseCaseExecutor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // -----------------------------------------------------
    // EJECUCIÓN SIN TRANSACCIÓN
    // -----------------------------------------------------

    /**
     * Puede ejecutar una acción de negocio sin contexto transaccional.
     *
     * Usar cuando:
     * - Solo se realizan consultas
     * - No hay modificaciones en base de datos
     *
     * @param action Lógica a ejecutar
     * @param <R>    Tipo de retorno
     * @return Resultado de la ejecución
     */
    public <R> R execute(ThrowingSupplier<R> action)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        return executeInternal(action);
    }

    // -----------------------------------------------------
    // EJECUCIÓN CON TRANSACCIÓN
    // -----------------------------------------------------

     /**
     * Puede ejecutar una acción de negocio dentro de una transacción.
     *
     * Usar cuando:
     * - Se realizan operaciones de escritura (insert/update/delete)
     * - Se requiere atomicidad
     *
     * @param accion Lógica a ejecutar
     * @param <R>    Tipo de retorno
     * @return Resultado de la ejecución
     */
   
    public <R> R executeTx(ThrowingSupplier<R> action)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        return transactionManager.execute(() -> executeInternal(action));
    }

    /**
     * Puede ejecutar una acción de negocio dentro de una transacción sin retorno.
     *
     * @param action Lógica a ejecutar
     */
    public void executeTxVoid(ThrowingRunnable action)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        transactionManager.executeVoid(() -> executeInternalVoid(action));
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
    private <R> R executeInternal (ThrowingSupplier<R> action)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        try {
            return action.get();

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
    private void executeInternalVoid(ThrowingRunnable action)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        executeInternal(() -> {
            action.run();
            return null;
        });
    }

}