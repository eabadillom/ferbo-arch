package com.ferbo.arch.example.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ferbo.arch.example.cliente.dto.ClienteDTO;
import com.ferbo.arch.example.cliente.mapper.ClienteMapper;
import com.ferbo.arch.example.cliente.mgr.ClienteMGR;
import com.ferbo.arch.example.cliente.respository.ClienteRepository;
import com.ferbo.arch.example.cliente.usecase.ClienteUseCase;
import com.ferbo.arch.example.config.MockPersistenceContext;
import com.ferbo.arch.example.config.MockTransactionManager;
import com.ferbo.tools.result.OperationResult;

public class ClienteFlowTest {

    private ClienteMGR clienteMGR;
    private ClienteUseCase clienteUseCase;
    private ClienteRepository clienteRepository;
    private ClienteMapper clienteMapper;

    private MockPersistenceContext persistenceContext;
    private MockTransactionManager transactionManager;

    @BeforeEach
    public void setup() {
        // Crear mocks
        persistenceContext = new MockPersistenceContext();
        transactionManager = new MockTransactionManager();

        // Crear repository y mapper
        clienteRepository = new ClienteRepository(persistenceContext);
        clienteMapper = new ClienteMapper();

        // Crear use case
        clienteUseCase = new ClienteUseCase(transactionManager, clienteRepository);

        // Crear MGR
        clienteMGR = new ClienteMGR(clienteUseCase, clienteMapper);
    }

    // -------------------------------------------------------------------------
    // TEST: Crear cliente
    // -------------------------------------------------------------------------
    @Test
    public void testCrearCliente() {
        ClienteDTO dto = new ClienteDTO(null, "Juan Perez", true);

        OperationResult<ClienteDTO> resultado = clienteMGR.crearCliente(dto);

        assertTrue(resultado.isSuccess());
        assertNotNull(resultado.getData().getId());
        assertEquals("Juan Perez", resultado.getData().getNombre());
        assertTrue(resultado.getData().getActivo());
    }

    // -------------------------------------------------------------------------
    // TEST: Obtener clientes activos
    // -------------------------------------------------------------------------
    @Test
    public void testObtenerClientesActivos() {
        // Crear clientes
        ClienteDTO dto1 = new ClienteDTO(null, "Cliente 1", true);
        ClienteDTO dto2 = new ClienteDTO(null, "Cliente 2", false);
        clienteMGR.crearCliente(dto1);
        clienteMGR.crearCliente(dto2);

        OperationResult<List<ClienteDTO>> resultado = clienteMGR.obtenerClientesActivos();

        assertTrue(resultado.isSuccess());
        assertEquals(1, resultado.getData().size());
        assertEquals("Cliente 1", resultado.getData().get(0).getNombre());
    }

    // -------------------------------------------------------------------------
    // TEST: Buscar cliente por nombre
    // -------------------------------------------------------------------------
    @Test
    public void testBuscarClientePorNombre() {
        ClienteDTO dto = new ClienteDTO(null, "BuscarMe", true);
        clienteMGR.crearCliente(dto);

        OperationResult<ClienteDTO> resultado = clienteMGR.buscarClientePorNombre("BuscarMe");

        assertTrue(resultado.isSuccess());
        assertEquals("BuscarMe", resultado.getData().getNombre());
    }

} 
