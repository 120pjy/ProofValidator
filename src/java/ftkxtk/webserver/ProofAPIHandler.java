package ftkxtk.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ftkxtk.validator.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProofAPIHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        if ("POST".equals(exchange.getRequestMethod())) {
            post(exchange);
        } else {
            res(exchange, 400, "bad request");
        }
    }

    private void get(HttpExchange exchange) {
    }

    private void post(HttpExchange exchange) throws IOException {
        JSONParser jsonParser = new JSONParser();
        String body = new String(exchange.getRequestBody().readAllBytes());
        System.out.println(body);
        JSONObject obj = jsonParser.parse(body);
        if(obj.get("proof") instanceof String) {
            try {
                Lexer lexer = new Lexer((String) obj.get("proof"));
                Parser parser = new Parser(lexer.lex());
                Analyzer analyzer = new Analyzer();
                analyzer.visit(parser.parseSource());
                res(exchange, "no error has been found on this proof.");
            } catch(ParseException e) {
                res(exchange, "Parse Exception: " + e.getMessage() + " at " + e.getIndex());
            } catch(AnalyzeException e) {
                res(exchange, "Analyze Exception: " + e.getMessage() + " at " + e.getPosition());
            }
        } else {
            res(exchange, 400, "proof field required");
        }
    }

    private void res(HttpExchange httpExchange, String message) throws IOException {
        res(httpExchange, 200, message);
    }

    private void res(HttpExchange httpExchange, int status, String message) throws IOException{
        OutputStream out = httpExchange.getResponseBody();
        String json = "{\"message\": \"" + message + "\"}";
        httpExchange.getResponseHeaders().set("Content-Type", "appication/json");
        httpExchange.sendResponseHeaders(status, json.length());
        out.write(json.getBytes());
        out.flush();
        out.close();
    }
}