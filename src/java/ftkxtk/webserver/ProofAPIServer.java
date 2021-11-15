package ftkxtk.webserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class ProofAPIServer {
    private static final String SERV_ADDR = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        HttpServer server = HttpServer.create(new InetSocketAddress(SERV_ADDR, PORT), 0);
        server.createContext("/api", new ProofAPIHandler());
        server.setExecutor(threadPoolExecutor);
        server.start();
        System.out.println(" Server started on port " + PORT);
    }
}
