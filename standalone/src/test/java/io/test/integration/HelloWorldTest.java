package io.test.integration;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

import static junit.framework.Assert.assertEquals;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class HelloWorldTest {

    @Test
    public void testHellowWorld() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/try-resteasy-standalone/hello");
        Response response = target.request().get();
        String text = response.readEntity(String.class);
        assertEquals("Hello, world!", text);
    }

    static class Task implements Callable<Boolean> {
        public static StringBuffer text = new StringBuffer(); // not thread safe
        private InputStream is;

        Task(InputStream is) {
            this.is = is;
        }

        @Override
        public Boolean call() throws Exception {
            int c;
            c = is.read();
            while (c != -1) {
                System.out.println(c);
                text.append((char) c);
                c = is.read();
            }
            return true; // result is useless
        }
    }

    @Test
    public void testRawApi() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Writer out = null;
        InputStream is = null;
        try {
            Socket socket = new Socket("localhost", 8080);
            OutputStream os = socket.getOutputStream();
            OutputStream buffered = new BufferedOutputStream(os);
            out = new OutputStreamWriter(buffered, "ASCII");
            out.write("GET /try-resteasy-standalone/hello HTTP/1.1\r\nAccept: */*\r\n\r\n");
            out.flush();
            is = socket.getInputStream();
            Future<Boolean> future = executor.submit(new Task(is));
            try {
                future.get(3, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                // do nothing
            } finally {
                // server won't block client
                assertEquals("HTTP/1.1 200 OK\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Content-Type: application/octet-stream\r\n" +
                        "Content-Length: 13\r\n\r\n" +
                        "Hello, world!", Task.text.toString());
            }
        } catch (Exception e) {
            // ignore
        } finally {
            try {
                out.close();
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }

    }
}
