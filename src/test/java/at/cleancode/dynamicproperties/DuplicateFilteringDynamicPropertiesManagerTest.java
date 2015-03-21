package at.cleancode.dynamicproperties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

public class DuplicateFilteringDynamicPropertiesManagerTest {

    DummyDynamicPropertiesManager delegate = new DummyDynamicPropertiesManager();
    DuplicateFilteringDynamicPropertiesManager manager = new DuplicateFilteringDynamicPropertiesManager(delegate);

    @Test
    public void passes_all_properties_on_first_invocation() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("foo", "bar");
        properties.put("x", "y");

        manager.propertiesChanged(properties);

        assertThat(delegate.getCurrentProperties()).isEqualTo(properties);
    }

    @Test
    public void passes_only_changed_properties_on_further_invocations() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("foo", "bar");
        properties.put("x", "y");

        manager.propertiesChanged(properties);

        properties.put("x", "z");
        properties.put("a", "b");

        manager.propertiesChanged(properties);

        Map<String, String> changedAndNewProperties = new HashMap<>();
        changedAndNewProperties.put("x", "z");
        changedAndNewProperties.put("a", "b");

        assertThat(delegate.getCurrentProperties()).isEqualTo(changedAndNewProperties);
    }

    private static class DummyDynamicPropertiesManager implements DynamicPropertiesManager {

        private Map<String, String> properties;

        @Override
        public void propertiesChanged(Map<String, String> properties) {
            this.properties = properties;
        }

        @Override
        public Map<String, String> getCurrentProperties() { return properties; }

        @Override public void setBeanFactory(BeanFactory beanFactory) throws BeansException { }
    }

}
