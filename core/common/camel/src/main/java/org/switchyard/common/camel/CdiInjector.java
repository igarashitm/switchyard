package org.switchyard.common.camel;

import org.apache.camel.spi.Injector;

import javax.enterprise.inject.spi.BeanManager;

/**
 * Injector implementation which performs injection with CDI bean provider.
 */
final class CdiInjector implements Injector {

    private final Injector injector;

    private final BeanManager manager;

    CdiInjector(Injector injector, BeanManager manager) {
        this.injector = injector;
        this.manager = manager;
    }

    @Override
    public <T> T newInstance(Class<T> type) {
        T instance = CdiManagerHelper.getReferenceByType(manager, type);
        if (instance != null) {
            return instance;
        } else {
            return injector.newInstance(type);
        }
    }

    @Override
    public <T> T newInstance(Class<T> type, Object instance) {
        return injector.newInstance(type, instance);
    }
}