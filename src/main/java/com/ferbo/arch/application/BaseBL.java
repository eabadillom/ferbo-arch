package com.ferbo.arch.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.tools.domain.Identifiable;
import com.ferbo.tools.exception.BusinessException;
import com.ferbo.tools.exception.RuleException;
import com.ferbo.tools.exception.SystemException;
import com.ferbo.tools.exception.ToolException;
import com.ferbo.tools.exception.ValidationException;
import com.ferbo.tools.functional.ThrowingRunnable;
import com.ferbo.tools.functional.ThrowingSupplier;
import com.ferbo.tools.result.MessageLevel;
import com.ferbo.tools.result.OperationResult;
import com.ferbo.tools.result.ResultBuilder;

/**
 * BaseBL: Clase abstracta para la capa de lógica de negocio.
 *
 * Responsabilidades:
 * - Ejecutar operaciones de negocio
 * - Manejar excepciones de forma centralizada
 * - Traducir excepciones a OperationResult
 * - Definir semántica de mensajes (SUCCESS, WARNING, ERROR)
 *
 * @param <T> Tipo de entidad
 */
public abstract class BaseBL<T extends Identifiable<?>> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    // --------------------------
    // Métodos base
    // --------------------------

    protected abstract T nuevo();

    protected T nuevoOExistente(T entity) {
        return (entity == null || entity.getId() == null) ? nuevo() : entity;
    }

    // --------------------------
    // Operaciones principales
    // --------------------------

    protected <R> OperationResult<R> operar(ThrowingSupplier<R> proveedor, String descripcion)
            throws SystemException, ToolException {

        try {
            R resultado = proveedor.get();

            log.info("Operación '{}' completada correctamente.", descripcion);

            return ResultBuilder.<R>success()
                    .message(MessageLevel.INFO, "Éxito", "Se completó correctamente " + descripcion)
                    .data(resultado)
                    .affectedCount(1)
                    .build();

        } catch (Exception ex) {
            return manejarExcepcion(ex, descripcion);
        }
    }

    protected OperationResult<Void> ejecutar(ThrowingRunnable operacion, String descripcion)
            throws SystemException, ToolException {

        try {
            operacion.run();

            log.info("Operación '{}' completada correctamente.", descripcion);

            return ResultBuilder.<Void>success()
                    .message(MessageLevel.INFO, "Éxito", "Se completó correctamente " + descripcion)
                    .affectedCount(1)
                    .build();

        } catch (Exception ex) {
            return manejarExcepcion(ex, descripcion);
        }
    }

    // --------------------------
    // Manejo centralizado de excepciones
    // --------------------------

    private <R> OperationResult<R> manejarExcepcion(Exception ex, String descripcion)
            throws SystemException, ToolException {

        // ---------------- VALIDATION → ERROR ----------------
        if (ex instanceof ValidationException) {
            ValidationException vex = (ValidationException) ex;

            log.warn("Validación fallida en '{}': {}", descripcion, vex.getMessage());

            return ResultBuilder.<R>failure()
                    .message(MessageLevel.ERROR,
                            "Error de validación",
                            "Se encontraron errores al " + descripcion)
                    .build();
        }

        // ---------------- RULE → WARN ----------------
        if (ex instanceof RuleException) {
            log.warn("Regla de negocio no cumplida en '{}': {}", descripcion, ex.getMessage());

            return ResultBuilder.<R>failure()
                    .message(MessageLevel.WARNING,
                            "Regla de negocio",
                            ex.getMessage())
                    .build();
        }

        // ---------------- BUSINESS → WARN ----------------
        if (ex instanceof BusinessException) {
            log.warn("Error de negocio en '{}': {}", descripcion, ex.getMessage());

            return ResultBuilder.<R>failure()
                    .message(MessageLevel.WARNING,
                            "Error de negocio",
                            ex.getMessage())
                    .build();
        }

        // ---------------- SYSTEM → CRÍTICO ----------------
        if (ex instanceof SystemException) {
            log.error("Error de sistema en '{}'", descripcion, ex);
            throw (SystemException) ex;
        }

        // ---------------- TOOL → CRÍTICO ----------------
        if (ex instanceof ToolException) {
            log.error("Error de herramienta en '{}'", descripcion, ex);
            throw (ToolException) ex;
        }

        // ---------------- DESCONOCIDO → ENVOLVER ----------------
        log.error("Error inesperado en '{}'", descripcion, ex);
        throw new SystemException("Error inesperado al " + descripcion, ex);
    }
}