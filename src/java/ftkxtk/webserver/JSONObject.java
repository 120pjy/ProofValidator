package ftkxtk.webserver;

import java.util.HashMap;

public class JSONObject {
    private final HashMap<String, Object> content = new HashMap<>();

    public void set(String name, Object obj) {
        content.put(name, obj);
    }

    public Object get(String name) {
        if(content.containsKey(name))
            return content.get(name);
        return null;
    }

    @Override
    public String toString() {
        return "JSONObject{" +
                "content=" + content +
                '}';
    }
}
