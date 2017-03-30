package org.stagemonitor.vertx.example.database;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseStub {
    private static DatabaseStub ourInstance = new DatabaseStub();

    public static DatabaseStub aquire() {
        return ourInstance;
    }

    private JsonArray acceptedCredentials;
    private HashMap<String, JsonObject> books;
    private HashMap<String, HashMap<String, Integer>> availabilities;

    private DatabaseStub() {
        acceptedCredentials = new JsonArray(new ArrayList<JsonObject>(){{
            add(new JsonObject(){{
                put("username", "test1");
                put("password", "test1");
            }});
            add(new JsonObject(){{
                put("username", "test2");
                put("password", "test2");
            }});
        }});

        books = new HashMap<String, JsonObject>(){{
            put("book1", new JsonObject(){{
                put("name", "book1");
                put("author", "author1");
                put("price", 12.50);
            }});
            put("book2", new JsonObject(){{
                put("name", "book2");
                put("author", "author2");
                put("price", 15.50);
            }});
        }};

        availabilities = new HashMap<String, HashMap<String, Integer>>(){{
            put("Montreal", new HashMap<String, Integer>(){{
                put("book1", 2);
                put("book2", 3);
            }});
            put("Laval", new HashMap<String, Integer>(){{
                put("book1", 3);
                put("book2", 2);
            }});
            put("Repentigny", new HashMap<String, Integer>(){{
                put("book1", 2);
                put("book2", 0);
            }});
            put("Terrebonne", new HashMap<String, Integer>(){{
                put("book1", 0);
                put("book2", 2);
            }});
        }};
    }

    public boolean checkCredentials(String username, String password){
        boolean accepted = false;
        JsonObject user;
        for (int i = 0; i < acceptedCredentials.size() && ! accepted; i++) {
            user = acceptedCredentials.getJsonObject(i);
            accepted = username.equals(user.getString("username")) && password.equals(user.getString("password"));
        }
        return accepted;
    }

    public JsonObject getBookByName(String name){
        return books.get(name);
    }

    public JsonObject getBookAvailabilities(String name){
        JsonObject avail = new JsonObject();
        int count;
        for(String city : availabilities.keySet()){
            count = availabilities.get(city).get(name);
            if(count > 0){
                avail.put(city, count);
            }
        }
        return avail;
    }
}
