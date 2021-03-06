package ftkxtk.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ftkxtk.validator.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

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
                String message = "Parse Exception: " + e.getMessage() +" on line " + e.getLine();
                if (e.getLineString() != null)
                    message += "\nexpression: "+e.getLineString();
                message  += " at index " + e.getIndex();
                res(exchange, message);
            } catch(AnalyzeException e) {
                String message = "Analyze Exception: " + e.getMessage() ;
                message += "\nexpression: " + e.getActual();
                message += " at " + e.getPosition();
//                if (e.getExpected() != null)
//                    message += "\nexpected: "+e.getExpected();
                res(exchange, message);
            } catch(Exception e) {
                String message = "Unknown Exception: " + e.getMessage();
                e.printStackTrace();
                res(exchange, message);
            }
        } else {
            res(exchange, 400, "proof field required");
        }
    }

    private void res(HttpExchange httpExchange, String message) throws IOException {
        res(httpExchange, 200, message);
    }

    private void res(HttpExchange httpExchange, int status, String message) throws IOException{
        String processedMessage = message.replace("\n", "\\n");
        OutputStream out = httpExchange.getResponseBody();
        String json = "{\"message\": \"" + processedMessage + "\"}";
        httpExchange.getResponseHeaders().set("Content-Type", "appication/json");
        httpExchange.sendResponseHeaders(status, json.length());
        out.write(json.getBytes());
        out.flush();
        out.close();
    }
}