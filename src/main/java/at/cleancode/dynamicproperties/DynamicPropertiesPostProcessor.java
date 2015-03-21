package at.cleancode.dynamicproperties;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class DynamicPropertiesPostProcessor implements BeanPostProcessor {

    private final DynamicPropertiesMapping mapping = new DynamicPropertiesMapping();

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        detectDynamicProperties(o);
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

    public DynamicPropertiesMapping getMapping() {
        return mapping;
    }

    private void detectDynamicProperties(Object o) {
        Class<?> type = o.getClass();
        Method[] declaredMethods = type.getDeclaredMethods();
        Set<String> definedProperties = new HashSet<>();
        for (Method method : declaredMethods) {
            DynamicProperty annotation = method.getAnnotation(DynamicProperty.class);
            if (annotation != null) {
                checkMethodSignature(method);
                String property = annotation.value();
                mapping.addChangeAction(property, new ChangeAction(o, method));
                definedProperties.add(property);
            }
        }
        if (!definedProperties.isEmpty()) {
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(AfterDynamicPropertiesSet.class)) {
                    checkAfterSetMethodSignature(method);
                    for (String property : definedProperties) {
                        mapping.addAfterChangeAction(property, new AfterChangeAction(o, method));
                    }
                }
            }
        }
    }

    private void checkMethodSignature(Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(String.format(
                    "Expected exactly 1 parameter for method annotated with %s, offending method: %s",
                    DynamicProperty.class.getSimpleName(),
                    method));
        }
    }

    private void checkAfterSetMethodSignature(Method method) {
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException(String.format(
                    "Expected no parameter for method annotated with %s, offending method: %s",
                    AfterDynamicPropertiesSet.class.getSimpleName(),
                    method));
        }
    }

}
