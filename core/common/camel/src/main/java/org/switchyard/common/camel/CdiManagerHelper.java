package org.switchyard.common.camel;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

final class CdiManagerHelper {

    private CdiManagerHelper() {
    }

    static <T> Set<T> getReferencesByType(BeanManager manager, Class<T> type, Annotation... qualifiers) {
        Set<T> references = new HashSet<T>();
        for (Bean<?> bean : manager.getBeans(type, qualifiers)) {
            references.add(getReference(manager, type, bean));
        }
        return references;
    }

    static <T> T getReferenceByName(BeanManager manager, String name, Class<T> type) {
        Set<Bean<?>> beans = manager.getBeans(name);
        if (beans == null || beans.isEmpty()) {
            return null;
        }
        return getReference(manager, type, manager.resolve(beans));
    }

    static <T> T getReferenceByType(BeanManager manager, Class<T> type, Annotation... qualifiers) {
        Set<Bean<?>> beans = manager.getBeans(type, qualifiers);
        if (beans == null || beans.isEmpty()) {
            return null;
        }
        return getReference(manager, type, manager.resolve(beans));
    }

    static <T> T getReference(BeanManager manager, Class<T> type, Bean<?> bean) {
        return type.cast(manager.getReference(bean, type, manager.createCreationalContext(bean)));
    }
}
