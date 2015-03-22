# Spring Dynamic Properties

This project provides support for dynamically changing properties in spring applications.

**Please be aware that this project is still in alpha phase.**

## Usage

Annotate setter of your spring components with `@DynamicProperty`:

```java
class Service {

    @DynamicProperty("x")
    public void setX(int x) {
        this.x = x;
    }

}
```

Whenever a property source propagates a change to the dynamic property `x` this method is called with the new value.

If you need a callback whenever one or more properties of a component where changed you can annotate a no-argument
method with `@AfterDynamicPropertiesSet`.
This method will be called exactly once, even when multiple properties changed at a time.
If you need to do expensive work when properties change (i.e. create a new connection pool) this is the place to do it.


## How it works

TODO

## Design Goals

I wanted a solution that is only dependent on annotations but does not introduce some new wrapper type for dynamic
 properties. That is, instead of

```java
class Service {

    private DynamicProperty<Boolean> myProperty; // magically set somewhere

    public void foo() {
        if (myProperty.get()) { // non-transparent: call to get() is always required
            // do something
        }
    }

}
```

you can write

```java
class Service {

    private boolean myProperty;

    public void foo() {
        if (myProperty) {
            // do something
        }
    }

    @DynamicProperty("myProperty")
    public void setMyProperty(boolean myProperty) {
        this.myProperty = myProperty;
    }

}
```

This may seem more verbose, but doesn't need new special types and - more importantly - works without a special
framework by just calling the appropriate setter.


## Requirements

* Java 7
* Spring 4


## License

See [LICENSE](LICENSE).
