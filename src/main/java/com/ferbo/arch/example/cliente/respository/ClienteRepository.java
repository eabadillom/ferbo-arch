package com.ferbo.arch.example.cliente.respository;

import java.util.List;
import java.util.Optional;

import com.ferbo.arch.example.cliente.domain.Cliente;
import com.ferbo.arch.persistence.BaseRepository;
import com.ferbo.arch.persistence.PersistenceContext;

/**
 * ClienteRepository
 *
 * PROPÓSITO:
 * - Gestionar la persistencia de Cliente
 * - Extender comportamiento genérico de BaseRepository
 * - Exponer consultas orientadas a negocio
 *
 * PRINCIPIOS:
 * - No contiene lógica de negocio
 * - No maneja transacciones
 * - No depende de frameworks específicos
 */
public class ClienteRepository extends BaseRepository<Cliente, Long> {

    /**
     * Constructor
     *
     * @param persistenceContext contexto de persistencia agnóstico
     */
    public ClienteRepository(PersistenceContext persistenceContext) {
        super(Cliente.class, persistenceContext);
    }

    // -------------------------------------------------------------------------
    // MÉTODOS ORIENTADOS A NEGOCIO
    // -------------------------------------------------------------------------

    /**
     * Obtiene todos los clientes activos.
     *
     * NOTA:
     * - En una implementación real, esto sería una query (JPQL, SQL, etc.)
     * - Aquí lo dejamos preparado para futura implementación
     */
    public List<Cliente> buscarClientesActivos() {
        throw new UnsupportedOperationException(
                "buscarClientesActivos no implementado aún"
        );
    }

    /**
     * Busca un cliente por nombre.
     *
     * @param nombre nombre del cliente
     * @return Optional con el cliente encontrado
     */
    public Optional<Cliente> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
                "buscarPorNombre no implementado aún"
        );
    }
}