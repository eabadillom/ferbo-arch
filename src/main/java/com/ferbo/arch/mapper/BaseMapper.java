package com.ferbo.arch.mapper;

/**
 * BaseMapper: Contrato base para el mapeo entre entidades y DTOs.
 *
 * Propósito:
 * - Estandarizar la conversión entre capas (domain ↔ presentation)
 * - Evitar lógica de transformación duplicada
 * - Facilitar mantenimiento y pruebas
 *
 * NOTAS:
 * - No depende de frameworks (MapStruct, ModelMapper, etc.)
 * - Se implementa manualmente en cada módulo
 * - Permite personalización total por caso de uso
 *
 * @param <E> Tipo de entidad (domain)
 * @param <D> Tipo de DTO (data transfer object)
 */
public interface BaseMapper<E, D> {

    /**
     * Convierte una entidad a DTO.
     *
     * @param entity entidad de dominio
     * @return DTO equivalente o null si la entidad es null
     */
    D toDto(E entity);

    /**
     * Convierte un DTO a entidad.
     *
     * @param dto objeto DTO
     * @return entidad equivalente o null si el DTO es null
     */
    E toEntity(D dto);
}
