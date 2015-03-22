package at.cleancode.dynamicproperties;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class ChangeAction {
    private final WeakReference<Object> beanRef;
    private final Method method;

    public ChangeAction(Object bean, Method method) {
        this.beanRef = new WeakReference<>(Assert.argumentNotNull(bean, "bean"));
        this.method = Assert.argumentNotNull(method, "method");
    }

    public void performChange(String value, ValueConverter valueConverter) {
        try {
            Object bean = beanRef.get();
            if (bean != null) {
                Object converted = valueConverter.convert(value, method.getParameterTypes()[0]);
                method.invoke(bean, converted);
            }
        } catch (Exception e) {
            throw new DynamicPropertiesException(String.format("Error invoking method %s on %s",
                    method.getName(), method.getDeclaringClass()), e);
        }
    }

}
