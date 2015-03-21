package at.cleancode.dynamicproperties;

import java.util.Map;

import org.springframework.beans.factory.BeanFactoryAware;

public interface DynamicPropertiesManager extends BeanFactoryAware {

    void propertiesChanged(Map<String, String> properties);

    Map<String, String> getCurrentProperties();
}
