package com.ferbo.arch.core.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.tools.exception.BusinessException;
import com.ferbo.tools.exception.RuleException;
import com.ferbo.tools.exception.SystemException;
import com.ferbo.tools.exception.ToolException;
import com.ferbo.tools.exception.ValidationException;
import com.ferbo.tools.functional.ThrowingRunnable;
import com.ferbo.tools.functional.ThrowingSupplier;

/**
 * BaseUseCase: clase base para lógica de negocio.
 *
 * Responsabilidades:
 * - Ejecutar operaciones de negocio
 * - Centralizar logging
 * - Propagar excepciones correctamente según su tipo
 *
 * NO se encarga de:
 * - Construir respuestas (OperationResult)
 * - Manejo de presentación
 *
 * @param <T> Tipo de entidad (opcional)
 */
public abstract class BaseUseCase<T> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    // --------------------------
    // Ejecución con retorno
    // --------------------------
    protected <R> R ejecutar(ThrowingSupplier<R> accion)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        try {
            return accion.get();

        } catch (ValidationException ex) {
            log.warn("Validación fallida: {}", ex.getMessage(), ex);
            throw ex;

        } catch (RuleException ex) {
            log.warn("Regla de negocio no cumplida: {}", ex.getMessage(), ex);
            throw ex;

        } catch (BusinessException ex) {
            log.warn("Error de negocio: {}", ex.getMessage(), ex);
            throw ex;

        } catch (SystemException ex) {
            log.error("Error de sistema", ex);
            throw ex;

        } catch (ToolException ex) {
            log.error("Error de herramienta", ex);
            throw ex;

        } catch (Exception ex) {
            log.error("Error inesperado en caso de uso", ex);
            throw new SystemException("Error inesperado en caso de uso", ex);
        }
    }

    // --------------------------
    // Ejecución sin retorno (void)
    // --------------------------
    protected void ejecutarVoid(ThrowingRunnable accion)
            throws ValidationException, RuleException, BusinessException, SystemException, ToolException {

        try {
            accion.run();

        } catch (ValidationException ex) {
            log.warn("Validación fallida: {}", ex.getMessage(), ex);
            throw ex;

        } catch (RuleException ex) {
            log.warn("Regla de negocio no cumplida: {}", ex.getMessage(), ex);
            throw ex;

        } catch (BusinessException ex) {
            log.warn("Error de negocio: {}", ex.getMessage(), ex);
            throw ex;

        } catch (SystemException ex) {
            log.error("Error de sistema", ex);
            throw ex;

        } catch (ToolException ex) {
            log.error("Error de herramienta", ex);
            throw ex;

        } catch (Exception ex) {
            log.error("Error inesperado en caso de uso", ex);
            throw new SystemException("Error inesperado en caso de uso", ex);
        }
    }
}