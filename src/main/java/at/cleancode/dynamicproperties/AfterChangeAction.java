package at.cleancode.dynamicproperties;

import java.lang.reflect.Method;
import java.util.Objects;

public class AfterChangeAction {

    private final Object bean;
    private final Method method;

    public AfterChangeAction(Object bean, Method method) {
        this.bean = Assert.argumentNotNull(bean, "bean");
        this.method = Assert.argumentNotNull(method, "method");
    }

    public void notifyChange() {
        try {
            method.invoke(bean);
        } catch (Exception e) {
            throw new DynamicPropertiesException(String.format("Error invoking method %s on %s",
                    method.getName(), method.getDeclaringClass()), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AfterChangeAction)) {
            return false;
        }
        AfterChangeAction other = (AfterChangeAction) o;
        return Objects.equals(this.bean, other.bean) &&
                Objects.equals(this.method, other.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bean, method);
    }

}
