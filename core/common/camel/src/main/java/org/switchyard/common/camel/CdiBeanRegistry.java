package org.switchyard.common.camel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.util.ObjectHelper;

/**
 * CdiBeanRegistry used by Camel to perform lookup into the CDI {@link javax.enterprise.inject.spi.BeanManager}.
 */
public class CdiBeanRegistry implements Registry {

    private final static AnnotationLiteral<Any> ANY = new AnnotationLiteral<Any>() {};

    private final BeanManager _manager;

    CdiBeanRegistry(BeanManager manager) {
        this._manager = manager;
    }

    @Override
    public Object lookupByName(String name) {
        ObjectHelper.notEmpty(name, "name");
        // Work-around for WELD-2089
        if ("properties".equals(name) && findByTypeWithName(PropertiesComponent.class).containsKey("properties")) {
            return CdiManagerHelper.getReferenceByName(_manager, name, PropertiesComponent.class);
        }
        return CdiManagerHelper.getReferenceByName(_manager, name, Object.class);
    }

    @Override
    public <T> T lookupByNameAndType(String name, Class<T> type) {
        ObjectHelper.notEmpty(name, "name");
        ObjectHelper.notNull(type, "type");
        return CdiManagerHelper.getReferenceByName(_manager, name, type);
    }

    @Override
    public <T> Map<String, T> findByTypeWithName(Class<T> type) {
        ObjectHelper.notNull(type, "type");
        Map<String, T> references = new HashMap<String, T>();
        for (Bean<?> bean : _manager.getBeans(type, ANY)) {
            if (bean.getName() != null) {
                references.put(bean.getName(), CdiManagerHelper.getReference(_manager, type, bean));
            }
        }
        return references;
    }

    @Override
    public <T> Set<T> findByType(Class<T> type) {
        ObjectHelper.notNull(type, "type");
        return CdiManagerHelper.getReferencesByType(_manager, type, ANY);
    }

    @Override
    public Object lookup(String name) {
        return lookupByName(name);
    }

    @Override
    public <T> T lookup(String name, Class<T> type) {
        return lookupByNameAndType(name, type);
    }

    @Override
    public <T> Map<String, T> lookupByType(Class<T> type) {
        return findByTypeWithName(type);
    }
}
