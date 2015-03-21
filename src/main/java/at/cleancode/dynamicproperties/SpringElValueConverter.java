package at.cleancode.dynamicproperties;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpringElValueConverter implements ValueConverter {

    private final SpelExpressionParser parser = new SpelExpressionParser();
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
    public static final Pattern SYSTEM_EXPRESSION_PATTERN = Pattern.compile("#\\{(.+?)\\}");

    @Override
    public <T> T convert(String value, Class<T> type) {
        Assert.argumentNotNull(value, "value");
        Assert.argumentNotNull(type, "type");

        value = processSpringExpressions(value);
        value = processSpringSystemExpressions(value);

        Expression expression = parser.parseExpression("'" + value + "'");
        return expression.getValue(type);
    }

    private String processSpringExpressions(String value) {
        Matcher matcher = EXPRESSION_PATTERN.matcher(value);
        while (matcher.find()) {
            String match = matcher.group();
            String spel = matcher.group(1);
            value = value.replace(match, evaluate(spel));
        }
        return value;
    }

    private String processSpringSystemExpressions(String value) {
        Matcher matcher = SYSTEM_EXPRESSION_PATTERN.matcher(value);
        while (matcher.find()) {
            String match = matcher.group();
            String spel = matcher.group(1);
            value = value.replace(match, evaluateSystemExpression(spel));
        }
        return value;
    }

    private String evaluate(String value) {
        return parser.parseExpression(value).getValue(String.class);
    }

    private String evaluateSystemExpression(String value) {
        return parser.parseExpression(value).getValue(new Root(), String.class);
    }

    private static class Root {
        public Properties getSystemProperties() {
            return System.getProperties();
        }
    }

}
