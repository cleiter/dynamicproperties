package at.cleancode.dynamicproperties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

public class DuplicateFilteringDynamicPropertiesManager implements DynamicPropertiesManager {

    private final DynamicPropertiesManager delegate;
    private final Map<String, String> currentProperties = new HashMap<>();

    public DuplicateFilteringDynamicPropertiesManager(DynamicPropertiesManager delegate) {
        this.delegate = Assert.argumentNotNull(delegate, "delegate");
    }

    @Override
    public void propertiesChanged(Map<String, String> properties) {
        Assert.argumentNotNull(properties, "properties");
        Map<String, String> changes = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (changed(entry)) {
                changes.put(entry.getKey(), entry.getValue());
            }
        }
        delegate.propertiesChanged(changes);
        currentProperties.putAll(properties);
    }

    @Override
    public Map<String, String> getCurrentProperties() {
        return currentProperties;
    }

    private boolean changed(Map.Entry<String, String> entry) {
        String currentValue = currentProperties.get(entry.getKey());
        return currentValue == null || !currentValue.equals(entry.getValue());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.argumentNotNull(beanFactory, "beanFactory");
        delegate.setBeanFactory(beanFactory);
    }

}
