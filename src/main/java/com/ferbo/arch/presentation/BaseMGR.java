package com.ferbo.arch.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferbo.arch.application.BaseBL;
import com.ferbo.tools.domain.Identifiable;
import com.ferbo.tools.exception.SystemException;
import com.ferbo.tools.exception.ToolException;
import com.ferbo.tools.functional.ThrowingSupplier;
import com.ferbo.tools.result.OperationResult;

/**
 * BaseMGR: Clase base para la capa de presentación / manager.
 *
 * Responsabilidades:
 * - Orquestar llamadas a la capa de negocio (BL)
 * - Delegar ejecución de operaciones
 * - Servir como punto de entrada para controladores o servicios
 *
 * @param <T> Tipo de entidad
 */
public abstract class BaseMGR<T extends Identifiable<?>> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final BaseBL<T> bl;

    /**
     * Constructor que recibe la lógica de negocio asociada
     */
    protected BaseMGR(BaseBL<T> bl) {
        this.bl = bl;
    }

    // --------------------------
    // Métodos helper
    // --------------------------

    /**
     * Ejecuta una operación que devuelve resultado
     */
    protected <R> OperationResult<R> ejecutarOperacion(
            ThrowingSupplier<OperationResult<R>> operacion,
            String descripcion) throws SystemException, ToolException {

        try {
            log.info("MGR ejecutando operación '{}'", descripcion);
            return operacion.get();
        } catch (ToolException ex) {
            log.error("Error crítico en MGR '{}'", descripcion, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado en MGR '{}'", descripcion, ex);
            throw new SystemException("Error inesperado en MGR al " + descripcion, ex);
        }
    }

    /**
     * Ejecuta una operación sin retorno (void)
     */
    protected OperationResult<Void> ejecutarOperacionVoid(
            ThrowingSupplier<OperationResult<Void>> operacion,
            String descripcion) throws SystemException, ToolException {

        return ejecutarOperacion(operacion, descripcion);
    }
}