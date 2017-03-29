package org.stagemonitor.vertx.example.verticles.rxjava;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import org.stagemonitor.vertx.example.database.DatabaseStub;

import java.util.Arrays;

public class DbService extends BaseVerticle {
    private static final String[] ACTIONS = new String[]{"checkCredentials", "getBookByName", "getBookAvailabilities"};

    @Override
    public void start() throws Exception {
        super.start();
        address = "dbservice";
        logger = LoggerFactory.getLogger(address);
        registerActions(Arrays.asList(ACTIONS));
    }

    public void checkCredentials(Message<JsonObject> message){
        DatabaseStub database = DatabaseStub.aquire();

        boolean accepted = database.checkCredentials(message.body().getString("username"), message.body().getString("password"));
        if(accepted){
            sendOK(message);
        }
        else{
            sendError(message, "Credentials invalid");
        }
    }

    public void getBookByName(Message<JsonObject> message){
        DatabaseStub database = DatabaseStub.aquire();

        JsonObject book = database.getBookByName(message.body().getString("name"));

        if(book != null){
            message.reply(book);
        }
        else{
            sendError(message, "Book does not exist");
        }
    }

    public void getBookAvailabilities(Message<JsonObject> message){
        DatabaseStub database = DatabaseStub.aquire();

        JsonObject avail = database.getBookAvailabilities(message.body().getString("name"));
        if(!avail.isEmpty()){
            message.reply(avail);
        }
    }
}
