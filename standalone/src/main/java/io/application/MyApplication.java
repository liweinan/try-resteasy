package io.application;

import io.resources.HelloWorld;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class MyApplication extends Application {
    private Set<Class<?>> classes = new HashSet<Class<?>>();

    @Override
    public Set<Class<?>> getClasses() {
        classes.add(HelloWorld.class);
        return classes;
    }
}
