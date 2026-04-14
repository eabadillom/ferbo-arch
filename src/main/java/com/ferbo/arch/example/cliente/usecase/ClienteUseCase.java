package com.ferbo.arch.example.cliente.usecase;

import java.util.List;

import com.ferbo.arch.core.usecase.UseCaseExecutor;
import com.ferbo.arch.example.cliente.domain.Cliente;
import com.ferbo.arch.example.cliente.respository.ClienteRepository;
import com.ferbo.tools.exception.BusinessException;
import com.ferbo.tools.exception.ValidationException;

/**
 * ClienteUseCase
 *
 * PROPÓSITO:
 * - Orquestar la lógica de negocio relacionada con Cliente
 * - Coordinar dominio + persistencia
 * - Manejar ejecución transaccional
 *
 * PRINCIPIOS:
 * - No contiene lógica de infraestructura
 * - No construye DTOs
 * - Usa entidades de dominio
 */
public class ClienteUseCase{

    private final ClienteRepository clienteRepository;
    private final UseCaseExecutor executor;

    /**
     * Constructor
     *
     * @param transactionManager gestor de transacciones
     * @param clienteRepository repositorio de cliente
     */
    public ClienteUseCase(ClienteRepository clienteRepository, UseCaseExecutor executor) {
        this.clienteRepository = clienteRepository;
        this.executor = executor;
    }

    // -------------------------------------------------------------------------
    // CASOS DE USO
    // -------------------------------------------------------------------------

    /**
     * Crea un nuevo cliente.
     *
     * FLUJO:
     * 1. Validar datos de entrada
     * 2. Validar reglas de dominio
     * 3. Persistir
     *
     * @param cliente entidad cliente
     * @return cliente persistido
     */
    public Cliente crearCliente(Cliente cliente) {

        return executor.executeTx(() -> {

            // 1. Validación básica
            if (cliente == null) {
                throw new ValidationException("El cliente no puede ser nulo");
            }

            // 2. Validaciones de dominio
            cliente.validarActivo();

            // 3. Persistencia
            return clienteRepository.save(cliente);
        });
    }

    /**
     * Obtiene todos los clientes activos.
     *
     * NO usa transacción porque es solo lectura.
     *
     * @return lista de clientes activos
     */
    public List<Cliente> obtenerClientesActivos() {

        return executor.execute(() ->
                clienteRepository.buscarClientesActivos()
        );
    }

    /**
     * Busca un cliente por nombre.
     *
     * @param nombre nombre del cliente
     * @return cliente encontrado
     */
    public Cliente buscarClientePorNombre(String nombre) {

        return executor.execute(() -> {

            if (nombre == null || nombre.trim().isEmpty()) {
                throw new ValidationException("El nombre es obligatorio");
            }

            return clienteRepository.buscarPorNombre(nombre)
                    .orElseThrow(() ->
                            new BusinessException("Cliente no encontrado"));
        });
    }
}