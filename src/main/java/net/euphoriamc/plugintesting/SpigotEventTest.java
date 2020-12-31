package net.euphoriamc.plugintesting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark a method as a Spigot Event Test.
 * This will make the method run either once or everytime the specified event is called.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SpigotEventTest {
    /**
     * Define the event to register the method to.
     * @return Event
     */
    Class<?> event();
    boolean ignoreCancel() default false;
    boolean runOnce() default true;
}
