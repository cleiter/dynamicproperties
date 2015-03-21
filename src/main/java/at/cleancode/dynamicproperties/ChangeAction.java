package at.cleancode.dynamicproperties;

import java.lang.reflect.Method;

public class ChangeAction {
    private final Object bean;
    private final Method method;

    public ChangeAction(Object bean, Method method) {
        this.bean = Assert.argumentNotNull(bean, "bean");
        this.method = Assert.argumentNotNull(method, "method");
    }

    public void performChange(String value, ValueConverter valueConverter) {
        try {
            Object converted = valueConverter.convert(value, method.getParameterTypes()[0]);
            method.invoke(bean, converted);
        } catch (Exception e) {
            throw new DynamicPropertiesException(String.format("Error invoking method %s on %s",
                    method.getName(), method.getDeclaringClass()), e);
        }
    }

}
