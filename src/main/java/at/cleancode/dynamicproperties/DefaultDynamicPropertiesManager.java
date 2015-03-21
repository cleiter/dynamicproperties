package at.cleancode.dynamicproperties;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DefaultDynamicPropertiesManager implements DynamicPropertiesManager, BeanFactoryAware {

    private DynamicPropertiesPostProcessor postProcessor;
    private ValueConverter valueConverter;

    @Override
    public void propertiesChanged(Map<String, String> properties) {
        Assert.argumentNotNull(properties, "properties");
        DynamicPropertiesMapping mapping = postProcessor.getMapping();
        Set<AfterChangeAction> allAfterChangeActions = new HashSet<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String property = entry.getKey();
            String value = entry.getValue();
            List<ChangeAction> changeActions = mapping.getChangeActions(property);
            for (ChangeAction changeAction : changeActions) {
                changeAction.performChange(value, valueConverter);
            }
            allAfterChangeActions.addAll(mapping.getAfterChangeActions(property));
        }
        for (AfterChangeAction afterChangeAction : allAfterChangeActions) {
            afterChangeAction.notifyChange();
        }
    }

    @Override
    public Map<String, String> getCurrentProperties() {
        return null; // this implementation has no knowledge of the current properties
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.argumentNotNull(beanFactory, "beanFactory");

        try {
            postProcessor = beanFactory.getBean(DynamicPropertiesPostProcessor.class);
        } catch (NoUniqueBeanDefinitionException e) {
            throw new DynamicPropertiesException("It seems like you installed multiple beans of type DynamicPropertiesPostProcessor", e);
        } catch (NoSuchBeanDefinitionException e) {
            throw new DynamicPropertiesException("It seems like you did not install a bean of type DynamicPropertiesPostProcessor", e);
        }

        try {
            valueConverter = beanFactory.getBean(ValueConverter.class);
        } catch (NoUniqueBeanDefinitionException e) {
            throw new DynamicPropertiesException("It seems like you installed multiple beans of type ValueConverter", e);
        } catch (NoSuchBeanDefinitionException e) {
            valueConverter = new SpringElValueConverter();
        }
    }

}
