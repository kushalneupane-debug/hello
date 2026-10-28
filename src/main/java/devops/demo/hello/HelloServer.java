package devops.demo.hello;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HelloServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            String html = "<html><head><title>DevOps Demo</title></head>"
                + "<body style='font-family:Arial;text-align:center;margin-top:80px;background:#f8f9fb'>"
                + "<h1 style='color:#336699'>Hello, DevOps!</h1>"
                + "<p>Welcome to our first DevOps demo application.</p>"
                + "<p>My First CI/CD pipeline has been successfully built and deployed. It works!</p>"
                + "<p style='color:gray'>&copy; DevOps Demo | Computer Science - Kushal Neupane</p>"
                + "</body></html>";
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        });
        server.start();
    }
}
