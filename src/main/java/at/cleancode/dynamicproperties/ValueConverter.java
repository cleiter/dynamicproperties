package at.cleancode.dynamicproperties;

public interface ValueConverter {

    <T> T convert(String value, Class<T> type);

}
