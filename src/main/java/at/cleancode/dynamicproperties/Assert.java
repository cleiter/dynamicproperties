package at.cleancode.dynamicproperties;

class Assert {

    static <T> T argumentNotNull(T argument, String argumentName) {
        if (argument == null) {
            throw new IllegalArgumentException("Argument '" + argumentName + "' must not be null");
        }
        return argument;
    }

}
