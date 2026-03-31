package com.ferbo.arch.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * BaseMGR: Clase base para la capa de presentación / manager.
 *
 * Responsabilidades:
 * - Recibir llamadas desde controladores o servicios
 * - Ejecutar operaciones de la capa de negocio (BaseUseCase)
 * - Capturar excepciones y construir OperationResult
 * - Registrar logs de la operación
 *
 * NOTA:
 * - Ya no depende de <T> genérico; si se necesita, se usa en implementaciones concretas
 * - BaseUseCase devuelve entidades puras, MGR construye resultados con ResultBuilder
 */
public abstract class BaseMGR {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Ejecuta una operación de negocio que devuelve resultado.
     *
     * @param operacion  Lógica a ejecutar (normalmente desde BaseUseCase)
     * @param descripcion Descripción de la operación para logging
     * @param <R> Tipo de resultado de la operación
     * @return OperationResult con datos, mensajes y estado
     */
    protected <R> OperationResult<R> ejecutarOperacion(
            ThrowingSupplier<R> operacion,
            String descripcion) {

        try {
            log.info("MGR ejecutando operación '{}'", descripcion);

            // Ejecuta la operación BL/UseCase
            R resultado = operacion.get();

            // Construye OperationResult exitoso
            return ResultBuilder.<R>success()
                    .data(resultado)
                    .message(MessageLevel.SUCCESS,
                             "Éxito",
                             descripcion + " completada correctamente")
                    .build();

        } catch (ValidationException ex) {
            log.warn("Validación fallida en '{}': {}", descripcion, ex.getMessage());
            return ResultBuilder.<R>failure()
                    .message(MessageLevel.WARNING,
                             "Validación",
                             ex.getMessage())
                    .build();

        } catch (RuleException ex) {
            log.warn("Regla de negocio no cumplida en '{}': {}", descripcion, ex.getMessage());
            return ResultBuilder.<R>failure()
                    .message(MessageLevel.INFO,
                             "Regla de negocio",
                             ex.getMessage())
                    .build();

        } catch (BusinessException ex) {
            log.error("Error de negocio en '{}': {}", descripcion, ex.getMessage(), ex);
            return ResultBuilder.<R>failure()
                    .message(MessageLevel.ERROR,
                             "Error de negocio",
                             ex.getMessage())
                    .build();

        } catch (SystemException ex) {
            log.error("Error de sistema en '{}'", descripcion, ex);
            return ResultBuilder.<R>failure()
                    .message(MessageLevel.ERROR,
                             "Error de sistema",
                             ex.getMessage())
                    .build();

        } catch (ToolException ex) {
            log.error("Error crítico de herramienta en '{}'", descripcion, ex);
            return ResultBuilder.<R>failure()
                    .message(MessageLevel.ERROR,
                             "Error de infraestructura",
                             ex.getMessage())
                    .build();

        } catch (Exception ex) {
            log.error("Error inesperado en '{}'", descripcion, ex);
            return ResultBuilder.<R>failure()
                    .message(MessageLevel.ERROR,
                             "Error inesperado",
                             ex.getMessage())
                    .build();
        }
    }

    /**
     * Ejecuta una operación de negocio que no devuelve resultado (void)
     *
     * @param operacion Lógica a ejecutar
     * @param descripcion Descripción de la operación para logging
     * @return OperationResult<Void> con mensajes y estado
     */
    protected OperationResult<Void> ejecutarOperacionVoid(
            ThrowingRunnable operacion,
            String descripcion) {

        // Reusa el método genérico, retornando null
        return ejecutarOperacion(() -> {
            operacion.run();
            return null;
        }, descripcion);
    }
}