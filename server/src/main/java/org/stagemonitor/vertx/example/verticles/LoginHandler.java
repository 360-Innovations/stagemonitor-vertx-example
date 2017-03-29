package org.stagemonitor.vertx.example.verticles;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.net.HttpURLConnection;

public class LoginHandler implements Handler<RoutingContext> {

    public static final String DB_SERVICE = "dbservice";

    protected Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void handle(RoutingContext context) {
        EventBus bus = context.vertx().eventBus();
        JsonObject requestBody = context.getBodyAsJson();
        String username = requestBody.getString("username");
        String password = requestBody.getString("password");

        if(username == null || password == null){
            logger.info("Failed: null params");
            context.fail(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        else {
            JsonObject json = new JsonObject(){{
                put("action", "checkCredentials");
                put("username", username);
                put("password", password);
            }};

            bus.send(DB_SERVICE, json, (Handler<AsyncResult<Message<JsonObject>>>) res -> {
                if(res.succeeded()){
                    Message<JsonObject> message = res.result();
                    boolean codeOk = message.body().getInteger("code") == HttpURLConnection.HTTP_OK;
                    JsonObject result = new JsonObject(){{
                        put("code", codeOk ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_UNAUTHORIZED);
                        put("result", codeOk ? "Login Succeeded" : "Login Failed");
                    }};

                    context.response().putHeader("Content-Type", "text/json");
                    context.response().putHeader("Content-Length", Integer.toString(result.toString().length()));
                    context.response().write(result.toString());
                    context.response().end();
                }
                else{
                    logger.info("Failed: failed result");
                    context.fail(HttpURLConnection.HTTP_UNAUTHORIZED);
                }
            });
        }
    }
}
