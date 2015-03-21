package at.cleancode.dynamicproperties;


import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class AfterChangeActionTest {

    @Test
    public void equals_and_hashcode_must_be_same() throws Exception {
        Object o = new Object();
        Object o2 = new Object();
        Method method = o.getClass().getMethod("toString");
        AfterChangeAction aca1 = new AfterChangeAction(o, method);
        AfterChangeAction aca2 = new AfterChangeAction(o, method);
        AfterChangeAction aca3 = new AfterChangeAction(o2, method);

        assertThat(aca1).isEqualTo(aca2);
        assertThat(aca1.hashCode()).isEqualTo(aca2.hashCode());

        assertThat(aca1).isNotEqualTo(aca3);
        assertThat(aca1.hashCode()).isNotEqualTo(aca3.hashCode());
    }

    @Test
    public void notify_change_works() throws Exception {
        final AtomicBoolean b = new AtomicBoolean();
        Runnable r = new Runnable() {
            @Override public void run() {
                b.set(true);
            }
        };
        Method method = r.getClass().getMethod("run");
        AfterChangeAction aca = new AfterChangeAction(r, method);

        aca.notifyChange();

        assertThat(b.get()).isTrue();
    }

    @Test(expected = DynamicPropertiesException.class)
    public void throws_dynamic_properties_exception_if_execution_did_not_work() throws Exception {
        Object o = new Object();
        Method method = o.getClass().getDeclaredMethod("clone"); // protected
        AfterChangeAction aca = new AfterChangeAction(o, method);

        aca.notifyChange();
    }

}
