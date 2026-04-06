package com.ferbo.arch.example.cliente.respository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    private Map<Long, Cliente> clientes = new HashMap<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Constructor
     *
     * @param persistenceContext contexto de persistencia agnóstico
     */
    public ClienteRepository(PersistenceContext persistenceContext) {
        super(Cliente.class, persistenceContext);
    }

    /**
     * Guarda un cliente en el repositorio en memoria.
     *
     * Propósito:
     * - Simular la persistencia de un cliente sin necesidad de una base de datos
     * real.
     * - Asignar automáticamente un ID único al cliente si aún no tiene uno.
     * - Mantener el cliente en la estructura interna (HashMap) para que pueda
     * ser consultado posteriormente por otros métodos como
     * buscarClientesActivos() o buscarPorNombre().
     *
     * Nota:
     * - Este método reemplaza la implementación original de BaseRepository para
     * fines de pruebas unitarias.
     * - No realiza operaciones de base de datos real; todo se maneja en memoria.
     *
     * @param cliente el objeto Cliente a guardar
     * @return el cliente guardado, con ID asignado si era nulo
     */
    @Override
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(idGenerator.getAndIncrement());
        }
        clientes.put(cliente.getId(), cliente);
        return cliente;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS ORIENTADOS A NEGOCIO
    // -------------------------------------------------------------------------

    /**
     * Obtiene todos los clientes que están activos en el repositorio en memoria.
     *
     * Propósito:
     * - Simular la obtención de clientes activos sin acceder a una base de datos
     * real.
     * - Permitir que los tests unitarios verifiquen la lógica de negocio que
     * depende de clientes activos.
     *
     * Nota:
     * - En una implementación real, esto sería una query (JPQL, SQL, etc.).
     * - Aquí se filtra directamente sobre la estructura interna en memoria.
     *
     * @return lista de clientes activos
     */
    public List<Cliente> buscarClientesActivos() {
        return clientes.values().stream()
                .filter(Cliente::getActivo)
                .collect(Collectors.toList());
    }

    /**
     * Busca un cliente por su nombre en el repositorio en memoria.
     *
     * Propósito:
     * - Simular la búsqueda de un cliente sin necesidad de una base de datos.
     * - Permitir que los tests unitarios verifiquen la búsqueda de clientes
     * por nombre.
     *
     * Nota:
     * - La búsqueda no distingue mayúsculas/minúsculas (equalsIgnoreCase).
     * - En una implementación real, esto sería una query (JPQL, SQL, etc.).
     *
     * @param nombre el nombre del cliente a buscar
     * @return un Optional con el cliente encontrado, o vacío si no existe
     */
    public Optional<Cliente> buscarPorNombre(String nombre) {
        return clientes.values().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }
}