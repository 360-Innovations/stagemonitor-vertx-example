package org.stagemonitor.vertx.example.verticles.rxjava;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

public abstract class BaseVerticle extends AbstractVerticle {

    protected EventBus bus;
    protected JsonObject config;
    protected Logger logger;
    protected String address;

    private HashMap<String, Method> actionMethodMap = new HashMap<>();

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        bus = this.vertx.eventBus();
        config = context.config();
    }

    private Method getMethodForAction(String action){
        if (actionMethodMap.containsKey(action)) {
            return actionMethodMap.get(action);
        } else {
            Method tmp = null;
            try {
                tmp = getClass().getDeclaredMethod(action, Message.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if(tmp != null){
                actionMethodMap.put(action, tmp);
            }
            return tmp;
        }
    }

    protected void registerActions(List<String> actions){
        if (address == null) {
            throw new IllegalStateException("Address must be initialized first");
        }
        logger = LoggerFactory.getLogger(address);

        bus.<JsonObject>consumer(address).toObservable().subscribe( message -> {
            String action = message.body().getString("action");

            if(actions.contains(action)){
                Method method = getMethodForAction(action);

                if (method == null){
                    sendError(message, "Action " + action + " not implemented");
                }
                else {
                    try {
                        method.invoke(this, message);
                    } catch (Exception e) {
                        logger.error("Error happened with action : " + action);
                        logger.error(e,e);
                        sendError(message, "Oups an error happenned on the server");
                    }
                }
            }
            else{
                sendError(message, "Action " + action + " not supported");
            }
        });
        logger.info(address + " started with actions " + String.join(", ", actions));
    }

    protected void sendStatus(Message<JsonObject> message, String status, int code) {
        sendStatus(message, status, code, new JsonObject());
    }

    private JsonObject constructBaseResponse(String status, int code){
        JsonObject response = new JsonObject();
        response.put("status", status);
        response.put("code", code);
        return response;
    }

    protected void sendStatus(Message<JsonObject> message, String status, int code, JsonObject json) {
        JsonObject response = constructBaseResponse(status, code);
        response.put("result", json);
        message.reply(response);
    }

    protected void sendStatus(Message<JsonObject> message, String status, int code, JsonArray jsonA) {
        JsonObject response = constructBaseResponse(status, code);
        response.put("result", jsonA);
        message.reply(response);
    }

    protected void sendBytes(Message<JsonObject> message, byte[] data) {
        JsonObject response = new JsonObject();
        response.put("bytes", data);
        message.reply(response);
    }

    protected void sendOK(Message<JsonObject> message, JsonObject json) {
        sendStatus(message, "ok", HttpURLConnection.HTTP_OK, json);
    }

    protected void sendOK(Message<JsonObject> message, JsonArray jsonA) {
        sendStatus(message, "ok", HttpURLConnection.HTTP_OK, jsonA);
    }

    protected void sendOK(Message<JsonObject> message) {
        sendOK(message, new JsonObject());
    }

    protected void sendUndefined(Message<JsonObject> message) {
        JsonObject response = constructBaseResponse("ok", HttpURLConnection.HTTP_OK);
        message.reply(response);
    }

    protected void sendError(Message<JsonObject> message, String error) {
        sendError(message, error, null);
    }

    protected void sendError(Message<JsonObject> message, String error, Exception e) {
        JsonObject json = new JsonObject();
        json.put("message", error);
        sendStatus(message, "error", HttpURLConnection.HTTP_INTERNAL_ERROR, json);
    }

    protected boolean getOptionalBooleanConfig(String fieldName, boolean defaultValue) {
        Boolean b = config.getBoolean(fieldName);
        return b == null ? defaultValue : b.booleanValue();
    }

    protected String getOptionalStringConfig(String fieldName, String defaultValue) {
        String s = config.getString(fieldName);
        return s == null ? defaultValue : s;
    }

    protected int getOptionalIntConfig(String fieldName, int defaultValue) {
        Number i = config.getInteger(fieldName);
        return i == null ? defaultValue : i.intValue();
    }

    protected long getOptionalLongConfig(String fieldName, long defaultValue) {
        Number l = config.getLong(fieldName);
        return l == null ? defaultValue : l.longValue();
    }

    protected JsonObject getOptionalObjectConfig(String fieldName, JsonObject defaultValue) {
        JsonObject o = config.getJsonObject(fieldName);
        return o == null ? defaultValue : o;
    }

    protected JsonArray getOptionalArrayConfig(String fieldName, JsonArray defaultValue) {
        JsonArray a = config.getJsonArray(fieldName);
        return a == null ? defaultValue : a;
    }

    protected boolean getMandatoryBooleanConfig(String fieldName) {
        Boolean b = config.getBoolean(fieldName);
        if (b == null) {
            // TODO busmod do not exist no more
            throw new IllegalArgumentException(fieldName + " must be specified in config for busmod");
        }
        return b;
    }

    protected String getMandatoryStringConfig(String fieldName) {
        String s = config.getString(fieldName);
        if (s == null) {
            throw new IllegalArgumentException(fieldName + " must be specified in config for busmod");
        }
        return s;
    }

    protected int getMandatoryIntConfig(String fieldName) {
        Number i = config.getInteger(fieldName);
        if (i == null) {
            throw new IllegalArgumentException(fieldName + " must be specified in config for busmod");
        }
        return i.intValue();
    }

    protected long getMandatoryLongConfig(String fieldName) {
        Number l = config.getLong(fieldName);
        if (l == null) {
            throw new IllegalArgumentException(fieldName + " must be specified in config for busmod");
        }
        return l.longValue();
    }

}
