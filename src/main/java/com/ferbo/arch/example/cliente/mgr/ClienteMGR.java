package com.ferbo.arch.example.cliente.mgr;

import java.util.List;
import java.util.stream.Collectors;

import com.ferbo.arch.example.cliente.domain.Cliente;
import com.ferbo.arch.example.cliente.dto.ClienteDTO;
import com.ferbo.arch.example.cliente.mapper.ClienteMapper;
import com.ferbo.arch.example.cliente.usecase.ClienteUseCase;
import com.ferbo.arch.presentation.BaseMGR;
import com.ferbo.tools.result.OperationResult;

/**
 * ClienteMGR
 *
 * PROPÓSITO:
 * - Servir como capa de entrada para operaciones de Cliente
 * - Adaptar DTOs hacia el dominio y viceversa
 * - Manejar respuestas estandarizadas (OperationResult)
 *
 * RESPONSABILIDADES:
 * - Convertir DTO → Entity
 * - Invocar UseCase
 * - Convertir Entity → DTO
 * - Delegar manejo de errores a BaseMGR
 *
 * NO se encarga de:
 * - Lógica de negocio
 * - Persistencia
 * - Manejo de transacciones
 */
public class ClienteMGR extends BaseMGR {

    private final ClienteUseCase clienteUseCase;
    private final ClienteMapper clienteMapper;

    /**
     * Constructor
     *
     * @param clienteUseCase caso de uso
     * @param clienteMapper mapper DTO ↔ dominio
     */
    public ClienteMGR(ClienteUseCase clienteUseCase,
                      ClienteMapper clienteMapper) {
        this.clienteUseCase = clienteUseCase;
        this.clienteMapper = clienteMapper;
    }

    // -------------------------------------------------------------------------
    // OPERACIONES
    // -------------------------------------------------------------------------

    /**
     * Crea un nuevo cliente.
     *
     * @param dto datos del cliente
     * @return resultado de la operación
     */
    public OperationResult<ClienteDTO> crearCliente(ClienteDTO dto) {

        return ejecutarOperacion(() -> {

            // 1. Convertir DTO → dominio
            Cliente cliente = clienteMapper.toEntity(dto);

            // 2. Ejecutar caso de uso
            Cliente creado = clienteUseCase.crearCliente(cliente);

            // 3. Convertir dominio → DTO
            return clienteMapper.toDto(creado);

        }, "Crear Cliente");
    }

    /**
     * Obtiene clientes activos.
     *
     * @return lista de clientes activos
     */
    public OperationResult<List<ClienteDTO>> obtenerClientesActivos() {

        return ejecutarOperacion(() -> {

            List<Cliente> clientes = clienteUseCase.obtenerClientesActivos();

            return clientes.stream()
                    .map(clienteMapper::toDto)
                    .collect(Collectors.toList());

        }, "Obtener Clientes Activos");
    }

    /**
     * Busca cliente por nombre.
     *
     * @param nombre nombre del cliente
     * @return cliente encontrado
     */
    public OperationResult<ClienteDTO> buscarClientePorNombre(String nombre) {

        return ejecutarOperacion(() -> {

            Cliente cliente = clienteUseCase.buscarClientePorNombre(nombre);

            return clienteMapper.toDto(cliente);

        }, "Buscar Cliente por Nombre");
    }
}