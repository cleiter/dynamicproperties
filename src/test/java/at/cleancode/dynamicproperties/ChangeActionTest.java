package at.cleancode.dynamicproperties;


import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class ChangeActionTest {

    @Test
    public void notify_change_works() throws Exception {
        final AtomicBoolean b = new AtomicBoolean();
        Object bean = new Object() {
            public void foo(String s) {
                assertThat(s).isEqualTo("X");
                b.set(true);
            }
        };
        Method method = bean.getClass().getMethod("foo", String.class);
        ChangeAction ca = new ChangeAction(bean, method);

        ca.performChange("x", new DummyValueConverter());

        assertThat(b.get()).isTrue();
    }

    @Test(expected = DynamicPropertiesException.class)
    public void throws_dynamic_properties_exception_if_execution_did_not_work() throws Exception {
        Object o = new Object();
        Method method = o.getClass().getDeclaredMethod("clone"); // protected
        ChangeAction ca = new ChangeAction(o, method);

        ca.performChange("x", new DummyValueConverter());
    }

    private static class DummyValueConverter implements ValueConverter {
        @Override
        public <T> T convert(String value, Class<T> type) {
            return (T) value.toUpperCase();
        }
    }

}
