package io.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */

@Path("/hello")
public class HelloWorld {

    @GET
    public String sayHello() {
        return "Hello, world!";
    }
}
