package org.stagemonitor.vertx.example.verticles.rxjava;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

import java.util.Arrays;

public class BookService extends BaseVerticle {
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

            Observable<Message<JsonObject>> obsBook = bus.sendObservable(DB_SERVICE, getBookJson);
            Observable<Message<JsonObject>> obsAvail = bus.sendObservable(DB_SERVICE, getAvailJson);

            Observable.zip(obsBook, obsAvail, (msgBook, msgAvail) -> {
                JsonObject response = msgBook.body();
                if(response.getInteger("code") == null) {
                    if(msgAvail.body().getInteger("code") == null) {
                        response.put("availabilities", msgAvail.body());
                    }
                    else{
                        response = msgAvail.body();
                    }
                }
                return response;
            }).subscribe(message::reply);
        }
        else{
            sendError(message, "The name cannot be empty");
        }
    }
}
