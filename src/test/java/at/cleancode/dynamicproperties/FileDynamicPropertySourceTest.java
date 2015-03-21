package at.cleancode.dynamicproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

public class FileDynamicPropertySourceTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
    DynamicPropertiesManager manager = Mockito.mock(DynamicPropertiesManager.class);

    @Before
    public void setUp() throws Exception {
        when(applicationContext.getBean(DynamicPropertiesManager.class)).thenReturn(manager);
    }

    @Test
    public void propagates_properties_on_startup() throws Exception {
        File file = folder.newFile("test.dynprops");
        FileDynamicPropertySource source = new FileDynamicPropertySource(file);
        Files.write(file.toPath(), "foo=bar".getBytes());

        source.onApplicationEvent(contextRefreshedEvent());

        verify(manager).propertiesChanged(any(Map.class));
    }

    @Test
    public void propagates_properties_when_file_changed() throws Exception {
        File file = folder.newFile("test.dynprops");
        FileDynamicPropertySource source = new FileDynamicPropertySource(file);

        source.onApplicationEvent(contextRefreshedEvent());
        Thread.sleep(200);

        write(file, "foo=bar");
        Thread.sleep(200);

        write(file, "foo=bar");
        Thread.sleep(200);

        verify(manager, atLeast(2)).propertiesChanged(any(Map.class));
    }

    @Test
    public void does_not_propagate_no_properties() throws Exception {
        File file = folder.newFile("test.dynprops");
        FileDynamicPropertySource source = new FileDynamicPropertySource(file);

        source.onApplicationEvent(contextRefreshedEvent());

        verify(manager, never()).propertiesChanged(any(Map.class));
    }

    @Test
    public void propagates_correct_properties() throws Exception {
        File file = folder.newFile("test.dynprops");
        FileDynamicPropertySource source = new FileDynamicPropertySource(file);
        write(file, "foo=bar\nx=y");

        source.onApplicationEvent(contextRefreshedEvent());

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(manager).propertiesChanged(mapCaptor.capture());
        assertThat(mapCaptor.getValue())
                .contains(entry("foo", "bar"))
                .contains(entry("x", "y"))
                .hasSize(2);
    }

    private void write(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes());
    }

    private ContextRefreshedEvent contextRefreshedEvent() {
        return new ContextRefreshedEvent(applicationContext);
    }

}
