package at.cleancode.dynamicproperties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class DefaultDynamicPropertiesManagerTest {

    BeanFactory beanFactory = mock(BeanFactory.class);
    DynamicPropertiesPostProcessor postProcessor = mock(DynamicPropertiesPostProcessor.class);
    ValueConverter valueConverter = mock(ValueConverter.class);

    ChangeAction changeAction1 = mock(ChangeAction.class);
    ChangeAction changeAction2 = mock(ChangeAction.class);
    ChangeAction changeAction3 = mock(ChangeAction.class);
    AfterChangeAction afterChangeAction = mock(AfterChangeAction.class);

    DefaultDynamicPropertiesManager manager = new DefaultDynamicPropertiesManager();

    @Before
    public void setUp() throws Exception {
        when(beanFactory.getBean(DynamicPropertiesPostProcessor.class)).thenReturn(postProcessor);
        when(beanFactory.getBean(ValueConverter.class)).thenReturn(valueConverter);
        manager.setBeanFactory(beanFactory);
    }

    @Test
    public void executes_single_change_action() throws Exception {
        DynamicPropertiesMapping mapping = new DynamicPropertiesMapping();
        mapping.addChangeAction("foo", changeAction1);
        mapping.addChangeAction("bar", changeAction2);
        mapping.addChangeAction("buz", changeAction3);
        when(postProcessor.getMapping()).thenReturn(mapping);

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("bar", "x");
        manager.propertiesChanged(changedProperties);

        verify(changeAction2).performChange("x", valueConverter);
        verify(changeAction1, never()).performChange(any(String.class), any(ValueConverter.class));
        verify(changeAction3, never()).performChange(any(String.class), any(ValueConverter.class));
    }

    @Test
    public void executes_all_change_actions_if_property_mapped_more_than_once() throws Exception {
        DynamicPropertiesMapping mapping = new DynamicPropertiesMapping();
        mapping.addChangeAction("foo", changeAction1);
        mapping.addChangeAction("bar", changeAction2);
        mapping.addChangeAction("bar", changeAction3);
        when(postProcessor.getMapping()).thenReturn(mapping);

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("bar", "x");
        manager.propertiesChanged(changedProperties);

        verify(changeAction2).performChange("x", valueConverter);
        verify(changeAction3).performChange("x", valueConverter);
        verify(changeAction1, never()).performChange(any(String.class), any(ValueConverter.class));
    }

    @Test
    public void executes_change_actions_when_multiple_properties_changed() throws Exception {
        DynamicPropertiesMapping mapping = new DynamicPropertiesMapping();
        mapping.addChangeAction("foo", changeAction1);
        mapping.addChangeAction("bar", changeAction2);
        mapping.addChangeAction("buz", changeAction3);
        when(postProcessor.getMapping()).thenReturn(mapping);

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("foo", "y");
        changedProperties.put("bar", "x");
        manager.propertiesChanged(changedProperties);

        verify(changeAction1).performChange("y", valueConverter);
        verify(changeAction2).performChange("x", valueConverter);
        verify(changeAction3, never()).performChange(any(String.class), any(ValueConverter.class));
    }

    @Test
    public void executes_no_after_change_actions_if_no_match() throws Exception {
        DynamicPropertiesMapping mapping = new DynamicPropertiesMapping();
        mapping.addChangeAction("foo", changeAction1);
        mapping.addAfterChangeAction("bar", afterChangeAction);
        when(postProcessor.getMapping()).thenReturn(mapping);

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("foo", "y");
        manager.propertiesChanged(changedProperties);

        verifyZeroInteractions(afterChangeAction);
    }

    @Test
    public void executes_after_change_actions_if_match() throws Exception {
        DynamicPropertiesMapping mapping = new DynamicPropertiesMapping();
        mapping.addChangeAction("foo", changeAction1);
        mapping.addAfterChangeAction("foo", afterChangeAction);
        when(postProcessor.getMapping()).thenReturn(mapping);

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("foo", "y");
        manager.propertiesChanged(changedProperties);

        verify(afterChangeAction).notifyChange();
    }

    @Test
    public void executes_after_change_actions_only_once_even_if_multiple_properties_changed() throws Exception {
        DynamicPropertiesMapping mapping = new DynamicPropertiesMapping();
        mapping.addChangeAction("foo", changeAction1);
        mapping.addChangeAction("bar", changeAction2);
        mapping.addAfterChangeAction("foo", afterChangeAction);
        mapping.addAfterChangeAction("bar", afterChangeAction);
        when(postProcessor.getMapping()).thenReturn(mapping);

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("foo", "y");
        changedProperties.put("bar", "y");
        manager.propertiesChanged(changedProperties);

        verify(afterChangeAction).notifyChange();
    }

}
