package com.ferbo.arch.example.cliente.mapper;

import com.ferbo.arch.example.cliente.domain.Cliente;
import com.ferbo.arch.example.cliente.dto.ClienteDTO;
import com.ferbo.arch.mapper.BaseMapper;

/**
 * ClienteMapper
 *
 * PROPÓSITO:
 * - Convertir entre Cliente (dominio) y ClienteDTO (presentación)
 * - Centralizar la lógica de transformación
 *
 * PRINCIPIOS:
 * - Sin lógica de negocio
 * - Conversión explícita (no automática)
 * - Fácil de mantener y testear
 */
public class ClienteMapper implements BaseMapper<Cliente, ClienteDTO> {

    /**
     * Convierte entidad de dominio a DTO.
     *
     * @param entity Cliente del dominio
     * @return ClienteDTO equivalente o null si entity es null
     */
    @Override
    public ClienteDTO toDto(Cliente entity) {
        if (entity == null) {
            return null;
        }

        return new ClienteDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getActivo()
        );
    }

    /**
     * Convierte DTO a entidad de dominio.
     *
     * @param dto ClienteDTO
     * @return Cliente equivalente o null si dto es null
     */
    @Override
    public Cliente toEntity(ClienteDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Cliente(
                dto.getId(),
                dto.getNombre(),
                dto.getActivo()
        );
    }
}