package at.cleancode.dynamicproperties;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Objects;

public class AfterChangeAction {

    private final WeakReference<Object> beanRef;
    private final Method method;

    public AfterChangeAction(Object bean, Method method) {
        this.beanRef= new WeakReference(Assert.argumentNotNull(bean, "bean"));
        this.method = Assert.argumentNotNull(method, "method");
    }

    public void notifyChange() {
        try {
            Object bean = beanRef.get();
            if (bean != null) {
                method.invoke(bean);
            }
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
        return Objects.equals(this.beanRef.get(), other.beanRef.get()) &&
                Objects.equals(this.method, other.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanRef.get(), method);
    }

}
