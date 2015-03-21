package at.cleancode.dynamicproperties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class SpringElValueConverterTest {

    SpringElValueConverter converter = new SpringElValueConverter();

    @Test
    public void convert_to_boolean() throws Exception {
        assertThat(converter.convert("true", boolean.class)).isTrue();
        assertThat(converter.convert("true", Boolean.class)).isTrue();
        assertThat(converter.convert("false", boolean.class)).isFalse();
        assertThat(converter.convert("false", Boolean.class)).isFalse();
    }

    @Test
    public void int_arithmetic() throws Exception {
        assertThat(converter.convert("${2*21}", Integer.class)).isEqualTo(42);
    }

    @Test
    public void string() throws Exception {
        assertThat(converter.convert("foo", String.class)).isEqualTo("foo");
    }

    @Test
    public void string_multiple_values() throws Exception {
        assertThat(converter.convert("foo ${7} ${'bar'}", String.class)).isEqualTo("foo 7 bar");
    }

    @Test
    public void method_calls() throws Exception {
        assertThat(converter.convert("${'foo'.getBytes()}", byte[].class)).isEqualTo("foo".getBytes());
    }

    @Test
    public void bean_property() throws Exception {
        assertThat(converter.convert("${'foo'.bytes}", byte[].class)).isEqualTo("foo".getBytes());
    }

    @Test
    public void system_property() throws Exception {
        System.setProperty("foo", "bar");
        assertThat(converter.convert("#{systemProperties['foo']}", String.class)).isEqualTo("bar");
    }

}
