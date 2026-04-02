package com.ferbo.arch.example.cliente.domain;

import com.ferbo.tools.exception.BusinessException;
import com.ferbo.tools.exception.ValidationException;

/**
 * Cliente
 *
 * PROPÓSITO:
 * - Representar un cliente dentro del dominio del sistema
 * - Encapsular estado y reglas de negocio relacionadas al cliente
 *
 * PRINCIPIOS:
 * - No depende de frameworks
 * - Contiene lógica de negocio (no es un simple POJO)
 * - Protege su estado mediante métodos controlados
 */
public class Cliente {

    private Long id;
    private String nombre;
    private Boolean activo;

    /**
     * Constructor vacío
     *
     * NECESARIO PARA:
     * - Herramientas de serialización (futuro)
     * - Mocks / testing
     */
    public Cliente() {
    }

    /**
     * Constructor completo
     *
     * @param id Identificador del cliente
     * @param nombre Nombre del cliente
     * @param activo Indica si el cliente está activo
     */
    public Cliente(Long id, String nombre, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
    }

    // -------------------------------------------------------------------------
    // GETTERS (no exponemos setters para proteger el dominio)
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE NEGOCIO
    // -------------------------------------------------------------------------

    /**
     * Regla de dominio:
     * El cliente debe estar activo para poder operar.
     *
     * @throws BusinessException si el cliente está inactivo
     */
    public void validarActivo() {
        if (activo == null || !activo) {
            throw new BusinessException("El cliente no está activo");
        }
    }

    /**
     * Activa el cliente.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el cliente.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Cambia el nombre del cliente.
     *
     * @param nuevoNombre Nuevo nombre
     */
    public void cambiarNombre(String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new ValidationException("El nombre del cliente es obligatorio");
        }
        this.nombre = nuevoNombre;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE INFRAESTRUCTURA (igualdad, debug, etc.)
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}