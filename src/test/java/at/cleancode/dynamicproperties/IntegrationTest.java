package at.cleancode.dynamicproperties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class IntegrationTest {

    @Test
    public void testName() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);

        DemoService demoService = context.getBean(DemoService.class);

        DynamicPropertiesManager manager = context.getBean(DynamicPropertiesManager.class);
        Map<String, String> properties = new HashMap<>();
        properties.put("x", "${3*7}");
        properties.put("y", "foo");
        manager.propertiesChanged(properties);

        assertThat(demoService.x.toString()).isEqualTo("21");
        assertThat(demoService.x2).isEqualTo(21);
        assertThat(demoService.y).isEqualTo("foo");
        assertThat(demoService.done).isEqualTo(1);

        Map<String, String> newProperties = new HashMap<>();
        newProperties.put("x", "42");
        manager.propertiesChanged(newProperties);

        assertThat(demoService.x.toString()).isEqualTo("42");
        assertThat(demoService.x2).isEqualTo(42);
        assertThat(demoService.done).isEqualTo(2);
    }

    @Configuration
    public static class SpringConfiguration {

        @Bean public DemoService demoService() {
            return new DemoService();
        }

        @Bean public DynamicPropertiesPostProcessor postProcessor() {
            return new DynamicPropertiesPostProcessor();
        }

        @Bean
        public DynamicPropertiesManager dynamicPropertiesManager() {
            return new DuplicateFilteringDynamicPropertiesManager(new DefaultDynamicPropertiesManager());
        }

    }

    private static class DemoService {

        Object x;
        Integer x2;
        String y;
        int done;

        @DynamicProperty("x")
        public void setX(Object x) {
            this.x = x;
        }

        @DynamicProperty("x")
        public void setX2(Integer x) {
            this.x2 = x;
        }

        @DynamicProperty("y")
        public void setY(String y) {
            this.y = y;
        }

        @AfterDynamicPropertiesSet
        public void done() {
            this.done++;
        }

    }


}
