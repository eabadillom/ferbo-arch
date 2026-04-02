package com.ferbo.arch.example.cliente.dto;

/**
 * ClienteDTO
 *
 * PROPÓSITO:
 * - Transportar datos del Cliente entre capas (presentation ↔ application)
 * - Evitar exponer directamente la entidad de dominio
 *
 * PRINCIPIOS:
 * - No contiene lógica de negocio
 * - Es un objeto plano (POJO)
 * - Puede ser serializado (JSON, XML, etc.)
 */
public class ClienteDTO {

    private Long id;
    private String nombre;
    private Boolean activo;

    /**
     * Constructor vacío
     *
     * NECESARIO PARA:
     * - Serialización (JSON, XML)
     * - Frameworks de presentación
     */
    public ClienteDTO() {
    }

    /**
     * Constructor completo
     *
     * @param id Identificador del cliente
     * @param nombre Nombre del cliente
     * @param activo Estado del cliente
     */
    public ClienteDTO(Long id, String nombre, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
    }

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS (DTO sí permite setters)
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ClienteDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}