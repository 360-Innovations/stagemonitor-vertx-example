package org.stagemonitor.vertx.example.verticles;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;

import java.util.Arrays;

public class BookService extends BaseVerticle{
    public static final String DB_SERVICE = "dbservice";

    private static final String[] ACTIONS = new String[]{"getBookInfo"};

    @Override
    public void start() throws Exception {
        super.start();
        address = "example.bookService";
        logger = LoggerFactory.getLogger(address);
        registerActions(Arrays.asList(ACTIONS));
    }

    public void getBookInfo(Message<JsonObject> message){
        String bookName = message.body().getString("name");
        if(!bookName.isEmpty()){
            JsonObject getBookJson = new JsonObject(){{
                put("action", "getBookByName");
                put("name", bookName);
            }};
            JsonObject getAvailJson = new JsonObject(){{
                put("action", "getBookAvailabilities");
                put("name", bookName);
            }};

            bus.send(DB_SERVICE, getBookJson, resBook ->{
                if(resBook.succeeded()){
                    bus.send(DB_SERVICE, getAvailJson, resAvail -> {
                        if(resAvail.succeeded()){
                            JsonObject response = (JsonObject) resBook.result().body();
                            if(response.getInteger("code") == null) {
                                JsonObject avail = (JsonObject) resAvail.result().body();
                                if(avail.getInteger("code") == null) {
                                    response.put("availabilities", avail);
                                }
                                else{
                                    response = avail;
                                }
                            }

                            message.reply(response);
                        }
                        else{
                            logger.error("Error while getting book",resAvail.cause());
                        }
                    });
                }
                else{
                    logger.error("Error while getting book",resBook.cause());
                }
            });
        }
        else{
            sendError(message, "The name cannot be empty");
        }
    }
}
