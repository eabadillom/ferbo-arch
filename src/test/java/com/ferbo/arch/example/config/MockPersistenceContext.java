package com.ferbo.arch.example.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.ferbo.arch.example.cliente.domain.Cliente;
import com.ferbo.arch.persistence.PersistenceContext;

/**
 * Mock simple de PersistenceContext usando Map interno
 */
public final class MockPersistenceContext implements PersistenceContext {

    private final Map<Long, Cliente> db = new HashMap<>();
    private long idSequence = 1;

    @Override
    public <T, ID> Optional<T> find(Class<T> clazz, Object id) {
        if (clazz.equals(Cliente.class)) {
            return Optional.ofNullable(
                    clazz.cast(db.get((Long) id)));
        }
        return Optional.empty();
    }

    @Override
    public <T> T save(T entity) {
        if (entity instanceof Cliente) {
            Cliente c = (Cliente) entity;
            if (c.getId() == null) {
                try {
                    java.lang.reflect.Field f = Cliente.class.getDeclaredField("id");
                    f.setAccessible(true);
                    f.set(c, idSequence++);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            db.put(c.getId(), c);
            return entity;
        }
        return entity;
    }

    @Override
    public <T> void delete(T entity) {
        if (entity instanceof Cliente) {
            Cliente c = (Cliente) entity;
            db.remove(c.getId());
        }
    }

    @Override
    public <T> boolean contains(T entity) {
        if (entity instanceof Cliente) {
            Cliente c = (Cliente) entity;
            return db.containsKey(c.getId());
        }
        return false;
    }

    public Collection<Cliente> findAll() {
        return db.values();
    }
}