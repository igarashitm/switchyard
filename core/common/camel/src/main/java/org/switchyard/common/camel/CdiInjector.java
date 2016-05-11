package org.switchyard.common.camel;

import org.apache.camel.spi.Injector;

import javax.enterprise.inject.spi.BeanManager;

/**
 * Injector implementation which performs injection with CDI bean provider.
 */
final class CdiInjector implements Injector {

    private final Injector _injector;

    private final BeanManager _manager;

    CdiInjector(Injector injector, BeanManager manager) {
        this._injector = injector;
        this._manager = manager;
    }

    @Override
    public <T> T newInstance(Class<T> type) {
        T instance = CdiManagerHelper.getReferenceByType(_manager, type);
        if (instance != null) {
            return instance;
        } else {
            return _injector.newInstance(type);
        }
    }

    @Override
    public <T> T newInstance(Class<T> type, Object instance) {
        return _injector.newInstance(type, instance);
    }
}
