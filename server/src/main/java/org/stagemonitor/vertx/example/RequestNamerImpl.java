package org.stagemonitor.vertx.example;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.stagemonitor.vertx.utils.RequestNamer;

public class RequestNamerImpl implements RequestNamer {
    @Override
    public String getRequestName(Message<?> message) {
        JsonObject body = (JsonObject) message.body();
        return message.address() + "." + body.getString("action");
    }
}
